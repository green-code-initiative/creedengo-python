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
import org.sonar.plugins.python.api.tree.Name;
import org.sonar.plugins.python.api.tree.CompoundAssignmentStatement;
import org.sonar.plugins.python.api.tree.Tree;
import org.sonar.plugins.python.api.tree.Expression;
import org.sonar.plugins.python.api.tree.AssignmentStatement;

import java.util.ArrayList;
import java.util.List;

@Rule(key = "GCI105")
public class StringConcatenation extends PythonSubscriptionCheck {

    private final List<String> stringVariables = new ArrayList<>();

    public static final String DESCRIPTION = "Concatenation of strings should be done using f-strings or str.join()";

    @Override
    public void initialize(Context context) {
        context.registerSyntaxNodeConsumer(Tree.Kind.ASSIGNMENT_STMT, this::trackVariableAssignments);
        context.registerSyntaxNodeConsumer(Tree.Kind.COMPOUND_ASSIGNMENT, this::checkAssignment);
    }

    private void checkAssignment(SubscriptionContext context) {
        CompoundAssignmentStatement compoundAssignment = (CompoundAssignmentStatement) context.syntaxNode();
        if ("+=".equals(compoundAssignment.compoundAssignmentToken().value())) {
            Expression lhsExpression = compoundAssignment.lhsExpression();
            if (lhsExpression.is(Tree.Kind.NAME)) {
                String variableName = ((Name) lhsExpression).name();
                if (stringVariables.contains(variableName)) {
                    context.addIssue(lhsExpression.firstToken(), DESCRIPTION);
                }
            }
        }
    }

    private void trackVariableAssignments(SubscriptionContext context) {
        AssignmentStatement assignment = (AssignmentStatement) context.syntaxNode();
        if (assignment.lhsExpressions().size() == 1) {
            Expression lhs = assignment.lhsExpressions().get(0).expressions().get(0);
            if (lhs.is(Tree.Kind.NAME)) {
                String variableName = ((Name) lhs).name();
                Expression assignedValue = assignment.assignedValue();
                if (isStringAssignment(assignedValue)) {
                    if (!stringVariables.contains(variableName)) {
                        stringVariables.add(variableName);
                    }
                } else {
                    stringVariables.remove(variableName);
                }
            }
        }
    }

    private boolean isStringAssignment(Expression assignedValue) {
        return assignedValue.is(Tree.Kind.STRING_ELEMENT) ||
                assignedValue.is(Tree.Kind.STRING_LITERAL) ||
                containsStringElement(assignedValue);
    }

    private boolean containsStringElement(Tree node) {
        if (node.is(Tree.Kind.STRING_ELEMENT) || node.is(Tree.Kind.STRING_LITERAL)) {
            return true;
        }
        for (Tree child : node.children()) {
            if (containsStringElement(child)) {
                return true;
            }
        }
        return false;
    }

}
