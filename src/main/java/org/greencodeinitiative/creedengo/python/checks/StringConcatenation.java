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
import org.sonar.plugins.python.api.tree.Name;
import org.sonar.plugins.python.api.tree.CompoundAssignmentStatement;
import org.sonar.plugins.python.api.tree.Tree;
import org.sonar.plugins.python.api.tree.Expression;
import org.sonar.plugins.python.api.tree.AssignmentStatement;

import java.util.ArrayList;
import java.util.List;



@Rule(key="GCI105")
public class StringConcatenation extends PythonSubscriptionCheck {

    private final List<String> stringVariables = new ArrayList<>();

  public static final String DESCRIPTION = "Concatenation of strings should be done using f-strings or str.join()";


  @Override
  public void initialize(Context context) {
    context.registerSyntaxNodeConsumer(Tree.Kind.STRING_ELEMENT, this::findStringVariable);

    context.registerSyntaxNodeConsumer(Tree.Kind.COMPOUND_ASSIGNMENT, this::checkAssignment);

  }

  private void findStringVariable(SubscriptionContext context) {
    Tree node = context.syntaxNode();
    if (node.is(Tree.Kind.STRING_ELEMENT)) {
        Tree current = node;
        while (current != null && !current.is(Tree.Kind.ASSIGNMENT_STMT)) {
            current = current.parent();
        }
        
        if (current != null && current.is(Tree.Kind.ASSIGNMENT_STMT)) {
            AssignmentStatement assignment = (AssignmentStatement) current;
            
           
            if (!assignment.assignedValue().is(Tree.Kind.LIST_LITERAL) && 
                !assignment.assignedValue().is(Tree.Kind.LIST_COMPREHENSION)) {
                
                String variableName = Utils.getVariableName(context);
                if (variableName != null) {
                    stringVariables.add(variableName);
                }
            }
        }
    }
}

  private void checkAssignment(SubscriptionContext context) {

    CompoundAssignmentStatement compoundAssignment = (CompoundAssignmentStatement) context.syntaxNode();
    if (compoundAssignment.compoundAssignmentToken().value().equals("+=")) {
      Expression lhsExpression = compoundAssignment.lhsExpression();
      if (lhsExpression.is(Tree.Kind.NAME)) {
          String variableName = ((Name) lhsExpression).name();
          if (stringVariables.contains(variableName)) {
              context.addIssue(lhsExpression.firstToken(), DESCRIPTION);
      }
  }}}


  

    
}
