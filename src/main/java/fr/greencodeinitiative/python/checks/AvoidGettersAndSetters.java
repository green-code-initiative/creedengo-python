/*
 * creedengo - Python language - Provides rules to reduce the environmental footprint of your Python programs
 * Copyright © 2023 Green Code Initiative (https://green-code-initiative.org)
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
package fr.greencodeinitiative.python.checks;

import java.util.List;
import java.util.stream.Collectors;

import org.sonar.check.Rule;
import org.sonar.plugins.python.api.PythonSubscriptionCheck;
import org.sonar.plugins.python.api.SubscriptionContext;
import org.sonar.plugins.python.api.tree.AnyParameter;
import org.sonar.plugins.python.api.tree.AssignmentStatement;
import org.sonar.plugins.python.api.tree.FunctionDef;
import org.sonar.plugins.python.api.tree.ParameterList;
import org.sonar.plugins.python.api.tree.QualifiedExpression;
import org.sonar.plugins.python.api.tree.ReturnStatement;
import org.sonar.plugins.python.api.tree.Statement;
import org.sonar.plugins.python.api.tree.StatementList;
import org.sonar.plugins.python.api.tree.Tree;
import org.sonarsource.analyzer.commons.annotations.DeprecatedRuleKey;

@Rule(key = "GCI7")
@DeprecatedRuleKey(repositoryKey = "gci-python", ruleKey = "D7")
public class AvoidGettersAndSetters extends PythonSubscriptionCheck {

    public static final String DESCRIPTION = "Avoid creating getter and setter methods in classes";

    @Override
    public void initialize(Context context) {
        context.registerSyntaxNodeConsumer(Tree.Kind.FUNCDEF, ctx -> {
            FunctionDef functionDef = (FunctionDef) ctx.syntaxNode();

            if (isConstructorMethod(functionDef)) {
                return; // Ignore constructors
            }

            StatementList statementList = functionDef.body();
            List<Statement> statements = statementList.statements();
            if (functionDef.parent().parent().is(Tree.Kind.CLASSDEF)) {
                checkAllGetters(statements, functionDef, ctx);
                checkAllSetters(statements, functionDef, ctx);
            }
        });
    }

    private boolean isConstructorMethod(FunctionDef functionDef) {
        return functionDef.name() != null && "__init__".equals(functionDef.name().name());
    }

    public void checkAllSetters(List<Statement> statements, FunctionDef functionDef, SubscriptionContext ctx) {
        if (statements.size() == 1 && statements.get(0).is(Tree.Kind.ASSIGNMENT_STMT)) {
            AssignmentStatement assignmentStatement = (AssignmentStatement) statements.get(0);
            if (checkIfStatementIsQualifiedExpressionAndStartsWithSelfDot((QualifiedExpression) assignmentStatement.children().get(0).children().get(0))) {
                // Check if assignedValue is a parameter of the function
                ParameterList parameters = functionDef.parameters();
                if (parameters != null && !parameters.all().stream().filter(p -> checkAssignementFromParameter(assignmentStatement, p)).collect(Collectors.toList()).isEmpty()) {
                    ctx.addIssue(functionDef.defKeyword(), AvoidGettersAndSetters.DESCRIPTION);
                }
            }
        }
    }

    public void checkAllGetters(List<Statement> statements, FunctionDef functionDef, SubscriptionContext ctx) {
        Statement lastStatement = statements.get(statements.size() - 1);
        if (lastStatement.is(Tree.Kind.RETURN_STMT)) {
            List<Tree> returnStatementChildren = ((ReturnStatement) lastStatement).children();
            if (returnStatementChildren.get(1).is(Tree.Kind.QUALIFIED_EXPR) &&
                    checkIfStatementIsQualifiedExpressionAndStartsWithSelfDot((QualifiedExpression) returnStatementChildren.get(1))) {
                ctx.addIssue(functionDef.defKeyword(), AvoidGettersAndSetters.DESCRIPTION);
            }
        }
    }

    public boolean checkAssignementFromParameter(AssignmentStatement assignmentStatement, AnyParameter parameter) {
        String parameterToString = parameter.firstToken().value();
        return assignmentStatement.assignedValue().firstToken().value().equalsIgnoreCase(parameterToString);
    }

    public boolean checkIfStatementIsQualifiedExpressionAndStartsWithSelfDot(QualifiedExpression qualifiedExpression) {
        List<Tree> qualifedExpressionChildren = qualifiedExpression.children();
        return qualifedExpressionChildren.size() == 3 &&
                qualifedExpressionChildren.get(0).firstToken().value().equalsIgnoreCase("self") &&
                qualifedExpressionChildren.get(1).firstToken().value().equalsIgnoreCase(".");
    }
}
