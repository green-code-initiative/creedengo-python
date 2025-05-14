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

import org.sonar.plugins.python.api.SubscriptionContext;
import org.sonar.plugins.python.api.tree.CallExpression;
import org.sonar.plugins.python.api.tree.QualifiedExpression;
import org.sonar.plugins.python.api.tree.StringLiteral;
import org.sonar.plugins.python.api.tree.Tree;
import org.sonar.plugins.python.api.tree.Expression;
import org.sonar.check.Rule;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.sonar.plugins.python.api.PythonSubscriptionCheck;
@Rule(key = "GCI95")

public class AvoidCSVFormat extends PythonSubscriptionCheck {

    public static final String DESCRIPTION = "Use Parquet or Feather format instead of CSV";
    protected static final Pattern CSV_EXTENSION = Pattern.compile("\\.(csv)");
    
    private final Set<Integer> reportedLines = new HashSet<>();

    @Override
    public void initialize(Context context) {
        context.registerSyntaxNodeConsumer(Tree.Kind.CALL_EXPR, this::visitCallExpression);
        context.registerSyntaxNodeConsumer(Tree.Kind.STRING_LITERAL, this::visitNodeString);
    }

    public void visitCallExpression(SubscriptionContext ctx) {
        CallExpression callExpression = (CallExpression) ctx.syntaxNode();

        Expression callee = callExpression.callee();

        

        if (callee.is(Tree.Kind.QUALIFIED_EXPR)) {
            QualifiedExpression qualifiedExpression = (QualifiedExpression) callee;
            String methodName = qualifiedExpression.name().name();
            
            if (methodName.equals("read_csv") || methodName.equals("to_csv")) {
                int line = callExpression.firstToken().line();
                

                if (!reportedLines.contains(line)) {
                    reportedLines.add(line);
                    ctx.addIssue(callExpression.firstToken(), DESCRIPTION);
                }
            }
        }
    }
    
    public void visitNodeString(SubscriptionContext ctx) {
        StringLiteral stringLiteral = (StringLiteral) ctx.syntaxNode();
        int line = stringLiteral.firstToken().line();
        

        if (reportedLines.contains(line)) {
            return;
        }
        
        String strValue = stringLiteral.trimmedQuotesValue();
        Matcher matcher = CSV_EXTENSION.matcher(strValue);
        if (matcher.find()) {
            reportedLines.add(line);
            ctx.addIssue(stringLiteral, DESCRIPTION);
        }
    }
}