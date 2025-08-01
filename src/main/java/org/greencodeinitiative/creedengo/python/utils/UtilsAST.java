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
package org.greencodeinitiative.creedengo.python.utils;

import org.sonar.plugins.python.api.SubscriptionContext;
import org.sonar.plugins.python.api.symbols.Symbol;
import org.sonar.plugins.python.api.tree.*;

import javax.annotation.CheckForNull;
import java.util.List;
import java.util.Optional;

public class UtilsAST {

    private UtilsAST() {
        // Utility class - prevent instantiation
    }

    public static List<Argument> getArgumentsFromCall(CallExpression callExpression) {
        return Optional.ofNullable(callExpression)
                .map(CallExpression::argumentList)
                .map(ArgList::arguments)
                .orElse(List.of());
    }

    public static String getMethodName(CallExpression callExpression) {
        return Optional.ofNullable(callExpression)
                .map(CallExpression::calleeSymbol)
                .map(Symbol::name)
                .orElse("");
    }

    public static String getQualifiedName(CallExpression callExpression) {
        return Optional.ofNullable(callExpression)
                .map(CallExpression::calleeSymbol)
                .map(Symbol::fullyQualifiedName)
                .orElse("");
    }

    public static String getVariableName(SubscriptionContext context) {

        if (context == null || context.syntaxNode() == null) {
            return null;
        }

        Tree current = context.syntaxNode();
        while (current != null && !current.is(Tree.Kind.ASSIGNMENT_STMT)) {
            current = current.parent();
        }
        if (current != null) {
            AssignmentStatement assignment = (AssignmentStatement) current;
            if (hasNonEmptyLhsExpressions(assignment)) {
                Expression leftExpr = assignment.lhsExpressions().get(0).expressions().get(0);
                if (leftExpr.is(Tree.Kind.NAME)) {
                    Name variableName = (Name) leftExpr;
                    return variableName.name();
                }
            }

        }
        return null;
    }

    @CheckForNull
    public static RegularArgument nthArgumentOrKeyword(int argPosition, String keyword, List<Argument> arguments) {
        return arguments.stream()
                .filter(argument -> hasKeyword(argument, keyword) ||
                        (argument.is(Tree.Kind.REGULAR_ARGUMENT)
                                && ((RegularArgument) argument).keywordArgument() == null
                                && arguments.indexOf(argument) == argPosition))
                .map(RegularArgument.class::cast)
                .findFirst()
                .orElse(null);
    }

    private static boolean hasKeyword(Argument argument, String keyword) {
        return argument instanceof RegularArgument regularArgument &&
                Optional.ofNullable(regularArgument.keywordArgument())
                        .map(Name::name)
                        .filter(name -> name.equals(keyword))
                        .isPresent();
    }

    private static boolean hasNonEmptyLhsExpressions(AssignmentStatement assignment ) {
        return Optional.ofNullable(assignment.lhsExpressions())
                .filter(lhs -> !lhs.isEmpty())
                .map(lhs -> lhs.get(0).expressions())
                .filter(exprLst -> !exprLst.isEmpty())
                .isPresent();
    }
}
