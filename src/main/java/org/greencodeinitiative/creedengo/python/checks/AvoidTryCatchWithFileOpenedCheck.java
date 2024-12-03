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
import org.sonar.plugins.python.api.symbols.Symbol;
import org.sonar.plugins.python.api.tree.*;
import org.sonarsource.analyzer.commons.annotations.DeprecatedRuleKey;

import static org.sonar.plugins.python.api.tree.Tree.Kind.CALL_EXPR;

@Rule(key = "EC35")
@DeprecatedRuleKey(repositoryKey = "ecocode-python", ruleKey = "EC35")
@DeprecatedRuleKey(repositoryKey = "gci-python", ruleKey = "S34")
public class AvoidTryCatchWithFileOpenedCheck extends PythonSubscriptionCheck {

    public static final String DESCRIPTION = "Avoid the use of try-catch with a file open in try block";

    @Override
    public void initialize(Context context) {
        context.registerSyntaxNodeConsumer(Tree.Kind.TRY_STMT, this::visitNode);
    }

    private void visitNode(SubscriptionContext context) {
        TryStatement tryStatement = (TryStatement) context.syntaxNode();

        for (Statement stmt : tryStatement.body().statements()){
            if (stmt.is(CALL_EXPR)) {
                CallExpression callExpression = (CallExpression) stmt;
                visitCallExpression(context, callExpression);
            } else {
                checkCallExpressionInChildren(context, stmt);
            }
        }

    }

    private void checkCallExpressionInChildren(SubscriptionContext context, Tree stmt) {
        stmt.children().forEach(tree -> {
            if (tree.is(CALL_EXPR)) {
                CallExpression callExpression = (CallExpression) tree;
                visitCallExpression(context, callExpression);
            } else {
                checkCallExpressionInChildren(context, tree);
            }
        });
    }

    private void visitCallExpression(SubscriptionContext context, CallExpression callExpression){
        if ("open".equals(getFunctionNameFromCallExpression(callExpression))) {
            context.addIssue(callExpression.firstToken(), DESCRIPTION);
        }
    }

    private String getFunctionNameFromCallExpression(CallExpression callExpression) {
        Symbol symbol = callExpression.calleeSymbol();
        return symbol != null && symbol.name() != null ? symbol.name() : "";
    }

}
