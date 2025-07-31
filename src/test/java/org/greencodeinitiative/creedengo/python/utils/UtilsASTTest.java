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

import org.junit.jupiter.api.Test;
import org.sonar.plugins.python.api.SubscriptionContext;
import org.sonar.plugins.python.api.symbols.Symbol;
import org.sonar.plugins.python.api.tree.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UtilsASTTest {

    @Test
    void testGetArgumentsFromCall_nonEmptyArguments() {
        CallExpression call = mock(CallExpression.class);
        ArgList argList = mock(ArgList.class);
        Argument arg = mock(Argument.class);
        when(call.argumentList()).thenReturn(argList);
        when(argList.arguments()).thenReturn(List.of(arg));

        List<Argument> result = UtilsAST.getArgumentsFromCall(call);

        assertEquals(1, result.size());
        assertSame(arg, result.get(0));
    }

    @Test
    void testGetArgumentsFromCall_nullArgumentList() {
        CallExpression call = mock(CallExpression.class);
        when(call.argumentList()).thenReturn(null);

        List<Argument> result = UtilsAST.getArgumentsFromCall(call);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetArgumentsFromCall_nullArguments() {
        CallExpression call = mock(CallExpression.class);
        ArgList argList = mock(ArgList.class);
        when(call.argumentList()).thenReturn(argList);
        when(argList.arguments()).thenReturn(null);

        List<Argument> result = UtilsAST.getArgumentsFromCall(call);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetMethodName_withSymbol() {
        CallExpression call = mock(CallExpression.class);
        Symbol symbol = mock(Symbol.class);
        when(call.calleeSymbol()).thenReturn(symbol);
        when(symbol.name()).thenReturn("baz");

        assertEquals("baz", UtilsAST.getMethodName(call));
    }

    @Test
    void testGetMethodName_nullSymbol() {
        CallExpression call = mock(CallExpression.class);
        when(call.calleeSymbol()).thenReturn(null);

        assertEquals("", UtilsAST.getMethodName(call));
    }

    @Test
    void testGetMethodName_nullName() {
        CallExpression call = mock(CallExpression.class);
        Symbol symbol = mock(Symbol.class);
        when(call.calleeSymbol()).thenReturn(symbol);
        when(symbol.name()).thenReturn(null);

        assertEquals("", UtilsAST.getMethodName(call));
    }

    @Test
    void testGetQualifiedName_withSymbol() {
        CallExpression call = mock(CallExpression.class);
        Symbol symbol = mock(Symbol.class);
        when(call.calleeSymbol()).thenReturn(symbol);
        when(symbol.fullyQualifiedName()).thenReturn("foo.bar");

        assertEquals("foo.bar", UtilsAST.getQualifiedName(call));
    }

    @Test
    void testGetQualifiedName_nullSymbol() {
        CallExpression call = mock(CallExpression.class);
        when(call.calleeSymbol()).thenReturn(null);

        assertEquals("", UtilsAST.getQualifiedName(call));
    }

    @Test
    void testGetQualifiedName_nullFQN() {
        CallExpression call = mock(CallExpression.class);
        Symbol symbol = mock(Symbol.class);
        when(call.calleeSymbol()).thenReturn(symbol);
        when(symbol.fullyQualifiedName()).thenReturn(null);

        assertEquals("", UtilsAST.getQualifiedName(call));
    }

    @Test
    void testGetVariableName_assignmentWithNameSeveralParents() {
        SubscriptionContext context = mock(SubscriptionContext.class);

        AssignmentStatement assignmentGrandParent = mock(AssignmentStatement.class);
        when(assignmentGrandParent.is(Tree.Kind.ASSIGNMENT_STMT)).thenReturn(true);
        when(assignmentGrandParent.parent()).thenReturn(null);

        AssignmentStatement assignmentParent = mock(AssignmentStatement.class);
        when(assignmentParent.is(Tree.Kind.ASSIGNMENT_STMT)).thenReturn(false);
        when(assignmentParent.parent()).thenReturn(assignmentGrandParent);

        AssignmentStatement assignmentChildren = mock(AssignmentStatement.class);
        when(assignmentChildren.is(Tree.Kind.ASSIGNMENT_STMT)).thenReturn(false);
        when(assignmentChildren.parent()).thenReturn(assignmentParent);

        when(context.syntaxNode()).thenReturn(assignmentChildren);

        ExpressionList lhs = mock(ExpressionList.class);
        when(assignmentGrandParent.lhsExpressions()).thenReturn(List.of(lhs));

        Expression expr = mock(Name.class);
        when(lhs.expressions()).thenReturn(List.of(expr));
        when(expr.is(Tree.Kind.NAME)).thenReturn(true);
        when(((Name) expr).name()).thenReturn("var");

        assertEquals("var", UtilsAST.getVariableName(context));
    }

    @Test
    void testGetVariableName_assignmentWithName() {
        SubscriptionContext context = mock(SubscriptionContext.class);
        AssignmentStatement assignment = mock(AssignmentStatement.class);
        when(assignment.is(Tree.Kind.ASSIGNMENT_STMT)).thenReturn(true);
        when(context.syntaxNode()).thenReturn(assignment);

        ExpressionList lhs = mock(ExpressionList.class);
        when(assignment.lhsExpressions()).thenReturn(List.of(lhs));

        Expression expr = mock(Name.class);
        when(lhs.expressions()).thenReturn(List.of(expr));
        when(expr.is(Tree.Kind.NAME)).thenReturn(true);
        when(((Name) expr).name()).thenReturn("var");

        assertEquals("var", UtilsAST.getVariableName(context));
    }

    @Test
    void testGetVariableName_assignmentWithNameButNoNameType() {
        SubscriptionContext context = mock(SubscriptionContext.class);
        AssignmentStatement assignment = mock(AssignmentStatement.class);
        when(assignment.is(Tree.Kind.ASSIGNMENT_STMT)).thenReturn(true);
        when(context.syntaxNode()).thenReturn(assignment);

        ExpressionList lhs = mock(ExpressionList.class);
        when(assignment.lhsExpressions()).thenReturn(List.of(lhs));

        Expression expr = mock(Name.class);
        when(lhs.expressions()).thenReturn(List.of(expr));
        when(expr.is(Tree.Kind.NAME)).thenReturn(false);

        assertNull(UtilsAST.getVariableName(context));
    }

    @Test
    void testGetVariableName_assignmentWithNoLhs() {
        SubscriptionContext context = mock(SubscriptionContext.class);
        AssignmentStatement assignment = mock(AssignmentStatement.class);
        when(assignment.is(Tree.Kind.ASSIGNMENT_STMT)).thenReturn(true);
        when(context.syntaxNode()).thenReturn(assignment);
        when(assignment.lhsExpressions()).thenReturn(List.of());

        assertNull(UtilsAST.getVariableName(context));
    }

    @Test
    void testGetVariableName_assignmentWithEmptyExpressions() {
        SubscriptionContext context = mock(SubscriptionContext.class);
        AssignmentStatement assignment = mock(AssignmentStatement.class);
        when(assignment.is(Tree.Kind.ASSIGNMENT_STMT)).thenReturn(true);
        when(context.syntaxNode()).thenReturn(assignment);

        ExpressionList lhs = mock(ExpressionList.class);
        when(lhs.expressions()).thenReturn(List.of());
        when(assignment.lhsExpressions()).thenReturn(List.of(lhs));

        assertNull(UtilsAST.getVariableName(context));
    }

    @Test
    void testGetVariableName_notAssignment() {
        SubscriptionContext context = mock(SubscriptionContext.class);
        Tree node = mock(Tree.class);
        when(context.syntaxNode()).thenReturn(node);
        when(node.is(Tree.Kind.ASSIGNMENT_STMT)).thenReturn(false);
        when(node.parent()).thenReturn(null);

        assertNull(UtilsAST.getVariableName(context));
    }

    @Test
    void testGetVariableName_contextNull() {
        assertNull(UtilsAST.getVariableName(null));
    }

    @Test
    void testGetVariableName_syntaxNodeNull() {
        SubscriptionContext context = mock(SubscriptionContext.class);
        when(context.syntaxNode()).thenReturn(null);
        assertNull(UtilsAST.getVariableName(context));
    }

    @Test
    void testNthArgumentOrKeyword_byKeyword() {
        RegularArgument argWithoutKeyword = mock(RegularArgument.class);
        when(argWithoutKeyword.is(Tree.Kind.REGULAR_ARGUMENT)).thenReturn(true);
        when(argWithoutKeyword.keywordArgument()).thenReturn(null);

        RegularArgument argWithKeywordWithNameNull = mock(RegularArgument.class);
        when(argWithKeywordWithNameNull.is(Tree.Kind.REGULAR_ARGUMENT)).thenReturn(true);
        Name keywordWithNameNull = mock(Name.class);
        when(keywordWithNameNull.name()).thenReturn(null);
        when(argWithKeywordWithNameNull.keywordArgument()).thenReturn(keywordWithNameNull);

        RegularArgument argWithKeyword = mock(RegularArgument.class);
        when(argWithKeyword.is(Tree.Kind.REGULAR_ARGUMENT)).thenReturn(true);
        Name keyword = mock(Name.class);
        when(keyword.name()).thenReturn("kw");
        when(argWithKeyword.keywordArgument()).thenReturn(keyword);

        List<Argument> args = List.of(argWithoutKeyword, argWithKeywordWithNameNull, argWithKeyword);

        assertSame(argWithKeyword,
                UtilsAST.nthArgumentOrKeyword(2, "kw", args));

    }

    @Test
    void testNthArgumentOrKeyword_byPosition() {
        RegularArgument arg1 = mock(RegularArgument.class);
        when(arg1.is(Tree.Kind.REGULAR_ARGUMENT)).thenReturn(true);
        when(arg1.keywordArgument()).thenReturn(null);

        List<Argument> args = List.of(arg1);

        assertSame(arg1, UtilsAST.nthArgumentOrKeyword(0, "notfound", args));
    }

    @Test
    void testNthArgumentOrKeyword_notFound() {
        Argument arg = mock(Argument.class);
        when(arg.is(Tree.Kind.REGULAR_ARGUMENT)).thenReturn(false);

        List<Argument> args = List.of(arg);

        assertNull(UtilsAST.nthArgumentOrKeyword(0, "kw", args));
    }

    @Test
    void testNthArgumentOrKeyword_regularArgumentWithKeywordButNotNameMatching() {
        RegularArgument arg = mock(RegularArgument.class);
        Name keyword = mock(Name.class);
        when(arg.is(Tree.Kind.REGULAR_ARGUMENT)).thenReturn(true);
        when(arg.keywordArgument()).thenReturn(keyword);
        when(keyword.name()).thenReturn("other");

        List<Argument> args = List.of(arg);

        assertNull(UtilsAST.nthArgumentOrKeyword(0, "kw", args));
    }

    @Test
    void testNthArgumentOrKeyword_emptyArguments() {
        List<Argument> args = List.of();
        assertNull(UtilsAST.nthArgumentOrKeyword(0, "kw", args));
    }

}