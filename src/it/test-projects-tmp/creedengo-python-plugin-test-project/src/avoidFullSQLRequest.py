def display_message(argument1):
    print(argument1)

display_message('   sElEcT * fRoM myTable') # Noncompliant {{Don't use the query SELECT * FROM}}
display_message('   sElEcT user fRoM myTable')

requestNonCompiliant = '   SeLeCt * FrOm myTable' # Noncompliant {{Don't use the query SELECT * FROM}}
requestCompiliant = '   SeLeCt user FrOm myTable'
display_message(requestNonCompiliant)
display_message(requestCompiliant)
