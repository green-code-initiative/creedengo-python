def display_message(argument1):
    print(argument1)

display_message('   sElEcT user fRoM myTable WhErE id > 0') # Noncompliant {{Don't use a SELECT _ FROM _ query without a limit}}
display_message('   sElEcT user fRoM myTable WhErE id > 0 LiMiT 10')

requestNonCompiliant = '   SeLeCt user FrOm myTable WhErE id > 0' # Noncompliant {{Don't use a SELECT _ FROM _ query without a limit}}
requestCompiliant = '   SeLeCt user FrOm myTable WhErE id > 0 LiMiT 10'
display_message(requestNonCompiliant)
display_message(requestCompiliant)
