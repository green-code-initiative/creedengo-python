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
import org.sonar.plugins.python.api.tree.QualifiedExpression;
import org.sonar.plugins.python.api.tree.RegularArgument;
import org.sonar.plugins.python.api.tree.NumericLiteral;

import java.util.List;

import static org.sonar.plugins.python.api.tree.Tree.Kind.*;

@Rule(key = "GCI302")
public class PreferAppendLeft extends PythonSubscriptionCheck {
    public static final String DESCRIPTION = "Use appendleft with deque instead of .insert(0, val) for modification at the beginning of a list";

    @Override
    public void initialize(Context context) {
        context.registerSyntaxNodeConsumer(CALL_EXPR, this::visitCallExpression);
    }

    private void visitCallExpression(SubscriptionContext context) {
        CallExpression callExpression = (CallExpression) context.syntaxNode();

        if (callExpression.callee().is(QUALIFIED_EXPR)) {
            QualifiedExpression qualifiedExpression = (QualifiedExpression) callExpression.callee();

            
            
            if (qualifiedExpression.name().name().equals("insert")) {
                List<org.sonar.plugins.python.api.tree.Argument> arguments = callExpression.arguments();
                
                if (arguments.size() >= 2) {// because it should be like insert(0,val) so there's two arguments
                    Expression firstArg;
                    firstArg = ((RegularArgument) arguments.get(0)).expression();
             
                    if (isZeroLiteral(firstArg)) {
                        context.addIssue(callExpression, DESCRIPTION);
                    }
                }
            }
        }
    }
    
    private boolean isZeroLiteral(Expression expression) {
        if (expression.is(NUMERIC_LITERAL)) {
            NumericLiteral numericLiteral = (NumericLiteral) expression;
            String value = numericLiteral.valueAsString();
            return "0".equals(value);
        }
        return false;
    }
}