/*
 * creedengo - Python language - Provides rules to reduce the environmental footprint of your Python programs
 * Copyright © 2024 Green Code Initiative (https://green-code-initiative.org)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.greencodeinitiative.creedengo.python.checks;

import org.greencodeinitiative.creedengo.python.utils.UtilsAST;
import org.sonar.check.Rule;
import org.sonar.plugins.python.api.PythonSubscriptionCheck;
import org.sonar.plugins.python.api.SubscriptionContext;
import org.sonar.plugins.python.api.symbols.ClassSymbol;
import org.sonar.plugins.python.api.tree.Name;
import org.sonar.plugins.python.api.tree.ClassDef;
import org.sonar.plugins.python.api.tree.CallExpression;
import org.sonar.plugins.python.api.tree.Tree;
import org.sonar.plugins.python.api.tree.Expression;
import org.sonar.plugins.python.api.tree.RegularArgument;
import org.sonar.plugins.python.api.tree.FunctionDef;
import org.sonar.plugins.python.api.tree.Argument;
import org.sonar.plugins.python.api.tree.AssignmentStatement;
import org.sonar.plugins.python.api.tree.BaseTreeVisitor;
import org.sonar.plugins.python.api.tree.QualifiedExpression;
import org.sonar.plugins.python.api.tree.Statement;

import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;



import static org.sonar.plugins.python.api.tree.Tree.Kind.ASSIGNMENT_STMT;
import static org.sonar.plugins.python.api.tree.Tree.Kind.CALL_EXPR;
import static org.sonar.plugins.python.api.tree.Tree.Kind.NAME;
import static org.sonar.plugins.python.api.tree.Tree.Kind.REGULAR_ARGUMENT;
import static org.sonar.plugins.python.api.tree.Tree.Kind.FUNCDEF;

@Rule(key="GCI101")
public class AvoidConvBiasBeforeBatchNorm extends PythonSubscriptionCheck {

  private static final String NN_MODULE_FULLY_QUALIFIED_NAME = "torch.nn.Module";
  private static final String NN_MODULE_FULLY_QUALIFIED_NAME_DETAILED = "torch.nn.modules.module.Module";  // New in sonar-python 5.17+
  private static final String CONV_FULLY_QUALIFIED_NAME = "torch.nn.Conv2d";
  private static final String FORWARD_METHOD_NAME = "forward";
  private static final String BATCH_NORM_FULLY_QUALIFIED_NAME = "torch.nn.BatchNorm2d";
  private static final String SEQUENTIAL_MODULE_FULLY_QUALIFIED_NAME = "torch.nn.Sequential";
  protected static final String MESSAGE = "Remove bias for convolutions before batch norm layers to save time and memory.";

  /**
   * Check if a fully qualified name matches the expected name.
   * Handles both old format (torch.nn.Conv2d) and new format (torch.nn.modules.conv.Conv2d).
   *
   * @param actualName The actual fully qualified name from the API
   * @param expectedShortName The expected short format name (e.g., "torch.nn.Conv2d")
   * @return true if it matches
   */
  private static boolean matchesQualifiedName(String actualName, String expectedShortName) {
    if (actualName == null || expectedShortName == null) {
      return false;
    }
    // Direct match (old API format)
    if (actualName.equals(expectedShortName)) {
      return true;
    }
    // Check if it ends with the class name (new API format)
    // e.g., "torch.nn.modules.conv.Conv2d" should match "torch.nn.Conv2d"
    String className = expectedShortName.substring(expectedShortName.lastIndexOf('.') + 1);
    return actualName.endsWith("." + className) && actualName.startsWith("torch.nn");
  }

  @Override
  public void initialize(Context context) {
    context.registerSyntaxNodeConsumer(Tree.Kind.CLASSDEF, ctx -> {
      ClassDef classDef = (ClassDef) ctx.syntaxNode();
      Optional.ofNullable(classDef).filter(this::isModelClass).ifPresent(e -> visitModelClass(ctx, e));
    });
  }

  private boolean isConvWithBias(CallExpression convDefinition) {
    RegularArgument biasArgument = UtilsAST.nthArgumentOrKeyword(7, "bias", convDefinition.arguments());
    if (biasArgument == null)
      return true;
    else {
      Expression expression = biasArgument.expression();
      return expression.is(NAME) && "True".equals(((Name) expression).name());
    }
  }

  private boolean isModelClass(ClassDef classDef) {
    ClassSymbol classSymbol = (ClassSymbol) classDef.name().symbol();
    if (classSymbol != null) {
      boolean hasTorchNNModuleParent = classSymbol.superClasses().stream()
        .anyMatch(e -> Objects.equals(e.fullyQualifiedName(), NN_MODULE_FULLY_QUALIFIED_NAME)
                    || Objects.equals(e.fullyQualifiedName(), NN_MODULE_FULLY_QUALIFIED_NAME_DETAILED)
                    || "Module".equals(e.name()));
      boolean hasForwardMethod = classSymbol.declaredMembers().stream()
        .anyMatch(e -> FORWARD_METHOD_NAME.equals(e.name()));
      return hasTorchNNModuleParent && hasForwardMethod;
    } else {
      // Fallback: check if class inherits from nn.Module by looking at the arguments list directly
      return isModelClassByArguments(classDef);
    }
  }

  private boolean isModelClassByArguments(ClassDef classDef) {
    if (classDef.args() == null || classDef.args().arguments().isEmpty()) {
      return false;
    }
    // Check if any parent class name contains "Module"
    boolean hasModuleParent = classDef.args().arguments().stream()
      .filter(arg -> arg.is(Tree.Kind.REGULAR_ARGUMENT))
      .map(arg -> ((RegularArgument) arg).expression())
      .anyMatch(expr -> {
        if (expr.is(Tree.Kind.QUALIFIED_EXPR)) {
          QualifiedExpression qe = (QualifiedExpression) expr;
          return "Module".equals(qe.name().name());
        } else if (expr.is(Tree.Kind.NAME)) {
          return "Module".equals(((Name) expr).name());
        }
        return false;
      });

    // Check if class has forward method
    boolean hasForwardMethod = classDef.body().statements().stream()
      .filter(stmt -> stmt.is(Tree.Kind.FUNCDEF))
      .map(stmt -> (FunctionDef) stmt)
      .anyMatch(func -> FORWARD_METHOD_NAME.equals(func.name().name()));

    return hasModuleParent && hasForwardMethod;
  }

  private void reportIfBatchNormIsCalledAfterDirtyConv(SubscriptionContext context, FunctionDef forwardDef, Map<String, CallExpression> dirtyConvInInit,
    Map<String, CallExpression> batchNormsInInit) {
    ForwardMethodVisitor visitor = new ForwardMethodVisitor();
    forwardDef.accept(visitor);

    for (CallExpression callInForward : visitor.callExpressions) {
      // if it is a batchNorm
      if (batchNormsInInit.containsKey(UtilsAST.getMethodName(callInForward))) {
        int batchNormLineNo = callInForward.firstToken().line();
        for (Argument batchNormArgument : UtilsAST.getArgumentsFromCall(callInForward)) {
          Expression batchNormArgumentExpression = ((RegularArgument) batchNormArgument).expression();
          if (batchNormArgumentExpression.is(CALL_EXPR)) {
            String functionName = UtilsAST.getMethodName((CallExpression) batchNormArgumentExpression);
            if (dirtyConvInInit.containsKey(functionName)) {
              context.addIssue(dirtyConvInInit.get(functionName), MESSAGE);
            }

            // if it uses a variable
          } else if (batchNormArgumentExpression.is(NAME) && ((Name) batchNormArgumentExpression).isVariable()) {
            String batchNormArgumentName = ((Name) batchNormArgumentExpression).name();

            // loop through all call expressions in forward
            AssignmentStatement lastAssignmentStatementBeforeBatchNorm = null;

            for (AssignmentStatement assignmentStatement : visitor.assignmentStatements) {
              Name variable = (Name) assignmentStatement.lhsExpressions().get(0).expressions().get(0);
              String variableName = variable.name();
              if (assignmentStatement.firstToken().line() >= batchNormLineNo)
                break;

              if (variableName.equals(batchNormArgumentName))
                lastAssignmentStatementBeforeBatchNorm = assignmentStatement;
            }
            if (lastAssignmentStatementBeforeBatchNorm != null && lastAssignmentStatementBeforeBatchNorm.assignedValue().is(CALL_EXPR)) {
              CallExpression function = (CallExpression) lastAssignmentStatementBeforeBatchNorm.assignedValue();
              String functionName = UtilsAST.getMethodName(function);
              if (dirtyConvInInit.containsKey(functionName)) {
                context.addIssue(dirtyConvInInit.get(functionName), MESSAGE);
              }
            }
          }
        }
      }
    }
  }

  private void reportForSequentialModules(SubscriptionContext context, CallExpression sequentialCall) {
    int moduleIndex = 0;
    int nModulesInSequential = UtilsAST.getArgumentsFromCall(sequentialCall).size();
    while (moduleIndex < nModulesInSequential) {
      Argument moduleInSequential = UtilsAST.getArgumentsFromCall(sequentialCall).get(moduleIndex);
      if (moduleInSequential.is(REGULAR_ARGUMENT) && ((RegularArgument) moduleInSequential).expression().is(CALL_EXPR)) {
        CallExpression module = (CallExpression) ((RegularArgument) moduleInSequential).expression();
        if (matchesQualifiedName(UtilsAST.getQualifiedName(module), CONV_FULLY_QUALIFIED_NAME) && isConvWithBias(module)) {
          if (moduleIndex == nModulesInSequential - 1)
            break;
          Argument nextModuleInSequential = UtilsAST.getArgumentsFromCall(sequentialCall).get(moduleIndex + 1);
          CallExpression nextModule = (CallExpression) ((RegularArgument) nextModuleInSequential).expression();
          if (matchesQualifiedName(UtilsAST.getQualifiedName(nextModule), BATCH_NORM_FULLY_QUALIFIED_NAME))
            context.addIssue(module, MESSAGE);
        }
      }
      moduleIndex += 1;
    }
  }

  private void visitModelClass(SubscriptionContext context, ClassDef classDef) {
    Map<String, CallExpression> dirtyConvInInit = new HashMap<>();
    Map<String, CallExpression> batchNormsInInit = new HashMap<>();

    for (Statement s : classDef.body().statements()) {
      if (s.is(FUNCDEF) && "__init__".equals(((FunctionDef) s).name().name())) {
        for (Statement ss : ((FunctionDef) s).body().statements()) {
          if (ss.is(ASSIGNMENT_STMT)) {
            Expression lhs = ((AssignmentStatement) ss).lhsExpressions().get(0).expressions().get(0);
            // consider only calls (modules)
            if (!((AssignmentStatement) ss).assignedValue().is(CALL_EXPR))
              continue;
            CallExpression callExpression = (CallExpression) ((AssignmentStatement) ss).assignedValue();
            String variableName = ((QualifiedExpression) lhs).name().name();
            String variableClass = UtilsAST.getQualifiedName(callExpression);
            if (matchesQualifiedName(variableClass, SEQUENTIAL_MODULE_FULLY_QUALIFIED_NAME)) {
              reportForSequentialModules(context, callExpression);
            } else if (matchesQualifiedName(variableClass, CONV_FULLY_QUALIFIED_NAME) && isConvWithBias(callExpression)) {
              dirtyConvInInit.put(variableName, callExpression);
            } else if (matchesQualifiedName(variableClass, BATCH_NORM_FULLY_QUALIFIED_NAME)) {
              batchNormsInInit.put(variableName, callExpression);
            }
          }
        }
      }
    }
    for (Statement s : classDef.body().statements()) {
      if (s.is(FUNCDEF) && FORWARD_METHOD_NAME.equals(((FunctionDef) s).name().name())) {
        FunctionDef forwardDef = (FunctionDef) s;
        reportIfBatchNormIsCalledAfterDirtyConv(context, forwardDef, dirtyConvInInit, batchNormsInInit);
      }
    }

  }

  private static class ForwardMethodVisitor extends BaseTreeVisitor {
    private final ArrayList<CallExpression> callExpressions = new ArrayList<>();
    private final ArrayList<AssignmentStatement> assignmentStatements = new ArrayList<>();

    @Override
    public void visitCallExpression(CallExpression pyCallExpressionTree) {
      callExpressions.add(pyCallExpressionTree);
      super.visitCallExpression(pyCallExpressionTree);
    }

    public void visitAssignmentStatement(AssignmentStatement pyAssignmentStatementTree) {
      assignmentStatements.add(pyAssignmentStatementTree);
      super.visitAssignmentStatement(pyAssignmentStatementTree);
    }
  }
}