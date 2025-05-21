package org.greencodeinitiative.creedengo.python.checks;

import org.sonar.check.Rule;
import org.sonar.plugins.python.api.PythonSubscriptionCheck;
import org.sonar.plugins.python.api.SubscriptionContext;
import org.sonar.plugins.python.api.tree.AssignmentStatement;
import org.sonar.plugins.python.api.tree.BinaryExpression;
import org.sonar.plugins.python.api.tree.CallExpression;
import org.sonar.plugins.python.api.tree.Expression;
import org.sonar.plugins.python.api.tree.ExpressionStatement;
import org.sonar.plugins.python.api.tree.ForStatement;
import org.sonar.plugins.python.api.tree.IfStatement;
import org.sonar.plugins.python.api.tree.Name;
import org.sonar.plugins.python.api.tree.Statement;
import org.sonar.plugins.python.api.tree.StatementList;
import org.sonar.plugins.python.api.tree.Tree;
import org.sonar.plugins.python.api.tree.WhileStatement;
import org.sonarsource.analyzer.commons.annotations.DeprecatedRuleKey;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Rule(key = "GCI204")
@DeprecatedRuleKey(repositoryKey = "ecocode-python", ruleKey = "EC204")
public class LoopInvariantStatementCheck extends PythonSubscriptionCheck {

	private static final String MESSAGE_ERROR =
			"Loop invariant statement detected. Consider moving this computation outside the loop to improve performance.";
	private static final Set<String> POTENTIALLY_INVARIANT_FUNCTIONS = Set.of(
			"len", "sum", "sorted", "list", "tuple", "set", "dict", "max", "min", "abs"
	);

	/**
	 * A set used to store nodes of type {@link Tree} that have already been analyzed
	 * or reported during the loop invariant checking process. This ensures that each
	 * node is processed only once, avoiding redundant checks and duplicate reporting
	 * of issues.
	 */
	private final Set<Tree> reportedNodes = new HashSet<>();

	/**
	 * Initializes the context for the rule by registering syntax node consumers
	 * that specifically target "for" and "while" loop statements.
	 *
	 * @param context the context used to register and manage syntax node analysis
	 */
	@Override
	public void initialize(Context context) {
		context.registerSyntaxNodeConsumer(Tree.Kind.FOR_STMT, this::visitLoop);
		context.registerSyntaxNodeConsumer(Tree.Kind.WHILE_STMT, this::visitLoop);
	}

	/**
	 * Processes and analyzes "for" and "while" loops to reset the set of reported nodes and analyze
	 * the loop body for potential invariant issues.
	 *
	 * @param ctx the subscription context managing the analysis and issue reporting
	 */
	private void visitLoop(SubscriptionContext ctx) {
		// Reset the set of reported nodes for each new top-level loop analysis
		if (ctx.syntaxNode().parent() == null || !(ctx.syntaxNode().parent() instanceof StatementList) ||
				(ctx.syntaxNode().parent() instanceof StatementList &&
						(ctx.syntaxNode().parent().parent() == null ||
								!(ctx.syntaxNode().parent().parent() instanceof ForStatement ||
										ctx.syntaxNode().parent().parent() instanceof WhileStatement)))) {
			reportedNodes.clear();
		}

		// Log the line number of the loop being analyzed
		// Extract the current loop statement node
		Statement stmt = (Statement) ctx.syntaxNode();
		StatementList body;
		if (stmt instanceof ForStatement) {
			body = ((ForStatement) stmt).body();
		} else {
			body = ((WhileStatement) stmt).body();
		}
		if (body != null) {
			analyzeLoopBody(ctx, body);
		}
	}

	/**
	 * Analyzes the body of a loop to identify and report issues related to loop invariants.
	 * This method iterates through the statements in the provided loop body and performs
	 * specific checks depending on the type of statement encountered. Invariant-related issues
	 * are reported through the subscription context as necessary.
	 *
	 * @param ctx  the subscription context managing the analysis and issue reporting
	 * @param body the list of statements representing the body of the loop to analyze
	 */
	private void analyzeLoopBody(SubscriptionContext ctx, StatementList body) {
		// Log the start of the loop body analysis
		for (Statement stmt : body.statements()) {
			if (stmt instanceof AssignmentStatement) {
				checkAssignmentForInvariants(ctx, (AssignmentStatement) stmt);
			} else if (stmt instanceof IfStatement ifStmt) {
				if (ifStmt.body() != null) {
					analyzeLoopBody(ctx, ifStmt.body());
				}
				if (ifStmt.elseBranch() != null && ifStmt.elseBranch() instanceof StatementList) {
					analyzeLoopBody(ctx, (StatementList) Objects.requireNonNull(ifStmt.elseBranch()));
				}
			} else if (stmt instanceof ExpressionStatement) {
				// Extract the first expression from the statement
				Expression expr = ((ExpressionStatement) stmt).expressions().get(0);
				if (expr instanceof CallExpression && isPotentiallyInvariantCall((CallExpression) expr) &&
						!reportedNodes.contains(expr)) {
					// Report the issue and add the expression to the set of reported nodes
					ctx.addIssue(expr, MESSAGE_ERROR);
					reportedNodes.add(expr);
				}
			} else if (stmt instanceof ForStatement || stmt instanceof WhileStatement) {
				// For nested loops, analyze the body of the loop
				StatementList nestedBody;
				if (stmt instanceof ForStatement) {
					nestedBody = ((ForStatement) stmt).body();
				} else {
					nestedBody = ((WhileStatement) stmt).body();
				}
				if (nestedBody != null) {
					analyzeLoopBody(ctx, nestedBody);
				}
			}
		}
	}

	/**
	 * Tracks and stores nodes corresponding to analyzed loop assignments.
	 * Used to ensure that each node is checked only once for invariants.
	 */
	private void checkAssignmentForInvariants(SubscriptionContext ctx, AssignmentStatement assignment) {
		// Log the line number of the current assignment being analyzed
		// Get the expression assigned to the target variable
		Expression valueExpr = assignment.assignedValue();
		checkExpressionForInvariants(ctx, valueExpr);
	}

	/**
	 * Analyzes the provided expression to check for loop invariants, reporting issues if necessary.
	 * This method identifies potentially invariant function calls and recursively checks binary
	 * expressions for invariants in their operands.
	 *
	 * @param ctx  the subscription context managing the analysis and issue reporting
	 * @param expr the expression to analyze for invariants
	 */
	private void checkExpressionForInvariants(SubscriptionContext ctx, Expression expr) {
		if (expr instanceof CallExpression callExpr) {
			if (isPotentiallyInvariantCall(callExpr) && !reportedNodes.contains(expr)) {
				ctx.addIssue(expr, MESSAGE_ERROR);
				reportedNodes.add(expr);
			}
		} else if (expr instanceof BinaryExpression binaryExpr) {
			// For binary expressions, recursively check both operands for invariants
			checkExpressionForInvariants(ctx, binaryExpr.leftOperand());
			checkExpressionForInvariants(ctx, binaryExpr.rightOperand());
		}
	}

	/**
	 * Determines whether the provided function call is potentially invariant.
	 * A call is considered potentially invariant if its callee matches a predefined set of function names
	 * deemed to exhibit invariant behavior in the context of loop analysis.
	 *
	 * @param callExpr the function call expression to analyze
	 * @return true if the function call is deemed potentially invariant; false otherwise
	 */
	private boolean isPotentiallyInvariantCall(CallExpression callExpr) {
		// Log the line number of the function call being checked
		Expression callee = callExpr.callee();
		if (callee instanceof Name) {
			String name = ((Name) callee).name();
			// Return true if the function name matches a potentially invariant function
			return POTENTIALLY_INVARIANT_FUNCTIONS.contains(name);
		}
		return false;
	}
}