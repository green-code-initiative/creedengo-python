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
import org.sonar.plugins.python.api.tree.ForStatement;
import org.sonar.plugins.python.api.tree.WhileStatement;
import org.sonar.plugins.python.api.tree.StatementList;
import org.sonar.plugins.python.api.tree.CallExpression;
import org.sonar.plugins.python.api.tree.Expression;
import org.sonar.plugins.python.api.tree.Tree;
import org.sonar.plugins.python.api.tree.AssignmentStatement;
import org.sonar.plugins.python.api.tree.Name;
import org.sonarsource.analyzer.commons.annotations.DeprecatedRuleKey;

import java.util.List;
import java.util.Set;

@Rule(key = "GCI204")
@DeprecatedRuleKey(repositoryKey = "ecocode-python", ruleKey = "EC204")
public class LoopInvariantStatementCheck extends PythonSubscriptionCheck {

	private static final String MESSAGE_ERROR = "Loop invariant statement detected. Consider moving this computation outside the loop to improve performance.";
	private static final Set<String> POTENTIALLY_INVARIANT_FUNCTIONS = Set.of(
			"len", "sum", "sorted", "list", "tuple", "set", "dict", "max", "min"
	);

	@Override
	public void initialize(Context context) {
		// Enregistrement pour les boucles for et while
		context.registerSyntaxNodeConsumer(Tree.Kind.FOR_STMT, this::visitLoop);
		context.registerSyntaxNodeConsumer(Tree.Kind.WHILE_STMT, this::visitLoop);
	}

	private void visitLoop(SubscriptionContext ctx) {
		// Obtenir le corps de la boucle, qu'il s'agisse d'une boucle for ou while
		StatementList body = ctx.syntaxNode() instanceof ForStatement forStatement
				? forStatement.body()
				: ((WhileStatement) ctx.syntaxNode()).body();

		analyzeLoopBody(ctx, body);
	}

	private void analyzeLoopBody(SubscriptionContext ctx, StatementList body) {
		// Parcourir les instructions du corps de la boucle
		body.statements().stream()
				.filter(stmt -> stmt.is(Tree.Kind.ASSIGNMENT_STMT))
				.map(stmt -> (AssignmentStatement) stmt)
				.forEach(assignment -> checkAssignmentForInvariants(ctx, assignment));
	}

	private void checkAssignmentForInvariants(SubscriptionContext ctx, AssignmentStatement assignment) {
		// Vérifier si la partie droite de l'assignation contient un appel de fonction potentiellement invariant
		Expression expression = assignment.assignedValue();
		checkExpressionForInvariants(ctx, assignment, expression);
	}

	private void checkExpressionForInvariants(SubscriptionContext ctx, AssignmentStatement assignment, Expression expr) {
		if (expr.is(Tree.Kind.CALL_EXPR)) {
			CallExpression callExpr = (CallExpression) expr;
			if (isPotentiallyInvariantCall(callExpr)) {
				ctx.addIssue(assignment, MESSAGE_ERROR);
			}
		}
	}

	private boolean isPotentiallyInvariantCall(CallExpression callExpr) {
		if (!callExpr.callee().is(Tree.Kind.NAME)) {
			return false;
		}

		var functionName = ((Name) callExpr.callee()).name();
		return POTENTIALLY_INVARIANT_FUNCTIONS.contains(functionName);
	}
}