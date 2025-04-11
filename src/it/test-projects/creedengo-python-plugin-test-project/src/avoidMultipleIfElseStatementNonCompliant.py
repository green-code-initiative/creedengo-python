# functional RULES : please see HTML description file of this rule (resources directory)

# ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
# ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
#
# NON COMPLIANT use cases
#
# ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
# ///////////////////////////////////////////////////////////////////////////////////////////////////////////////

# NON COMPLIANT
# USE CASE : Non compliant use case to check if following is NON OK :
#  - two uses of the same variable
#  - usage of the same variable on different levels of IF statements
def not_compliant_variables_used_max_twice_in_composed_else_statements():
    nb1 = 0
    if nb1 == 1:
        nb1 = 2
    else:
        if nb1 == 2: # Noncompliant {{Use a match-case statement instead of multiple if-else if possible}}
            nb1 = 1
    return nb1

# NON COMPLIANT
# USE CASE : non compliant use case to check if a variable is not used max twice on several IF / ELSE statements
# at the same level
def not_compliant_variables_used_max_twice_and_differents_variables_used():
    nb1 = 0
    nb2 = 0
    nb3 = 0
    if nb3 == 1 and nb3 == 2 and nb3 == 3: # Noncompliant {{Use a match-case statement instead of multiple if-else if possible}}
        nb1 = 1
    else: # Noncompliant {{Use a match-case statement instead of multiple if-else if possible}}
        nb2 = 2
    if nb2 == 2:
        nb1 = 3
    else:
        nb1 = 4
    return nb1

# NON COMPLIANT
# USE CASE : NON compliant use case to check if following is NOT COMPLIANT :
# one variable is used maximum in two IF / ELSE / elif statements
def not_compliant_variable_used_more_than_twice():
    nb1 = 0
    if nb1 == 1:
        nb1 = 2
    else:
        nb1 = 3
    if nb1 == 2: # Noncompliant {{Use a match-case statement instead of multiple if-else if possible}}
        nb1 = 4
    return nb1

# NON COMPLIANT
# USE CASE : NON compliant use case to check if following is NOT OK :
#  - same variable used maximum twice : no compliant because 2 IFs and 1 ELSE
def not_compliant_variable_used_more_than_twice_in_if_statements_at_differents_levels():
    nb1 = 0
    if nb1 == 1:
        if nb1 == 2:
            nb1 = 1
        else: # Noncompliant {{Use a match-case statement instead of multiple if-else if possible}}
            nb1 = 3
    else:
        nb1 = 2
    return nb1

# NON COMPLIANT
# USE CASE : non compliant use case to check if following is NOT OK :
#  - two uses of the same variable : use thre times with 2 IFs and 1 ELSE
#  - usage of the same variable on different levels of IF statements
def not_compliant_variable_used_more_than_twice_in_composed_else_statements():
    nb1 = 0
    if nb1 == 1:
        nb1 = 2
    else:
        if nb1 == 2:  # Noncompliant {{Use a match-case statement instead of multiple if-else if possible}}
            nb1 = 1
        else: # Noncompliant {{Use a match-case statement instead of multiple if-else if possible}}
            nb1 = 3
    return nb1

# NON COMPLIANT
# USE CASE : non compliant use case to check if following is NOT OK :
#  - two uses of the same variable : use thre times with 2 IFs and 1 ELSE
#  - usage of the same variable on different levels of IF statements
def not_compliant_variable_used_more_than_twice_in_composed_else_statements_scenario_2():
    nb1 = 0
    if nb1 == 1:
        if nb1 == 3:
            nb1 = 4
        else: # Noncompliant {{Use a match-case statement instead of multiple if-else if possible}}
            nb1 = 5
    else:
        if nb1 == 2: # Noncompliant {{Use a match-case statement instead of multiple if-else if possible}}
            nb1 = 1
        else: # Noncompliant {{Use a match-case statement instead of multiple if-else if possible}}
            nb1 = 3
    return nb1

# NON COMPLIANT
# USE CASE : non compliant use case to check if following is NOT OK :
#  - two uses of the same variable : use thre times with 2 IFs and 1 ELSE
#  - usage of the same variable on different levels of IF statements
def not_compliant_variable_used_more_than_twice_in_composed_else_statements_scenario_3():
    nb1 = 0
    nb2 = 0
    if nb1 == 1:
        if nb1 == 3:
            nb1 = 4
        else: # Noncompliant {{Use a match-case statement instead of multiple if-else if possible}}
            nb1 = 5
    elif nb2 == 2:
        if nb1 == 4:
            nb1 = 5
        else: # Noncompliant {{Use a match-case statement instead of multiple if-else if possible}}
            nb1 = 6
    return nb1

# NON COMPLIANT
# USE CASE : non compliant use case to check if following is NOT OK :
#  - two uses of the same variable : use thre times with 2 IFs and 1 ELSE
#  - usage of the same variable on different levels of IF statements
def not_compliant_variable_used_more_than_twice_in_composed_else_statements_scenario_4():
    nb1 = 0
    nb2 = 0
    if nb1 == 1:
        if nb2 == 3:
            nb1 = 4
        else:
            nb1 = 5
    elif nb2 == 2:
        if nb1 == 3:
            nb1 = 4
        else: # Noncompliant {{Use a match-case statement instead of multiple if-else if possible}}
            nb1 = 5
    return nb1

# NON COMPLIANT
# USE CASE : NON compliant use case to check if following is NOT OK :
#  - the same variable must used maximum twice
#  - usage of the same variable on different levels of IF / ELSE statements
def not_compliant_variable_used_max_twice_in_composed_else_statements():
    nb1 = 0
    if nb1 == 1:
        nb1 = 2
    else:
        if nb1 == 2: # Noncompliant {{Use a match-case statement instead of multiple if-else if possible}}
            nb1 = 1
        else: # Noncompliant {{Use a match-case statement instead of multiple if-else if possible}}
            if nb1 == 3: # Noncompliant {{Use a match-case statement instead of multiple if-else if possible}}
                nb1 = 4
            else: # Noncompliant {{Use a match-case statement instead of multiple if-else if possible}}
                nb1 = 5
    return nb1

# NON COMPLIANT
# USE CASE : NON compliant use case to check if following is NOT OK :
#  - more than twice uses of the same variable
#  - usage of the same variable on different kind of test statements (IF and elif)
def not_compliant_the_same_variable_is_used_more_than_twice():
    nb1 = 0
    nb2 = 10
    if nb1 == 1:
        nb2 = 1
    elif nb1 == nb2:
        nb2 = 2
    else: # Noncompliant {{Use a match-case statement instead of multiple if-else if possible}}
        nb2 = 4
    return nb2

# NON COMPLIANT
# USE CASE : NON compliant use case to check if following is NOT OK :
#  - more than twice uses of the same variable
#  - usage of the same variable on different kind of test statements (IF and elif)
def not_compliant_the_same_variable_is_used_many_times():
    nb1 = 0
    nb2 = 10
    nb3 = 11
    if nb1 == 1:
        nb2 = 1
    elif nb1 == nb2:
        nb2 = 2
    elif nb3 == nb1: # Noncompliant {{Use a match-case statement instead of multiple if-else if possible}}
        nb2 = 3
    else: # Noncompliant {{Use a match-case statement instead of multiple if-else if possible}}
        nb2 = 4
    return nb2
