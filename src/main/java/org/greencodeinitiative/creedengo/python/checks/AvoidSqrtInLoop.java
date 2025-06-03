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
import org.sonar.plugins.python.api.tree.Tree;

@Rule(key = "GCI106")
public class AvoidSqrtInLoop extends PythonSubscriptionCheck {

    public static final String DESCRIPTION = "Avoid using scalar sqrt functions in loops. Apply vectorized sqrt operations on arrays directly.";

    @Override
    public void initialize(Context context) {
        context.registerSyntaxNodeConsumer(Tree.Kind.CALL_EXPR, this::checkCallExpression);
    }

    private void checkCallExpression(SubscriptionContext context) {
        CallExpression callExpression = (CallExpression) context.syntaxNode();
        
        if (isSqrtCall(callExpression) && hasLoopParent(callExpression)) {
            context.addIssue(callExpression, DESCRIPTION);
        }
    }
    
    private boolean isSqrtCall(CallExpression callExpression) {
        Expression callee = callExpression.callee();
        
        // Check for direct calls to math.sqrt
        if (callee.is(Tree.Kind.QUALIFIED_EXPR)) {
            QualifiedExpression qualifiedExpression = (QualifiedExpression) callee;
            String methodName = qualifiedExpression.name().name();
            
            if ("sqrt".equals(methodName)) {
                Expression qualifier = qualifiedExpression.qualifier();
                if (qualifier != null && qualifier.is(Tree.Kind.NAME)) {
                    String qualifierName = qualifier.firstToken().value();
                    return "math".equals(qualifierName) || "np".equals(qualifierName) || "numpy".equals(qualifierName);
                }
            }
        }
        
        return false;
    }
    
    private boolean hasLoopParent(Tree tree) {
        for (Tree parent = tree.parent(); parent != null; parent = parent.parent()) {
            Tree.Kind kind = parent.getKind();
            if (kind == Tree.Kind.FOR_STMT || kind == Tree.Kind.WHILE_STMT) {
                return true;
            }
        }
        return false;
    }
}