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

import java.util.HashMap;
import java.util.Map;

import org.sonar.check.Rule;
import org.sonar.plugins.python.api.PythonSubscriptionCheck;
import org.sonar.plugins.python.api.SubscriptionContext;
import org.sonar.plugins.python.api.tree.CallExpression;
import org.sonar.plugins.python.api.tree.Expression;
import org.sonar.plugins.python.api.tree.ForStatement;
import org.sonar.plugins.python.api.tree.Name;
import org.sonar.plugins.python.api.tree.QualifiedExpression;
import org.sonar.plugins.python.api.tree.Tree;


@Rule(key ="GCI103")
public class DictionaryItemsUnused extends PythonSubscriptionCheck {

    public static final String DESCRIPTION = "Use dict.keys() or dict.values() instead of dict.items() when only one part of the key-value pair is used";

    private final Map<ForStatement, ItemsLoopInfo> itemsLoops = new HashMap<>();

    @Override
    public void initialize(Context context) {
        context.registerSyntaxNodeConsumer(Tree.Kind.FOR_STMT, this::checkForLoop);
        context.registerSyntaxNodeConsumer(Tree.Kind.FOR_STMT, this::finalizeCheck);
    }

    private void checkForLoop(SubscriptionContext context) {
        ForStatement forStmt = (ForStatement) context.syntaxNode();

        if (forStmt.expressions().size() == 2) {
            Expression keyExpr = forStmt.expressions().get(0);
            Expression valueExpr = forStmt.expressions().get(1);
            Expression iterable = forStmt.testExpressions().get(0);

            if (isItemsCall(iterable)) {
                String key = ((Name) keyExpr).name();
                String value = ((Name) valueExpr).name();

                ItemsLoopInfo info = new ItemsLoopInfo(key, value);
                itemsLoops.put(forStmt, info);

                trackNameUsages(forStmt.body(), info);
            }
        } 
    }

    private boolean isItemsCall(Expression expr) {
        if (expr.is(Tree.Kind.CALL_EXPR)) {
            CallExpression callExpr = (CallExpression) expr;
            if (callExpr.callee().is(Tree.Kind.QUALIFIED_EXPR)) {
                QualifiedExpression qualExpr = (QualifiedExpression) callExpr.callee();
                boolean isItems = "items".equals(qualExpr.name().name());
                return isItems;
            }
        }
        return false;
    }

    private void trackNameUsages(Tree node, ItemsLoopInfo info) {
        if (node instanceof Name) {
            String name = ((Name) node).name();
            info.markUsage(name);
        }

        for (Tree child : node.children()) {
            trackNameUsages(child, info);
        }
    }

    private void finalizeCheck(SubscriptionContext context) {
        ForStatement forStmt = (ForStatement) context.syntaxNode();
        ItemsLoopInfo info = itemsLoops.get(forStmt);

        if (info != null) {

            if (info.isOnlyOneUsed()) {
                context.addIssue(forStmt.firstToken(), DESCRIPTION);
            } 

            itemsLoops.remove(forStmt); 
        }
    }

    private static class ItemsLoopInfo {
        final String keyVar;
        final String valueVar;
        boolean keyUsed = false;
        boolean valueUsed = false;

        ItemsLoopInfo(String keyVar, String valueVar) {
            this.keyVar = keyVar;
            this.valueVar = valueVar;
        }

        void markUsage(String var) {
            if (var.equals(keyVar)) {
                keyUsed = true;
            }
            if (var.equals(valueVar)) {
                valueUsed = true;
            }
        }

        boolean isOnlyOneUsed() {
            return (keyUsed && !valueUsed) || (!keyUsed && valueUsed);
        }
    }
}