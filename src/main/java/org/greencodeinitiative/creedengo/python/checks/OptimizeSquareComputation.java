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
import org.sonar.plugins.python.api.tree.BinaryExpression;
import org.sonar.plugins.python.api.tree.CallExpression;
import org.sonar.plugins.python.api.tree.Expression;
import org.sonar.plugins.python.api.tree.NumericLiteral;
import org.sonar.plugins.python.api.tree.QualifiedExpression;
import org.sonar.plugins.python.api.tree.RegularArgument;

import static org.sonar.plugins.python.api.tree.Tree.Kind.*;

@Rule(key = "GCI97")
public class OptimizeSquareComputation extends PythonSubscriptionCheck {

    public static final String DESCRIPTION = "Use x*x instead of x**2 or math.pow(x,2) to calculate the square of a value";

    @Override
    public void initialize(Context context) {
        context.registerSyntaxNodeConsumer(CALL_EXPR, this::checkMathPowCall);
        context.registerSyntaxNodeConsumer(POWER, this::checkPowerOf2);  
    }

   
    private boolean isNumericLiteralWithValue(Expression expr, String value) {
        if (expr.is(NUMERIC_LITERAL)) {
            NumericLiteral numericLiteral = (NumericLiteral) expr;
            return value.equals(numericLiteral.valueAsString());
        }
        return false;
    }

    private void checkMathPowCall(SubscriptionContext context) {
        CallExpression callExpression = (CallExpression) context.syntaxNode();
        
        
        if (isMathPowCall(callExpression)) {
            context.addIssue(callExpression, DESCRIPTION);
        }
    }
    
    private boolean isMathPowCall(CallExpression callExpression) {
        Expression callee = callExpression.callee();
        
        
        if (callee.is(QUALIFIED_EXPR)) {
            QualifiedExpression qualifiedExpr = (QualifiedExpression) callee;
            String name = qualifiedExpr.name().name();
            
            
            if ("pow".equals(name)) {
                
                if (callExpression.arguments().size() >= 2) {
                    Expression secondArg = ((RegularArgument)callExpression.arguments().get(1)).expression();
                    return isNumericLiteralWithValue(secondArg, "2");
                }
            }
        }
        
        return false;
    }
    
    
    private void checkPowerOf2(SubscriptionContext context) {
        BinaryExpression power = (BinaryExpression) context.syntaxNode();
        
        
        if (isNumericLiteralWithValue(power.rightOperand(), "2")) {
            context.addIssue(power, DESCRIPTION);
        }
    }
}