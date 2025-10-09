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
import org.sonar.plugins.python.api.tree.ExceptClause;
import org.sonar.plugins.python.api.tree.Expression;
import org.sonar.plugins.python.api.tree.Tree;
import org.sonar.plugins.python.api.tree.TryStatement;

import java.util.Arrays;
import java.util.List;

@Rule(key = "GCI109")
public class AvoidExceptionsForControlFlowCheck extends PythonSubscriptionCheck {

    public static final String DESCRIPTION = "Avoid using exceptions for control flow";
    
    private static final List<String> CONTROL_FLOW_EXCEPTIONS = Arrays.asList(
        "KeyError",
        "IndexError",
        "AttributeError",
        "StopIteration"
    );

    @Override
    public void initialize(Context context) {
        context.registerSyntaxNodeConsumer(Tree.Kind.TRY_STMT, this::visitTryStatement);
    }

    private void visitTryStatement(SubscriptionContext context) {
        TryStatement tryStatement = (TryStatement) context.syntaxNode();
        
        List<ExceptClause> exceptClauses = tryStatement.exceptClauses();
        if (exceptClauses.isEmpty()) {
            return;
        }
        
        for (ExceptClause exceptClause : exceptClauses) {
            Expression exception = exceptClause.exception();
            if (exception != null && isControlFlowException(exception)) {
                context.addIssue(exceptClause.exceptKeyword(), DESCRIPTION);
            }
        }
    }

    private boolean isControlFlowException(Expression exception) {
        String exceptionName = exception.firstToken().value();
        return CONTROL_FLOW_EXCEPTIONS.contains(exceptionName);
    }
}

