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


import org.sonar.check.Priority;
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

  public static final String RULE_KEY = "P4";
  private static final String nnModuleFullyQualifiedName = "torch.nn.Module";
  private static final String convFullyQualifiedName = "torch.nn.Conv2d";
  private static final String forwardMethodName = "forward";
  private static final String batchNormFullyQualifiedName = "torch.nn.BatchNorm2d";
  private static final String sequentialModuleFullyQualifiedName = "torch.nn.Sequential";
  protected static final String MESSAGE = "Remove bias for convolutions before batch norm layers to save time and memory.";

  @Override
  public void initialize(Context context) {
    context.registerSyntaxNodeConsumer(Tree.Kind.CLASSDEF, ctx -> {
      ClassDef classDef = (ClassDef) ctx.syntaxNode();
      Optional.ofNullable(classDef).filter(this::isModelClass).ifPresent(e -> visitModelClass(ctx, e));
    });
  }

  private boolean isConvWithBias(CallExpression convDefinition) {
    RegularArgument biasArgument = Utils.nthArgumentOrKeyword(7, "bias", convDefinition.arguments());
    if (biasArgument == null)
      return true;
    else {
      Expression expression = biasArgument.expression();
      return expression.is(NAME) && ((Name) expression).name().equals("True");
    }
  }

  private boolean isModelClass(ClassDef classDef) {
    ClassSymbol classSymbol = (ClassSymbol) classDef.name().symbol();
    if (classSymbol != null) {
      return classSymbol.superClasses().stream().anyMatch(e -> Objects.equals(e.fullyQualifiedName(), nnModuleFullyQualifiedName))
        && classSymbol.declaredMembers().stream().anyMatch(e -> e.name().equals(forwardMethodName));
    } else
      return false;
  }

  private void reportIfBatchNormIsCalledAfterDirtyConv(SubscriptionContext context, FunctionDef forwardDef, Map<String, CallExpression> dirtyConvInInit,
    Map<String, CallExpression> batchNormsInInit) {
    ForwardMethodVisitor visitor = new ForwardMethodVisitor();
    forwardDef.accept(visitor);

    for (CallExpression callInForward : visitor.callExpressions) {
      // if it is a batchNorm
      if (batchNormsInInit.containsKey(Utils.getMethodName(callInForward))) {
        int batchNormLineNo = callInForward.firstToken().line();
        for (Argument batchNormArgument : Utils.getArgumentsFromCall(callInForward)) {
          Expression batchNormArgumentExpression = ((RegularArgument) batchNormArgument).expression();
          if (batchNormArgumentExpression.is(CALL_EXPR)) {
            String functionName = Utils.getMethodName((CallExpression) batchNormArgumentExpression);
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
              String functionName = Utils.getMethodName(function);
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
    int nModulesInSequential = Utils.getArgumentsFromCall(sequentialCall).size();
    while (moduleIndex < nModulesInSequential) {
      Argument moduleInSequential = Utils.getArgumentsFromCall(sequentialCall).get(moduleIndex);
      if (moduleInSequential.is(REGULAR_ARGUMENT) && ((RegularArgument) moduleInSequential).expression().is(CALL_EXPR)) {
        CallExpression module = (CallExpression) ((RegularArgument) moduleInSequential).expression();
        if (Utils.getQualifiedName(module).equals(convFullyQualifiedName) && isConvWithBias(module)) {
          if (moduleIndex == nModulesInSequential - 1)
            break;
          Argument nextModuleInSequential = Utils.getArgumentsFromCall(sequentialCall).get(moduleIndex + 1);
          CallExpression nextModule = (CallExpression) ((RegularArgument) nextModuleInSequential).expression();
          if (Utils.getQualifiedName(nextModule).equals(batchNormFullyQualifiedName))
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
      if (s.is(FUNCDEF) && ((FunctionDef) s).name().name().equals("__init__")) {
        for (Statement ss : ((FunctionDef) s).body().statements()) {
          if (ss.is(ASSIGNMENT_STMT)) {
            Expression lhs = ((AssignmentStatement) ss).lhsExpressions().get(0).expressions().get(0);
            // consider only calls (modules)
            if (!((AssignmentStatement) ss).assignedValue().is(CALL_EXPR))
              break;
            CallExpression callExpression = (CallExpression) ((AssignmentStatement) ss).assignedValue();
            String variableName = ((QualifiedExpression) lhs).name().name();
            String variableClass = Utils.getQualifiedName(callExpression);
            if (variableClass.equals(sequentialModuleFullyQualifiedName)) {
              reportForSequentialModules(context, callExpression);
            } else if (convFullyQualifiedName.contains(variableClass) && isConvWithBias(callExpression)) {
              dirtyConvInInit.put(variableName, callExpression);
            } else if (batchNormFullyQualifiedName.contains(variableClass)) {
              batchNormsInInit.put(variableName, callExpression);
            }
          }
        }
      }
    }
    for (Statement s : classDef.body().statements()) {
      if (s.is(FUNCDEF) && ((FunctionDef) s).name().name().equals(forwardMethodName)) {
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