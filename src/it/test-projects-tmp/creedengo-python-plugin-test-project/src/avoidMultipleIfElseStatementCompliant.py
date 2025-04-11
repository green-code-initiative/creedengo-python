# functional RULES : please see HTML description file of this rule (resources directory)

# ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
# ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
#
# COMPLIANT use cases
#
# ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
# ///////////////////////////////////////////////////////////////////////////////////////////////////////////////

# COMPLIANT
# USE CASE : compliant use case to check if a variable is used maximum twice on several IF / ELSE statements
# at the same level AND no problem with several IF staments at the same level using different variables
def compliant_variables_used_max_twice_and_differents_variables():
    nb1 = 0
    nb2 = 0
    nb3 = 0
    if nb3 != 1 and nb1 > 1 :
        nb1 = 1
    else :
        nb2 = 2
    if nb2 == 2 :
        nb1 = 3
    else :
        nb1 = 4
    return nb1

# COMPLIANT
# USE CASE : compliant use case to check if a variable is used maximum twice on several IF / ELSE statements
# at the same level AND no problem with several IF staments at the same level using different variables
def compliant_variables_used_max_twice_and_differents_variables_at_diff_levels():
    nb1 = 0
    nb2 = 0
    nb3 = 0
    if nb1 < 1:
        if nb2 == 2:
            nb3 = 3
        else:
            nb3 = 4
    else:
        nb2 = 2
    if nb3 >= 1:
        if nb2 == 2:
            nb1 = 3
        else:
            nb1 = 4
    else:
        nb1 = 2
    return nb1

# COMPLIANT
# USE CASE : compliant use case to check if a variable is used maximum twice on several IF / ELSE statements
# at the same level AND no problem with several IF staments at the same level using different variables
def compliant_variables_used_max_twice_and_diff_variables_used_at_diff_levels_scenario_2():
    nb1 = 0
    nb2 = 0
    nb3 = 0
    if nb1 <= 1:
        if nb2 == 2:
            if nb3 == 2:
                nb3 = 3
            else:
                nb3 = 4
        else:
            nb3 = 4
    else:
        nb2 = 2
    if nb3 == 1:
        if nb2 == 2:
            nb1 = 3
        else:
            nb1 = 4
    else:
        nb1 = 2
    return nb1

# COMPLIANT
# USE CASE : compliant use case to check if one variable is used maximum twice in different IF statements
def compliant_variables_used_maximum_twice_in_if_statements():
    nb1 = 0
    if nb1 == 1:
        nb1 = 1
    if nb1 == 2:
        nb1 = 3
    return nb1

# COMPLIANT
# USE CASE : compliant use case to check if following is OK :
#  - two uses of the same variable
#  - usage of the same variable on different levels of IF statements
def compliant_variables_used_max_twice_in_composed_else_statements():
    nb1 = 0
    nb2 = 0
    nb3 = 0
    if nb1 == 1:
        nb1 = 2
    else:
        if nb2 == 2:
            nb1 = 1
        else:
            if nb3 == 4:
                nb1 = 3
            else:
                nb1 = 6
    return nb1

# COMPLIANT
# USE CASE : compliant use case to check if following is OK :
#  - two uses of the same variable
#  - usage of the same variable on different kind of test statements (IF and elif)
def compliant_variables_used_max_twice_in_if_orelif_statements():
    nb1 = 0
    nb2 = 10
    if nb1 == 1:
        nb2 = 1
    elif nb1 == nb2:
        nb2 = 2
    return nb2

# COMPLIANT
# USE CASE : compliant use case to check if following is OK :
#  - two uses of the same variable
#  - usage of the same variable on different kind of test statements (IF and elif)
def compliant_variables_used_max_twice_in_if_orelif_statements_scenario_2():
    nb1 = 0
    nb2 = 10
    nb3 = 3
    nb4 = 1
    nb5 = 2
    if nb1 == 1:
        nb2 = 1
    elif nb3 == nb2:
        nb2 = 2
    elif nb4 == nb5:
        nb2 = 4
    else:
        nb2 = 3
    return nb2

# COMPLIANT
# USE CASE (secondary) : check no NullPointerException if no variables really used in conditions
# USE CASE : compliant use case to check if following is OK :
#  - more than twice uses of the same variable
#  - BUT not directly used, only inside a function
def compliant_the_same_variable_is_used_more_than_twice_but_inside_functions():
    nb1 = 3
    nb2 = 10
    if len(nb1) == 1:
        nb2 = 1
    elif len(nb1) == 3:
        nb2 = 2
    else:
        nb2 = 4
    return nb2
