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
import org.sonar.plugins.python.api.tree.CallExpression;
import org.sonar.plugins.python.api.tree.Expression;
import org.sonar.plugins.python.api.tree.StringLiteral;
import org.sonar.plugins.python.api.tree.Tree;
import org.sonar.plugins.python.api.tree.QualifiedExpression;
import org.sonar.plugins.python.api.tree.Argument;
import org.sonar.plugins.python.api.tree.AssignmentStatement;
import org.sonar.plugins.python.api.tree.RegularArgument;

import java.util.ArrayList;
import java.util.List;

import static org.sonar.plugins.python.api.tree.Tree.Kind.*;
@Rule(key = "GCI314")
public class UseTorchFromNumpy extends PythonSubscriptionCheck {

    private final List<String> numpyArrayList = new ArrayList<>();

    public static final String DESCRIPTION = "Use torch.from_numpy() instead of torch.tensor() to create tensors from numpy arrays";

    @Override
    public void initialize(Context context) {
        context.registerSyntaxNodeConsumer(ASSIGNMENT_STMT, this::visitAssiStmt);
        context.registerSyntaxNodeConsumer(CALL_EXPR, this::visitCallExpression);
    }

    private void visitAssiStmt(SubscriptionContext ctx) {
        AssignmentStatement assignmentStmt = (AssignmentStatement) ctx.syntaxNode();
        Expression value = assignmentStmt.assignedValue();

        if (value.is(CALL_EXPR)) {
            if (checkNumpyCallExpression((CallExpression) value)) {
                String variableName = Utils.getVariableName(ctx);
                System.out.println(("added one variable: " + variableName));
                if (variableName != null) {
                    numpyArrayList.add(variableName);
                }
            }
        }
    }

    private boolean checkNumpyCallExpression(CallExpression callExpression) {
        Expression callee = callExpression.callee();

        if (callee.is(QUALIFIED_EXPR)) {
            return Utils.getQualifiedName(callExpression).equals("numpy.array");
        }
        return false;
    }

    private void visitCallExpression(SubscriptionContext ctx) {
        CallExpression callExpression = (CallExpression) ctx.syntaxNode();
        // System.out.println("visiting call expression at line"+ callExpression.firstToken().line());
        // System.out.println("Utils"+ Utils.getQualifiedName(callExpression));
        // System.out.println("callExpression"+ callExpression.);
        // System.out.println((callExpression));

        if (Utils.getQualifiedName(callExpression).equals("torch.tensor")) {
            System.out.println(("detected torch.tensor() call at line: " + callExpression.firstToken().line()));
            for (Argument arg : callExpression.arguments()) {
                if (arg.is(REGULAR_ARGUMENT)) {
                    RegularArgument regArg = (RegularArgument) arg;
                    String varName = regArg.expression().toString();

                    if (numpyArrayList.contains(varName)) {
                        ctx.addIssue(callExpression, DESCRIPTION);

                    }
                }
            }
        }
    }
    
}
