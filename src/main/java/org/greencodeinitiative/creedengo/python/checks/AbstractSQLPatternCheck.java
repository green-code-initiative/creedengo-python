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

import org.sonar.plugins.python.api.PythonSubscriptionCheck;
import org.sonar.plugins.python.api.SubscriptionContext;
import org.sonar.plugins.python.api.tree.StringElement;
import org.sonar.plugins.python.api.tree.StringLiteral;
import org.sonar.plugins.python.api.tree.Tree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public abstract class AbstractSQLPatternCheck extends PythonSubscriptionCheck {

    private final Map<String, Collection<Integer>> linesWithIssuesByFile = new HashMap<>();

    protected abstract String getMessageRule();
    
    protected abstract Pattern getPattern();

    @Override
    public void initialize(Context context) {
        context.registerSyntaxNodeConsumer(Tree.Kind.STRING_LITERAL, this::visitNodeString);
    }

    public void visitNodeString(SubscriptionContext ctx) {
        StringLiteral stringLiteral = (StringLiteral) ctx.syntaxNode();
        stringLiteral.stringElements().forEach(stringElement -> checkIssue(stringElement, ctx));
    }

    public void checkIssue(StringElement stringElement, SubscriptionContext ctx) {
        if (lineAlreadyHasThisIssue(stringElement, ctx)) return;

        if (getPattern().matcher(stringElement.value()).matches()) {
            report(stringElement, ctx);
        }
    }

    private void report(StringElement stringElement, SubscriptionContext ctx) {
        if (stringElement.firstToken() != null) {
            final String classname = ctx.pythonFile().fileName();
            final int line = stringElement.firstToken().line();
            linesWithIssuesByFile.computeIfAbsent(classname, k -> new ArrayList<>()).add(line);
        }
        ctx.addIssue(stringElement, getMessageRule());
    }

    private boolean lineAlreadyHasThisIssue(StringElement stringElement, SubscriptionContext ctx) {
        final String classname = ctx.pythonFile().fileName();
        final int line = stringElement.firstToken() != null ? stringElement.firstToken().line() : -1;
        return linesWithIssuesByFile.getOrDefault(classname, new ArrayList<>()).contains(line);
    }
}
