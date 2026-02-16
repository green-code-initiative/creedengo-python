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
import org.sonar.plugins.python.api.tree.AliasedName;
import org.sonar.plugins.python.api.tree.BaseTreeVisitor;
import org.sonar.plugins.python.api.tree.CallExpression;
import org.sonar.plugins.python.api.tree.FileInput;
import org.sonar.plugins.python.api.tree.ImportFrom;
import org.sonar.plugins.python.api.tree.ImportName;
import org.sonar.plugins.python.api.tree.Name;
import org.sonar.plugins.python.api.tree.QualifiedExpression;
import org.sonar.plugins.python.api.tree.Tree;
import org.sonarsource.analyzer.commons.annotations.DeprecatedRuleKey;

@Rule(key = "GCI72")
@DeprecatedRuleKey(repositoryKey = "ecocode-python", ruleKey = "EC72")
@DeprecatedRuleKey(repositoryKey = "gci-python", ruleKey = "S72")
public class AvoidSQLRequestInLoop extends PythonSubscriptionCheck {

    // TODO: Handle ORM lib
    private static final List<String> SQL_LIBS = Arrays.asList("cx_Oracle", "mysql.connector", "psycopg2", "pymssql", "pyodbc", "sqlite3");

    protected static final String MESSAGE_RULE = "Avoid performing SQL queries within a loop";

    private boolean isUsingSqlLib = false;

    @Override
    public void initialize(Context context) {
        context.registerSyntaxNodeConsumer(Tree.Kind.FILE_INPUT, this::visitFile);
        context.registerSyntaxNodeConsumer(Tree.Kind.CALL_EXPR, this::checkCallExpression);
    }

    private void visitFile(SubscriptionContext ctx) {
        FileInput tree = (FileInput) ctx.syntaxNode();
        SqlLibraryImportVisitor visitor = new SqlLibraryImportVisitor();
        tree.accept(visitor);
        isUsingSqlLib = visitor.isUsingSqlLib;
    }

    private static class SqlLibraryImportVisitor extends BaseTreeVisitor {
        private boolean isUsingSqlLib = false;

        @Override
        public void visitImportName(ImportName importName) {
            for (AliasedName aliasedName : importName.modules()) {
                String fullModuleName = getFullModuleName(aliasedName.dottedName().names());
                if (SQL_LIBS.contains(fullModuleName)) {
                    isUsingSqlLib = true;
                }
            }
            super.visitImportName(importName);
        }

        @Override
        public void visitImportFrom(ImportFrom importFrom) {
            if (importFrom.module() != null) {
                String fullModuleName = getFullModuleName(importFrom.module().names());
                if (SQL_LIBS.contains(fullModuleName)) {
                    isUsingSqlLib = true;
                }
            }
            super.visitImportFrom(importFrom);
        }

        private String getFullModuleName(List<Name> names) {
            if (names == null || names.isEmpty()) {
                return "";
            }
            return names.stream()
                    .map(Name::name)
                    .reduce((a, b) -> a + "." + b)
                    .orElse("");
        }
    }

    private void checkCallExpression(SubscriptionContext context) {
        CallExpression expression = (CallExpression) context.syntaxNode();

        if (expression.callee().is(Tree.Kind.QUALIFIED_EXPR)) {
            String name = ((QualifiedExpression) expression.callee()).name().name();
            if (isUsingSqlLib && "execute".equals(name) && hasLoopParent(expression)) {
                context.addIssue(expression, AvoidSQLRequestInLoop.MESSAGE_RULE);
            }
        }
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
