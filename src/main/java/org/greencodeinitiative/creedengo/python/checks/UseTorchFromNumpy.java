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
import org.sonar.plugins.python.api.tree.*;

import java.util.HashSet;
import java.util.Set;

import static org.sonar.plugins.python.api.tree.Tree.Kind.*;

/**
 * Rule to enforce the use of torch.from_numpy() instead of torch.tensor() when working with NumPy arrays.
 * This optimization reduces memory usage and computational overhead by avoiding unnecessary data copying.
 */
@Rule(key = "GCI314")
public class UseTorchFromNumpy extends PythonSubscriptionCheck {

    public static final String DESCRIPTION = "Use torch.from_numpy() instead of torch.tensor() to create tensors from numpy arrays";
    private static final String NUMPY_ARRAY_FUNCTION = "numpy.array";
    private static final String TORCH_TENSOR_FUNCTION = "torch.tensor";

    private final Set<String> numpyArrayVariables = new HashSet<>();

    @Override
    public void initialize(Context context) {
        context.registerSyntaxNodeConsumer(ASSIGNMENT_STMT, this::visitAssignmentStatement);
        context.registerSyntaxNodeConsumer(CALL_EXPR, this::visitCallExpression);
    }

    private void visitAssignmentStatement(SubscriptionContext ctx) {
        var assignmentStmt = (AssignmentStatement) ctx.syntaxNode();
        var value = assignmentStmt.assignedValue();

        if (value.is(CALL_EXPR) && isNumpyArrayCreation((CallExpression) value)) {
            String variableName = Utils.getVariableName(ctx);
            if (variableName != null) {
                numpyArrayVariables.add(variableName);
            }
        }
    }

    private boolean isNumpyArrayCreation(CallExpression callExpression) {
        return NUMPY_ARRAY_FUNCTION.equals(Utils.getQualifiedName(callExpression));
    }

    private void visitCallExpression(SubscriptionContext ctx) {
        var callExpression = (CallExpression) ctx.syntaxNode();

        if (!TORCH_TENSOR_FUNCTION.equals(Utils.getQualifiedName(callExpression)) && !TORCH_TENSOR_FUNCTION.equals(callExpression.callee().firstToken().value()+"."+callExpression.calleeSymbol().name()))  {
            return;
        }

        for (Argument arg : callExpression.arguments()) {
            if (!arg.is(REGULAR_ARGUMENT)) {
                continue;
            }

            var regArg = (RegularArgument) arg;
            var argumentExpression = regArg.expression();

            // Case 1: Direct np.array call in the argument
            if (argumentExpression.is(CALL_EXPR)) {
                var argCallExpression = (CallExpression) argumentExpression;
                if (isNumpyArrayCreation(argCallExpression)) {
                    ctx.addIssue(argumentExpression, DESCRIPTION);
                    continue;
                }
            }

            // Case 2: Variable reference to a previously defined numpy array
            if (argumentExpression.is(NAME)) {
                var name = (Name) argumentExpression;
                var variableName = name.name();

                if (numpyArrayVariables.contains(variableName)) {
                    ctx.addIssue(argumentExpression, DESCRIPTION);
                }
            }
        }
    }
}
