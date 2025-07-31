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
import org.sonar.plugins.python.api.symbols.Symbol;
import org.sonar.plugins.python.api.tree.Argument;
import org.sonar.plugins.python.api.tree.AssignmentStatement;
import org.sonar.plugins.python.api.tree.Tree;
import org.sonar.plugins.python.api.tree.RegularArgument;
import org.sonar.plugins.python.api.tree.Name;
import org.sonar.plugins.python.api.tree.CallExpression;
import org.sonar.plugins.python.api.tree.Expression;

import javax.annotation.CheckForNull;
import java.util.List;
import java.util.Objects;

public class Utils {

  private static boolean hasKeyword(Argument argument, String keyword) {
    if (!argument.is(new Tree.Kind[] {Tree.Kind.REGULAR_ARGUMENT})) {
      return false;
    } else {
      Name keywordArgument = ((RegularArgument) argument).keywordArgument();
      return keywordArgument != null && keywordArgument.name().equals(keyword);
    }
  }

  @CheckForNull
  public static RegularArgument nthArgumentOrKeyword(int argPosition, String keyword, List<Argument> arguments) {
    for (int i = 0; i < arguments.size(); ++i) {
      Argument argument = (Argument) arguments.get(i);
      if (hasKeyword(argument, keyword)) {
        return (RegularArgument) argument;
      }

      if (argument.is(new Tree.Kind[] {Tree.Kind.REGULAR_ARGUMENT})) {
        RegularArgument regularArgument = (RegularArgument) argument;
        if (regularArgument.keywordArgument() == null && argPosition == i) {
          return regularArgument;
        }
      }
    }

    return null;
  }

  public static String getQualifiedName(CallExpression callExpression) {
    Symbol symbol = callExpression.calleeSymbol();

    return symbol != null && symbol.fullyQualifiedName() != null ? symbol.fullyQualifiedName() : "";
  }

  public static String getMethodName(CallExpression callExpression) {
    Symbol symbol = callExpression.calleeSymbol();
    return symbol != null && symbol.name() != null ? symbol.name() : "";
  }

  public static List<Argument> getArgumentsFromCall(CallExpression callExpression) {
    try {
      return Objects.requireNonNull(callExpression.argumentList()).arguments();
    } catch (NullPointerException e) {
      return List.of();
    }
  }

  public static String getVariableName(SubscriptionContext context) {
    Tree node = context.syntaxNode();
    Tree current = node;
    while (current != null && !current.is(Tree.Kind.ASSIGNMENT_STMT)) {
        current = current.parent();
    }
    if (current != null && current.is(Tree.Kind.ASSIGNMENT_STMT)) {
      AssignmentStatement assignment = (AssignmentStatement) current;
      if (!assignment.lhsExpressions().isEmpty() && !assignment.lhsExpressions().get(0).expressions().isEmpty()) {
              Expression leftExpr = assignment.lhsExpressions().get(0).expressions().get(0);
              if (leftExpr.is(Tree.Kind.NAME)) {
                  Name variableName = (Name) leftExpr;
                  return variableName.name();
              }
          }
      
    }
    return null;
  }
}
