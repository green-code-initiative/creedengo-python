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


import org.sonar.check.Rule;
import org.sonar.plugins.python.api.PythonSubscriptionCheck;
import org.sonar.plugins.python.api.SubscriptionContext;
import org.sonar.plugins.python.api.tree.Expression;
import org.sonar.plugins.python.api.tree.BinaryExpression;
import org.sonar.plugins.python.api.tree.CompoundAssignmentStatement;
import org.sonar.plugins.python.api.tree.ForStatement;
import org.sonar.plugins.python.api.tree.Tree;
import org.sonar.plugins.python.api.tree.Statement;
import org.sonar.plugins.python.api.tree.SubscriptionExpression;
import org.sonar.plugins.python.api.tree.AssignmentStatement;

import java.util.List;

@Rule(key = "GCI107")

public class AvoidIterativeMatrixOperations extends PythonSubscriptionCheck {
    
    private static final String DESCRIPTION = "Avoid iterative matrix operations, use numpy dot or outer function instead";

    @Override
    public void initialize(Context context) {
        context.registerSyntaxNodeConsumer(Tree.Kind.FOR_STMT, this::visitForStatement);
    }

    private void visitForStatement(SubscriptionContext context) {
        ForStatement forStatement = (ForStatement) context.syntaxNode();
        if (isDotProduct(forStatement) || isOuterProduct(forStatement) || isMatrixDotProduct(forStatement)) {
            context.addIssue(forStatement.firstToken(), DESCRIPTION);
        }
    }


    private boolean isDotProduct(ForStatement forStatement) {
        List<Statement> statements = forStatement.body().statements();
        for (Statement stmt : statements) {
            if (stmt.is(Tree.Kind.COMPOUND_ASSIGNMENT)) {
                CompoundAssignmentStatement assign = (CompoundAssignmentStatement) stmt;
                Expression lhsExpression = assign.lhsExpression();
                if (assign.compoundAssignmentToken().value().equals("+=")
                    && isMultiplicationOfIndexedElements(assign.rhsExpression(),false)
                    && !isDoubleSubscription(lhsExpression)) {
                        System.out.println("Dot product found");
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isOuterProduct(ForStatement outerForStatement) {
        List<Statement> outerStatements = outerForStatement.body().statements();
        for (Statement outerStatement : outerStatements) {
            if (outerStatement.is(Tree.Kind.FOR_STMT)) {
                ForStatement innerForStatement = (ForStatement) outerStatement;
                List<Statement> innerStatements = innerForStatement.body().statements();
                for (Statement innermostStatement : innerStatements) {
                    if (isOuterProductOperation(innermostStatement)) {
                        System.out.println("Outer product found");
                        return true;
                    }
                }
            }
        }
        return false;

    }
    private boolean isOuterProductOperation(Statement statement) {
    if (statement.is(Tree.Kind.ASSIGNMENT_STMT)) {
        AssignmentStatement assignmentStmt = (AssignmentStatement) statement;
        Expression lhsExpression = assignmentStmt.lhsExpressions().get(0).expressions().get(0);

        if (isDoubleSubscription(lhsExpression)) {
            Expression rhsExpression = assignmentStmt.assignedValue();
            return containsMultiplicationOfIndexedElements(rhsExpression, false);
        }
    }
    return false;
}

private boolean containsMultiplicationOfIndexedElements(Expression expr, boolean matrixOps) {
    if (isMultiplicationOfIndexedElements(expr, matrixOps)) {
        return true;
    }

    if (expr instanceof BinaryExpression) {
        BinaryExpression bin = (BinaryExpression) expr;
        return containsMultiplicationOfIndexedElements(bin.leftOperand(), matrixOps)
            || containsMultiplicationOfIndexedElements(bin.rightOperand(), matrixOps);
    }

    return false;
}

        private boolean isMatrixDotProduct(ForStatement outerForStatement) {
            List<Statement> outerStatements = outerForStatement.body().statements();
            for (Statement outerStatement : outerStatements) {
                if (outerStatement.is(Tree.Kind.FOR_STMT)) {
                    ForStatement middleForStatement = (ForStatement) outerStatement;
                    List<Statement> middleStatements = middleForStatement.body().statements();
                    for (Statement middleStatement : middleStatements) {
                        if (middleStatement.is(Tree.Kind.FOR_STMT)) {
                            ForStatement innerForStatement = (ForStatement) middleStatement;
                            List<Statement> innerStatements = innerForStatement.body().statements();
                            for (Statement innermostStatement : innerStatements) {
                                if (isMatrixDotProductOperation(innermostStatement)) {
                                    System.out.println("Matrix dot product found");
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
            return false;
        }
    
        private boolean isMatrixDotProductOperation(Statement statement) {
            if (statement.is(Tree.Kind.COMPOUND_ASSIGNMENT)) {
                CompoundAssignmentStatement compoundStatement = (CompoundAssignmentStatement) statement;
                String operator = compoundStatement.compoundAssignmentToken().value();
                if (operator.equals("+=")) {
                    if (isDoubleSubscription(compoundStatement.lhsExpression())) {
                        Expression rhsExpression = compoundStatement.rhsExpression();
                        return isMultiplicationOfIndexedElements(rhsExpression, true);
                    }
                }
            }
            return false;
        }

    private boolean isMultiplicationOfIndexedElements(Expression expr, boolean matrixOps) {
        if (expr.is(Tree.Kind.MULTIPLICATION)) {
            BinaryExpression bin = (BinaryExpression) expr;
            if (matrixOps) {
                return isDoubleSubscription(bin.leftOperand()) && isDoubleSubscription(bin.rightOperand());
            } else {
                return bin.leftOperand().is(Tree.Kind.SUBSCRIPTION) && bin.rightOperand().is(Tree.Kind.SUBSCRIPTION);
            }
        }
        return false;
    }

    private boolean isDoubleSubscription(Expression expr) {
        return expr.is(Tree.Kind.SUBSCRIPTION) && ((SubscriptionExpression) expr).object().is(Tree.Kind.SUBSCRIPTION);
    }
}