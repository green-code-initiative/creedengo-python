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
import org.sonar.plugins.python.api.tree.ComprehensionFor;
import org.sonar.plugins.python.api.tree.Expression;
import org.sonar.plugins.python.api.tree.ForStatement;
import org.sonar.plugins.python.api.tree.Name;
import org.sonar.plugins.python.api.tree.RegularArgument;
import org.sonar.plugins.python.api.tree.Tree;

import java.util.List;

@Rule(key = "GCI116")
public class GCI116PreferEnumerateOverRangeLen extends PythonSubscriptionCheck {

    public static final String DESCRIPTION = "Avoid range(len()) pattern, prefer direct iteration or enumerate() which avoids costly index-based access";

    @Override
    public void initialize(Context context) {
        context.registerSyntaxNodeConsumer(Tree.Kind.FOR_STMT, this::visitForStatement);
        context.registerSyntaxNodeConsumer(Tree.Kind.COMP_FOR, this::visitComprehensionFor);
    }

    private void visitForStatement(SubscriptionContext context) {
        ForStatement forStatement = (ForStatement) context.syntaxNode();

        List<Expression> testExpressions = forStatement.testExpressions();
        if (testExpressions.size() != 1) {
            return;
        }

        Expression iterable = testExpressions.get(0);
        if (isRangeLenCall(iterable)) {
            context.addIssue(iterable, DESCRIPTION);
        }
    }

    private void visitComprehensionFor(SubscriptionContext context) {
        ComprehensionFor compFor = (ComprehensionFor) context.syntaxNode();
        Expression iterable = compFor.iterable();
        if (isRangeLenCall(iterable)) {
            context.addIssue(iterable, DESCRIPTION);
        }
    }

    private boolean isRangeLenCall(Expression expression) {
        if (!expression.is(Tree.Kind.CALL_EXPR)) {
            return false;
        }

        CallExpression callExpression = (CallExpression) expression;
        Expression callee = callExpression.callee();

        if (!callee.is(Tree.Kind.NAME)) {
            return false;
        }

        if (!"range".equals(((Name) callee).name())) {
            return false;
        }

        List<org.sonar.plugins.python.api.tree.Argument> arguments = callExpression.arguments();
        if (arguments.size() != 1) {
            return false;
        }

        org.sonar.plugins.python.api.tree.Argument firstArg = arguments.get(0);
        if (!(firstArg instanceof RegularArgument)) {
            return false;
        }

        return isLenCall(((RegularArgument) firstArg).expression());
    }

    private boolean isLenCall(Expression expression) {
        if (!expression.is(Tree.Kind.CALL_EXPR)) {
            return false;
        }

        CallExpression callExpression = (CallExpression) expression;
        Expression callee = callExpression.callee();

        if (!callee.is(Tree.Kind.NAME)) {
            return false;
        }

        return "len".equals(((Name) callee).name());
    }
}
