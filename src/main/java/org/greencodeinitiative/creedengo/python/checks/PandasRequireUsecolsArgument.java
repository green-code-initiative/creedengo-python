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

import java.util.Arrays;
import java.util.List;
import org.sonar.check.Rule;
import org.sonar.plugins.python.api.PythonSubscriptionCheck;
import org.sonar.plugins.python.api.SubscriptionContext;
import org.sonar.plugins.python.api.tree.Argument;
import org.sonar.plugins.python.api.tree.CallExpression;
import org.sonar.plugins.python.api.tree.Tree;
import org.sonar.plugins.python.api.tree.Expression;
import org.sonar.plugins.python.api.tree.QualifiedExpression;
import org.sonar.plugins.python.api.tree.RegularArgument;
import static org.sonar.plugins.python.api.tree.Tree.Kind.*;

@Rule(key = "GCI96")
public class PandasRequireUsecolsArgument extends PythonSubscriptionCheck {

    public static final String DESCRIPTION = "Specify 'usecols' or 'columns' when reading a DataFrame using Pandas to load only necessary columns";
    private static final List<String> READ_METHODS = Arrays.asList(
            "read_csv", "read_parquet", "read_excel", "read_feather", "read_json"
    );
    
    @Override
    public void initialize(Context context) {
        context.registerSyntaxNodeConsumer(Tree.Kind.CALL_EXPR, this::visitCallExpression);
    }
    
    public void visitCallExpression(SubscriptionContext ctx) {
        CallExpression callExpression = (CallExpression) ctx.syntaxNode();
        Expression callee = callExpression.callee();
        
        if (callee.is(Tree.Kind.QUALIFIED_EXPR)) {
            QualifiedExpression qualifiedExpression = (QualifiedExpression) callee;
            String methodName = qualifiedExpression.name().name();
            
            if (READ_METHODS.contains(methodName)) {
                
                if (!hasColumnsSpecified(callExpression)) {
                    ctx.addIssue(callExpression.firstToken(), DESCRIPTION);
                }
            }
        }
    }
    
    private boolean hasColumnsSpecified(CallExpression callExpression) {
        List<Argument> arguments = callExpression.arguments();

        for (Argument arg : arguments) {
            if (arg.is(REGULAR_ARGUMENT)) {
                RegularArgument regularArg = (RegularArgument) arg;                
                String paramName = regularArg.keywordArgument() != null ? regularArg.keywordArgument().name() : null;
                if (paramName != null && (paramName.equals("usecols") || paramName.equals("columns"))) {
                    return true;
                }
            }
        }
        return false;
    }
}