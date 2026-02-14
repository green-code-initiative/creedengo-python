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
import org.sonar.plugins.python.api.ProjectPythonVersion;
import org.sonar.plugins.python.api.PythonSubscriptionCheck;
import org.sonar.plugins.python.api.SubscriptionContext;
import org.sonar.plugins.python.api.tree.*;

@Rule(key = "GCI112")
public class UsingSlotsOnDataClasses extends PythonSubscriptionCheck {

    private static final String DECORATOR_DATA_CLASS = "dataclass";
    private static final String SLOTS_ARG = "slots";

    public static final String DESCRIPTION = "From python >= 3.10, reduce memory footprint by using @dataclass(slots=True)";

    @Override
    public void initialize(Context context) {
        if(ProjectPythonVersion.currentVersions().stream().anyMatch(version -> version.compare(3, 10) >= 0)) {
            context.registerSyntaxNodeConsumer(Tree.Kind.DECORATOR, this::isUsingSlots);
        }
    }

    private void isUsingSlots(SubscriptionContext ctx) {
        Decorator decorator = ((Decorator) ctx.syntaxNode());

        if (!isDecoratorDataClass(decorator)) {
            return;
        }

        if (decorator.arguments() != null
                && decorator.arguments().arguments() != null
                && decorator.arguments().arguments().stream()
                .anyMatch(this::isSlotsTrue)) {
            return;
        }

        ctx.addIssue(decorator, DESCRIPTION);
    }

    private boolean isSlotsTrue(Argument argument) {
        // Check if argument is "slots=True" or positional "slots"
        if (argument.is(Tree.Kind.REGULAR_ARGUMENT)) {
            RegularArgument regArg = (RegularArgument) argument;
        if (regArg.keywordArgument() != null
                && SLOTS_ARG.equals(regArg.keywordArgument().name())
                && regArg.expression() != null
                && regArg.expression().is(Tree.Kind.NAME)) {
                    Name value = (Name) regArg.expression();
                    return "True".equals(value.name());
            }
        }
        return false;
    }

    private boolean isDecoratorDataClass(Decorator decorator) {
        Name name = null;
        // Manage decorator detected as simple expression
        if (decorator.expression().is(Tree.Kind.NAME)) {
            name = (Name) decorator.expression();
            // manage decorator detected as callable expression
        } else if(decorator.expression().is(Tree.Kind.CALL_EXPR)) {
            CallExpression callExpression = (CallExpression) decorator.expression();
            if (callExpression.callee().is(Tree.Kind.NAME)) {
                name = (Name) callExpression.callee();
            }
        }
        return name != null && UsingSlotsOnDataClasses.DECORATOR_DATA_CLASS.equals(name.name());
    }
}
