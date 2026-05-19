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
import org.sonar.plugins.python.api.tree.ImportFrom;
import org.sonar.plugins.python.api.tree.Name;
import org.sonar.plugins.python.api.tree.QualifiedExpression;
import org.sonar.plugins.python.api.tree.Tree;

@Rule(key = "GCI113")
public class GCI113PreferXGBoostOverRandomForest extends PythonSubscriptionCheck {

    private static final String DESCRIPTION = "Prefer XGBoost over RandomForest for better energy efficiency";
    private static final String RANDOM_FOREST_CLASSIFIER = "RandomForestClassifier";
    private static final String RANDOM_FOREST_REGRESSOR = "RandomForestRegressor";

    @Override
    public void initialize(Context context) {
        context.registerSyntaxNodeConsumer(Tree.Kind.IMPORT_FROM, this::checkImport);
        context.registerSyntaxNodeConsumer(Tree.Kind.CALL_EXPR, this::checkInstantiation);
    }

    private void checkImport(SubscriptionContext context) {
        ImportFrom importFrom = (ImportFrom) context.syntaxNode();
        if (importFrom.importedNames() == null) {
            return;
        }
        importFrom.importedNames().forEach(aliasedName -> {
            String importedName = aliasedName.dottedName().names().stream()
                    .map(Name::name)
                    .reduce((a, b) -> a + "." + b)
                    .orElse("");
            if (RANDOM_FOREST_CLASSIFIER.equals(importedName) || RANDOM_FOREST_REGRESSOR.equals(importedName)) {
                context.addIssue(aliasedName, DESCRIPTION);
            }
        });
    }

    private void checkInstantiation(SubscriptionContext context) {
        CallExpression callExpression = (CallExpression) context.syntaxNode();
        Expression callee = callExpression.callee();

        if (callee.is(Tree.Kind.NAME)) {
            Name name = (Name) callee;
            if (RANDOM_FOREST_CLASSIFIER.equals(name.name()) || RANDOM_FOREST_REGRESSOR.equals(name.name())) {
                context.addIssue(callExpression, DESCRIPTION);
            }
        } else if (callee.is(Tree.Kind.QUALIFIED_EXPR)) {
            QualifiedExpression qualifiedExpr = (QualifiedExpression) callee;
            String methodName = qualifiedExpr.name().name();
            if (RANDOM_FOREST_CLASSIFIER.equals(methodName) || RANDOM_FOREST_REGRESSOR.equals(methodName)) {
                context.addIssue(callExpression, DESCRIPTION);
            }
        }
    }
}
