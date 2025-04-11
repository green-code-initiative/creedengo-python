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
import org.sonar.plugins.python.api.tree.StringElement;
import org.sonar.plugins.python.api.tree.StringLiteral;
import org.sonar.plugins.python.api.tree.Tree;
import org.sonarsource.analyzer.commons.annotations.DeprecatedRuleKey;

import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@Rule(key = "GCI74")
@DeprecatedRuleKey(repositoryKey = "ecocode-python", ruleKey = "EC74")
@DeprecatedRuleKey(repositoryKey = "gci-python", ruleKey = "S74")
public class AvoidFullSQLRequest extends PythonSubscriptionCheck {

//    private static final Logger LOGGER = Loggers.get(AvoidFullSQLRequest.class);

    protected static final String MESSAGE_RULE = "Don't use the query SELECT * FROM";

    private static final Pattern PATTERN = Pattern.compile("(?i).*select.*\\*.*from.*");
//    private static final Pattern PATTERN_UPPERCASE = Pattern.compile(".*SELECT.*\\*.*FROM.*");
    private static final Map<String, Collection<Integer>> linesWithIssuesByFile = new HashMap<>();


    @Override
    public void initialize(Context context) {
//        LOGGER.warn("--- DDC --- initialize - debut");
        context.registerSyntaxNodeConsumer(Tree.Kind.STRING_LITERAL, this::visitNodeString);
//        LOGGER.warn("--- DDC --- initialize - fin");
    }

    public void visitNodeString(SubscriptionContext ctx) {
//        LOGGER.warn("--- DDC --- visitNodeString - debut");
        StringLiteral stringLiteral = (StringLiteral) ctx.syntaxNode();
        stringLiteral.stringElements().forEach(stringElement -> checkIssue(stringElement, ctx));
//        LOGGER.warn("--- DDC --- visitNodeString - fin");
    }

    public void checkIssue(StringElement stringElement, SubscriptionContext ctx) {
//        LOGGER.warn("--- DDC --- checkIssue - debut");
        if (lineAlreadyHasThisIssue(stringElement, ctx)) return;

//        String upperCase = stringElement.value().toUpperCase();
//        boolean isPatternMatched = PATTERN_UPPERCASE.matcher(upperCase).matches();
//        LOGGER.warn("-- DDC -- stringElement.value() = " + stringElement.value());
//        LOGGER.warn("-- DDC -- upperCase = " + upperCase);
//        LOGGER.warn("-- DDC -- isPatternMatched = " + isPatternMatched);

//        if (isPatternMatched) {
        if (PATTERN.matcher(stringElement.value()).matches()) {
            report(stringElement, ctx);
        }

//        LOGGER.warn("--- DDC --- checkIssue - fin");
    }

    private void report(StringElement stringElement, SubscriptionContext ctx) {
        if (stringElement.firstToken() != null) {
            final String classname = ctx.pythonFile().fileName();
            final int line = stringElement.firstToken().line();
            linesWithIssuesByFile.computeIfAbsent(classname, k -> new ArrayList<>());
            linesWithIssuesByFile.get(classname).add(line);
        }
        ctx.addIssue(stringElement, MESSAGE_RULE);
    }

    private boolean lineAlreadyHasThisIssue(StringElement stringElement, SubscriptionContext ctx) {
        if (stringElement.firstToken() != null) {
            final String filename = ctx.pythonFile().fileName();
            final int line = stringElement.firstToken().line();

            return linesWithIssuesByFile.containsKey(filename)
                    && linesWithIssuesByFile.get(filename).contains(line);
        }

        return false;
    }
}
