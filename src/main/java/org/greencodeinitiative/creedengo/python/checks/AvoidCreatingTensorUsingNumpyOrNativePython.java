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

import java.util.List;
import java.util.Map;

import static java.util.Map.entry;
import static org.sonar.plugins.python.api.tree.Tree.Kind.CALL_EXPR;

@Rule(key = "GCI104")
public class AvoidCreatingTensorUsingNumpyOrNativePython extends PythonSubscriptionCheck {

  public static final String RULE_KEY = "P5";
  private static final String dataArgumentName = "data";
  private static final int dataArgumentPosition = 0;
  private static final Map<String, String> torchOtherFunctionsMapping = Map.ofEntries(
    entry("numpy.random.rand", "torch.rand"),
    entry("numpy.random.randint", "torch.randint"),
    entry("numpy.random.randn", "torch.randn"),
    entry("numpy.zeros", "torch.zeros"),
    entry("numpy.zeros_like", "torch.zeros_like"),
    entry("numpy.ones", "torch.ones"),
    entry("numpy.ones_like", "torch.ones_like"),
    entry("numpy.full", "torch.full"),
    entry("numpy.full_like", "torch.full_like"),
    entry("numpy.eye", "torch.eye"),
    entry("numpy.arange", "torch.arange"),
    entry("numpy.linspace", "torch.linspace"),
    entry("numpy.logspace", "torch.logspace"),
    entry("numpy.identity", "torch.eye"),
    entry("numpy.tile", "torch.tile")
  );
  private static final List<String> torchTensorConstructors = List.of(
    "torch.tensor", "torch.FloatTensor",
    "torch.DoubleTensor", "torch.HalfTensor",
    "torch.BFloat16Tensor", "torch.ByteTensor",
    "torch.CharTensor", "torch.ShortTensor",
    "torch.IntTensor", "torch.LongTensor",
    "torch.BoolTensor", "torch.cuda.FloatTensor",
    "torch.cuda.DoubleTensor", "torch.cuda.HalfTensor",
    "torch.cuda.BFloat16Tensor", "torch.cuda.ByteTensor",
    "torch.cuda.CharTensor", "torch.cuda.ShortTensor",
    "torch.cuda.IntTensor", "torch.cuda.LongTensor",
    "torch.cuda.BoolTensor");
  protected static final String MESSAGE = "Directly create tensors as torch.Tensor. Use %s instead of %s.";

  @Override
  public void initialize(Context context) {
    context.registerSyntaxNodeConsumer(Tree.Kind.CALL_EXPR, ctx -> {
      CallExpression callExpression = (CallExpression) ctx.syntaxNode();
      if (torchTensorConstructors.contains(Utils.getQualifiedName(callExpression))) {
        RegularArgument tensorCreatorArgument = Utils.nthArgumentOrKeyword(dataArgumentPosition, dataArgumentName, callExpression.arguments());
        if (tensorCreatorArgument != null) {
          if (tensorCreatorArgument.expression().is(CALL_EXPR)) {
            String functionQualifiedName = Utils.getQualifiedName((CallExpression) tensorCreatorArgument.expression());
            if (torchOtherFunctionsMapping.containsKey(functionQualifiedName)) {
              ctx.addIssue(callExpression, String.format(MESSAGE, torchOtherFunctionsMapping.get(functionQualifiedName), functionQualifiedName));
            }
          }
        }
      }
    });
  }
}