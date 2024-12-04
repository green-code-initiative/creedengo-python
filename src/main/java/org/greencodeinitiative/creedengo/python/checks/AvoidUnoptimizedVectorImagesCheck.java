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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.sonar.check.Rule;
import org.sonar.plugins.python.api.PythonSubscriptionCheck;
import org.sonar.plugins.python.api.SubscriptionContext;
import org.sonar.plugins.python.api.tree.*;
import org.sonarsource.analyzer.commons.annotations.DeprecatedRuleKey;

@Rule(key = "GCI10")
@DeprecatedRuleKey(repositoryKey = "ecocode-python", ruleKey = "EC10")
public class AvoidUnoptimizedVectorImagesCheck extends PythonSubscriptionCheck {

    public static final String DESCRIPTION = "Avoid using unoptimized vector images";
    private static final Pattern LAYERS_PATTERN = Pattern.compile("</g>");

    @Override
    public void initialize(Context ctx) {
        ctx.registerSyntaxNodeConsumer(Tree.Kind.STRING_ELEMENT, this::checkSVG);
    }

    private void checkSVG(SubscriptionContext ctx) {
        StringElement stringLiteral = (StringElement) ctx.syntaxNode();
        checkComments(stringLiteral, ctx);
        checkLayers(stringLiteral, ctx);
        checkNamespaces(stringLiteral, ctx);
        checkMetadata(stringLiteral, ctx);
    }

    private void checkComments(StringElement str, SubscriptionContext ctx) {
        if (isSvgTagNotDetected(str))
            return;

        if (str.value().contains("<!--") || str.value().contains("-->")) {
            ctx.addIssue(str, DESCRIPTION);
        }
    }

    private void checkLayers(StringElement str, SubscriptionContext ctx) {
        if (isSvgTagNotDetected(str))
            return;

        Matcher matcher = LAYERS_PATTERN.matcher(str.value());
        int matches = 0;
        while (matcher.find()) matches++;
        if (matches > 1) { // if at least 2 finds, create an issue
            ctx.addIssue(str, DESCRIPTION);
        }
    }

    private void checkNamespaces(StringElement str, SubscriptionContext ctx) {
        if (isSvgTagNotDetected(str))
            return;

        if (str.value().contains("xmlns:") && !str.value().contains("xmlns:svg=")) {
            ctx.addIssue(str, DESCRIPTION);
        }
    }

    private void checkMetadata(StringElement str, SubscriptionContext ctx) {
        if (isSvgTagNotDetected(str))
            return;

        if (str.value().contains("</metadata>")) {
            ctx.addIssue(str, DESCRIPTION);
        }
    }

    private boolean isSvgTagNotDetected(StringElement str) {
        return !str.value().contains("</svg>");
    }
}
