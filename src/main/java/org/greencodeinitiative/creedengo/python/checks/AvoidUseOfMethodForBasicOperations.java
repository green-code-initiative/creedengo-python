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
import org.sonar.plugins.python.api.tree.Expression;

import java.util.List;
import java.util.Set;

import org.sonar.plugins.python.api.tree.BinaryExpression;

@Rule(key = "GCI22")
public class AvoidUseOfMethodForBasicOperations extends PythonSubscriptionCheck {

    static final String MESSAGE =
            "Avoid using methods for simple basic operations.";

    /**
     * Dunder methods that have a direct native-operator equivalent in Python.
     */
    private static final Set<String> DUNDER_METHODS = Set.of(
            //arithmetic
            "__add__", "__sub__", "__mul__", "__truediv__", "__floordiv__",
            "__mod__", "__pow__", "__matmul__",
            //reflected arithmetic
            "__radd__", "__rsub__", "__rmul__", "__rtruediv__", "__rfloordiv__",
            "__rmod__", "__rpow__",
            //in-place arithmetic
            "__iadd__", "__isub__", "__imul__", "__itruediv__", "__ifloordiv__",
            "__imod__", "__ipow__",
            //comparisons
            "__eq__", "__ne__", "__lt__", "__le__", "__gt__", "__ge__",
            //logical
            "__and__", "__or__", "__xor__", "__lshift__", "__rshift__",
            "__iand__", "__ior__", "__ixor__", "__ilshift__", "__irshift__",
            "__invert__", "__neg__", "__pos__",
            //container / subscript
            "__len__", "__contains__", "__getitem__", "__setitem__", "__delitem__",
            //string / sequence
            "__bool__", "__int__", "__float__", "__str__"
    );

    /**
     * Generic trivial function names that are likely pure operator wrappers.
     * We only flag functions whose *name* is in this set AND whose body is
     * a single basic expression (or assign + return of a basic expression).
     *
     * Business-meaningful names (is_adult, tax_rate_applies…) are intentionally
     * NOT in this set.
     */
    private static final Set<String> TRIVIAL_FUNCTION_NAMES = Set.of(
            "add", "sub", "subtract", "mul", "multiply", "div", "divide",
            "mod", "pow", "neg", "negate",
            "eq", "ne", "lt", "le", "gt", "ge",
            "is_eq", "is_gt", "is_lt", "is_gte", "is_lte", "is_ne",
            "compare", "cmp",
            "contains", "concat", "join_two",
            "add_op", "add_one", "increment", "decrement",
            "identity", "wrapper", "bool_wrapper"
    );

    @Override
    public void initialize(Context context) {
        // Detect explicit dunder calls and bad join() at every expression level
        context.registerSyntaxNodeConsumer(Tree.Kind.CALL_EXPR, this::visitCallExpr);
        // Detect append() inside a for-loop
        context.registerSyntaxNodeConsumer(Tree.Kind.FOR_STMT, this::visitForLoop);
        // Detect trivial wrapper functions
        context.registerSyntaxNodeConsumer(Tree.Kind.FUNCDEF, this::visitFunctionDef);
    }

    private void visitCallExpr(SubscriptionContext ctx) {
        CallExpression call = (CallExpression) ctx.syntaxNode();

        Expression callee = call.callee();

        // Pattern: obj.__dunder__(args) => callee is a QualifiedExpression
        if (callee instanceof QualifiedExpression qualifiedExpr) {
            String methodName = qualifiedExpr.name().name();

            if (DUNDER_METHODS.contains(methodName)) {
                ctx.addIssue(call, MESSAGE);
                return;
            }

            if ("join".equals(methodName)
                    && qualifiedExpr.qualifier() instanceof StringLiteral
                    && hasListLiteralArgument(call)) {
                ctx.addIssue(call, MESSAGE);
            }
        }
    }

    /**
     * Returns true if the call has exactly one argument and that argument is
     * an inline list literal (e.g. [a, ", ", b]).
     */
    private boolean hasListLiteralArgument(CallExpression call) {
        if (call.argumentList() == null) return false;
        List<Argument> args = call.argumentList().arguments();
        if (args.size() != 1) return false;
        Argument arg = args.get(0);
        return arg instanceof RegularArgument regArg
                && regArg.expression() instanceof ListLiteral;
    }

    private void visitForLoop(SubscriptionContext ctx) {
        ForStatement forStmt = (ForStatement) ctx.syntaxNode();
        for (Statement stmt : forStmt.body().statements()) {
            findAppendCalls(ctx, stmt);
        }
    }

    private void findAppendCalls(SubscriptionContext ctx, Statement stmt) {
        // We look for ExpressionStatement wrapping a CallExpression to .append()
        if (!(stmt instanceof ExpressionStatement exprStmt)) return;
        if (!(exprStmt.expressions().get(0) instanceof CallExpression call)) return;
        if (!(call.callee() instanceof QualifiedExpression qe)) return;
        if ("append".equals(qe.name().name())) {
            ctx.addIssue(call, MESSAGE);
        }
    }

    private void visitFunctionDef(SubscriptionContext ctx) {
        FunctionDef funcDef = (FunctionDef) ctx.syntaxNode();
        String name = funcDef.name().name();

        if (!TRIVIAL_FUNCTION_NAMES.contains(name)) {
            return; // business function or unknown name => not concern
        }

        List<Statement> stmts = funcDef.body().statements();

        if (stmts.size() == 1) {
            checkPatternA(ctx, stmts.get(0));
        } else if (stmts.size() == 2) {
            checkPatternB(ctx, stmts.get(0), stmts.get(1));
        }
    }

    /**
     * Pattern A:  return <basic_expr>
     * Flags the ReturnStatement if the returned expression is basic.
     */
    private void checkPatternA(SubscriptionContext ctx, Statement stmt) {
        if (!(stmt instanceof ReturnStatement returnStmt)) return;
        List<Expression> exprs = returnStmt.expressions();
        if (exprs.size() == 1 && isBasicExpression(exprs.get(0))) {
            ctx.addIssue(returnStmt, MESSAGE);
        }
    }

    /**
     * Pattern B:  var = <basic_expr>   followed by   return var
     * Flags the ReturnStatement when the assignment feeds straight into the return.
     */
    private void checkPatternB(SubscriptionContext ctx, Statement first, Statement second) {
        if (!(first instanceof AssignmentStatement assignStmt)) return;
        if (!(second instanceof ReturnStatement returnStmt)) return;

        // The return must reference the variable that was just assigned
        List<Expression> returnExprs = returnStmt.expressions();
        if (returnExprs.size() != 1) return;
        if (!(returnExprs.get(0) instanceof Name returnedName)) return;

        ExpressionList lhsExprs = assignStmt.lhsExpressions().get(0);
        if (lhsExprs.expressions().size() != 1) return;
        if (!(lhsExprs.expressions().get(0) instanceof Name assignedName)) return;

        if (!assignedName.name().equals(returnedName.name())) return;

        if (isBasicExpression(assignStmt.assignedValue())) {
            ctx.addIssue(returnStmt, MESSAGE);
        }
    }

    private boolean isBasicExpression(Tree expr) {
        return expr instanceof BinaryExpression
                || expr instanceof UnaryExpression
                || expr instanceof Name;
    }

}
