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
import org.sonar.plugins.python.api.tree.*;

import java.util.*;

@Rule(key = "GCI111")
public class DetectBadLoggingFormatInterpolation extends PythonSubscriptionCheck {

    protected static final String MESSAGE_RULE = "For logging format, prefer using %s with kwargs instead of builtin formatter \"\".format() or f\"\"";

    // All logging methods that accept format strings
    private static final Set<String> LOGGING_METHODS = new HashSet<>(Arrays.asList(
        "debug", "info", "warning", "warn", "error", "critical", "fatal", "exception", "log",
        "trace", "success"  // loguru specific methods
    ));

    // Logging module/library names
    private static final Set<String> LOGGING_MODULE_NAMES = new HashSet<>(Arrays.asList(
        "logging", "loguru"
    ));

    private boolean isUsingLoggingLib = false;
    private final Set<String> loggerVariableNames = new HashSet<>();

    @Override
    public void initialize(Context context) {
        context.registerSyntaxNodeConsumer(Tree.Kind.FILE_INPUT, this::visitFile);
        context.registerSyntaxNodeConsumer(Tree.Kind.CALL_EXPR, this::checkCallExpression);
    }

    /**
     * Scan file to detect logging imports and logger variable assignments
     */
    private void visitFile(SubscriptionContext ctx) {
        // Reset state for each file
        isUsingLoggingLib = false;
        loggerVariableNames.clear();

        FileInput fileInput = (FileInput) ctx.syntaxNode();

        // Check imports
        LoggingImportVisitor importVisitor = new LoggingImportVisitor();
        fileInput.accept(importVisitor);
        isUsingLoggingLib = importVisitor.isLoggingImported;

        // Add directly imported logger variables (e.g., from loguru import logger)
        loggerVariableNames.addAll(importVisitor.importedLoggerNames);

        // Check logger assignments
        if (isUsingLoggingLib) {
            LoggerAssignmentVisitor assignmentVisitor = new LoggerAssignmentVisitor();
            fileInput.accept(assignmentVisitor);
            loggerVariableNames.addAll(assignmentVisitor.loggerVariables);
        }
    }

    /**
     * Visitor to detect logging imports
     */
    private static class LoggingImportVisitor extends BaseTreeVisitor {
        private boolean isLoggingImported = false;
        private final Set<String> importedLoggerNames = new HashSet<>();

        @Override
        public void visitImportFrom(ImportFrom importFrom) {
            List<Name> names = importFrom.module() != null ? importFrom.module().names() : null;
            if (names != null && !names.isEmpty() && LOGGING_MODULE_NAMES.contains(names.get(0).name())) {
                isLoggingImported = true;

                // Detect directly imported logger names (e.g., from loguru import logger)
                for (AliasedName importedItem : importFrom.importedNames()) {
                    if (importedItem.alias() != null) {
                        // from loguru import logger as log -> "log"
                        importedLoggerNames.add(importedItem.alias().name());
                    } else {
                        // from loguru import logger -> "logger"
                        List<Name> itemNames = importedItem.dottedName().names();
                        if (!itemNames.isEmpty()) {
                            importedLoggerNames.add(itemNames.get(itemNames.size() - 1).name());
                        }
                    }
                }
            }
            super.visitImportFrom(importFrom);
        }

        @Override
        public void visitImportName(ImportName importName) {
            for (AliasedName aliasedName : importName.modules()) {
                List<Name> names = aliasedName.dottedName().names();
                if (!names.isEmpty() && LOGGING_MODULE_NAMES.contains(names.get(0).name())) {
                    isLoggingImported = true;
                }
            }
            super.visitImportName(importName);
        }
    }

    /**
     * Visitor to detect logger variable assignments (e.g., logger = logging.getLogger())
     */
    private static class LoggerAssignmentVisitor extends BaseTreeVisitor {
        private final Set<String> loggerVariables = new HashSet<>();

        @Override
        public void visitAssignmentStatement(AssignmentStatement assignment) {
            // Check if right side is a logging-related call
            for (ExpressionList expressionList : assignment.lhsExpressions()) {
                for (Expression expr : expressionList.expressions()) {
                    if (expr.is(Tree.Kind.NAME)) {
                        Name name = (Name) expr;
                        if (isLoggingRelatedAssignment(assignment.assignedValue())) {
                            loggerVariables.add(name.name());
                        }
                    }
                }
            }
            super.visitAssignmentStatement(assignment);
        }

        private boolean isLoggingRelatedAssignment(Expression expression) {
            if (expression.is(Tree.Kind.CALL_EXPR)) {
                CallExpression callExpr = (CallExpression) expression;
                Expression callee = callExpr.callee();

                // Check for logging.getLogger(), getLogger(), or Logger()
                if (callee.is(Tree.Kind.QUALIFIED_EXPR)) {
                    QualifiedExpression qualExpr = (QualifiedExpression) callee;
                    String methodName = qualExpr.name().name();
                    return "getLogger".equals(methodName) || "Logger".equals(methodName);
                } else if (callee.is(Tree.Kind.NAME)) {
                    Name name = (Name) callee;
                    return "getLogger".equals(name.name()) || "Logger".equals(name.name());
                }
            }
            return false;
        }
    }

    /**
     * Check if a call expression is a logging method with bad format interpolation
     */
    private void checkCallExpression(SubscriptionContext ctx) {
        if (!isUsingLoggingLib) {
            return;
        }

        CallExpression callExpression = (CallExpression) ctx.syntaxNode();
        Expression callee = callExpression.callee();

        // Check if this is a logging method call
        if (callee.is(Tree.Kind.QUALIFIED_EXPR)) {
            QualifiedExpression qualifiedExpression = (QualifiedExpression) callee;
            String methodName = qualifiedExpression.name().name();

            if (LOGGING_METHODS.contains(methodName)) {
                Expression qualifier = qualifiedExpression.qualifier();

                // Check if qualifier is "logging" module or a known logger variable
                if (isLoggingQualifier(qualifier)) {
                    checkLoggingArguments(ctx, callExpression);
                }
            }
        }
    }

    /**
     * Check if the qualifier is a logging module or logger variable
     */
    private boolean isLoggingQualifier(Expression qualifier) {
        if (qualifier.is(Tree.Kind.NAME)) {
            String qualifierName = ((Name) qualifier).name();
            // Check if it's the logging module or a known logger variable
            return LOGGING_MODULE_NAMES.contains(qualifierName) ||
                   loggerVariableNames.contains(qualifierName);
        }
        return false;
    }

    /**
     * Check the arguments of a logging call for bad format interpolation
     */
    private void checkLoggingArguments(SubscriptionContext ctx, CallExpression callExpression) {
        List<Argument> arguments = callExpression.arguments();

        if (arguments.isEmpty()) {
            return;
        }

        // For logging.log() and logger.log(), the first argument is the level, the second is the message
        // For other methods, the first argument is the message
        int messageArgIndex = 0;
        Expression callee = callExpression.callee();
        if (callee.is(Tree.Kind.QUALIFIED_EXPR)) {
            QualifiedExpression qualExpr = (QualifiedExpression) callee;
            if ("log".equals(qualExpr.name().name())) {
                messageArgIndex = 1; // For log(), message is the second argument
                if (arguments.size() < 2) {
                    return; // Not enough arguments
                }
            }
        }

        // Get the message argument
        Argument messageArg = arguments.get(messageArgIndex);
        if (messageArg.is(Tree.Kind.REGULAR_ARGUMENT)) {
            RegularArgument regularArg = (RegularArgument) messageArg;
            Expression expression = regularArg.expression();

            // Check for f-strings
            if (isFString(expression)) {
                ctx.addIssue(callExpression, MESSAGE_RULE);
                return;
            }

            // Check for .format() calls
            if (expression.is(Tree.Kind.CALL_EXPR)) {
                CallExpression innerCall = (CallExpression) expression;
                Expression innerCallee = innerCall.callee();

                if (innerCallee.is(Tree.Kind.QUALIFIED_EXPR)) {
                    QualifiedExpression qualExpr = (QualifiedExpression) innerCallee;
                    if ("format".equals(qualExpr.name().name())) {
                        ctx.addIssue(callExpression, MESSAGE_RULE);
                    }
                }
            }
        }
    }

    /**
     * Check if an expression is an f-string
     */
    private boolean isFString(Expression expression) {
        if (expression.is(Tree.Kind.STRING_LITERAL)) {
            StringLiteral stringLiteral = (StringLiteral) expression;
            String value = stringLiteral.firstToken().value();
            // Check if string starts with 'f' or 'F' followed by quote
            return value != null &&
                   (value.startsWith("f\"") || value.startsWith("f'") ||
                    value.startsWith("F\"") || value.startsWith("F'"));
        }
        return false;
    }
}








