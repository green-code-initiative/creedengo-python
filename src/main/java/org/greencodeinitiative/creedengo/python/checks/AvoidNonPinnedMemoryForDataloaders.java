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

import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.plugins.python.api.PythonSubscriptionCheck;
import org.sonar.plugins.python.api.tree.Tree;
import org.sonar.plugins.python.api.tree.CallExpression;
import org.sonar.plugins.python.api.tree.RegularArgument;
import org.sonar.plugins.python.api.tree.Expression;
import org.sonar.plugins.python.api.tree.Name;

import java.util.Optional;

import static org.sonar.plugins.python.api.tree.Tree.Kind.NAME;

@Rule(key = "GCI102")
public class AvoidNonPinnedMemoryForDataloaders extends PythonSubscriptionCheck {

  private static final String dataloaderFullyQualifiedName = "torch.utils.data.DataLoader";
  private static final int pinMemoryArgumentPosition = 7;
  private static final String pinMemoryArgumentName = "pin_memory";
  protected static final String MESSAGE = "Use pinned memory to reduce data transfer in RAM.";

  @Override
  public void initialize(Context context) {
    context.registerSyntaxNodeConsumer(Tree.Kind.CALL_EXPR, ctx -> {
      CallExpression callExpression = (CallExpression) ctx.syntaxNode();

      if (UtilsAST.getQualifiedName(callExpression).equals(dataloaderFullyQualifiedName)) {
        RegularArgument numWorkersArgument = UtilsAST.nthArgumentOrKeyword(pinMemoryArgumentPosition,
          pinMemoryArgumentName,
          callExpression.arguments());

        if (numWorkersArgument == null) {
          ctx.addIssue(callExpression, MESSAGE);
        } else {
          Optional.of(numWorkersArgument).filter(this::checkBadValuesForPinMemory)
            .ifPresent(arg -> ctx.addIssue(arg, MESSAGE));
        }
      }
    });
  }

  private boolean checkBadValuesForPinMemory(RegularArgument pinMemoryArgument) {
    Expression expression = pinMemoryArgument.expression();
    return expression.is(NAME) && ((Name) expression).name().equals("False");
  }
}