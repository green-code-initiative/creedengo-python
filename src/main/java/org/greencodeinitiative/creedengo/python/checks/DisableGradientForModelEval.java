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
import org.sonar.plugins.python.api.tree.Expression;
import org.sonar.plugins.python.api.tree.CallExpression;
import org.sonar.plugins.python.api.tree.FunctionDef;
import org.sonar.plugins.python.api.tree.QualifiedExpression;
import org.sonar.plugins.python.api.tree.Tree;
import org.sonar.plugins.python.api.tree.WithItem;
import org.sonar.plugins.python.api.tree.WithStatement;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Rule(key ="GCI100")
public class DisableGradientForModelEval extends PythonSubscriptionCheck {
    
    private static final String DESCRIPTION = "PyTorch : Disable gradient computation when evaluating a model to save memory and computation time";
    
    private final Map<Tree, Set<String>> evalModelsInContext = new HashMap<>();
    private final Map<Tree, Set<Tree>> noGradScopesInContext = new HashMap<>();
    
    @Override
    public void initialize(Context context) {
        context.registerSyntaxNodeConsumer(Tree.Kind.QUALIFIED_EXPR, this::checkEvalCall);
        context.registerSyntaxNodeConsumer(Tree.Kind.WITH_STMT, this::checkWithNoGrad);
        context.registerSyntaxNodeConsumer(Tree.Kind.CALL_EXPR, this::checkModelCall);
        
        context.registerSyntaxNodeConsumer(Tree.Kind.FUNCDEF, this::initializeContext);
    }
    
    private void initializeContext(SubscriptionContext context) {
        FunctionDef funcDef = (FunctionDef) context.syntaxNode();
        evalModelsInContext.put(funcDef, new HashSet<>());
        noGradScopesInContext.put(funcDef, new HashSet<>());
    }
    
    private Tree getEnclosingContext(Tree node) {
        Tree current = node;
        while (current != null) {
            if (current.is(Tree.Kind.FUNCDEF)) {
                return current;
            }
            current = current.parent();
        }
        return null; 
    }
    
    private void checkEvalCall(SubscriptionContext context) {
        QualifiedExpression expr = (QualifiedExpression) context.syntaxNode();
        
        
        if (expr.name().name().equals("eval")) {
            String modelName = expr.qualifier().firstToken().value();
            
            
            Tree enclosingContext = getEnclosingContext(expr);
            
            evalModelsInContext.computeIfAbsent(enclosingContext, k -> new HashSet<>()).add(modelName);
        }
    }
    
    private void checkWithNoGrad(SubscriptionContext context) {
        WithStatement withStmt = (WithStatement) context.syntaxNode();
        
        
        for (WithItem item : withStmt.withItems()) {
            if (isNoGradCall(item.test())) {
                
                Tree enclosingContext = getEnclosingContext(withStmt);
                
                
                noGradScopesInContext.computeIfAbsent(enclosingContext, k -> new HashSet<>()).add(withStmt);
                return;
            }
        }
    }
    
    private boolean isNoGradCall(Expression expr) {
        if (expr.is(Tree.Kind.CALL_EXPR)) {
            CallExpression callExpr = (CallExpression) expr;
            if (callExpr.callee().is(Tree.Kind.QUALIFIED_EXPR)) {
                QualifiedExpression qualExpr = (QualifiedExpression) callExpr.callee();
                return qualExpr.qualifier().firstToken().value().equals("torch") && 
                       qualExpr.name().name().equals("no_grad");
            }
        }
        return false;
    }
    
    private void checkModelCall(SubscriptionContext context) { 
        CallExpression callExpr = (CallExpression) context.syntaxNode();
        
        Tree enclosingContext = getEnclosingContext(callExpr);
        

        Expression callee = callExpr.callee();
        String modelName = null;
        
        if (!callee.is(Tree.Kind.QUALIFIED_EXPR)) {
            modelName = callee.firstToken().value();
        }
        
        Set<String> modelsInEvalMode = evalModelsInContext.getOrDefault(enclosingContext, new HashSet<>());
        
        if (modelName != null && modelsInEvalMode.contains(modelName)) {
           
            if (!isInNoGradContext(callExpr, enclosingContext)) {
                context.addIssue(callExpr, DESCRIPTION);
            }
        }
    }
    
    private boolean isInNoGradContext(Tree tree, Tree enclosingContext) {
        Set<Tree> noGradScopes = noGradScopesInContext.getOrDefault(enclosingContext, new HashSet<>());
        
        Tree current = tree;
        while (current != null && current != enclosingContext) {
            if (noGradScopes.contains(current)) {
                return true;
            }
            current = current.parent();
        }
        return false;
    }
}