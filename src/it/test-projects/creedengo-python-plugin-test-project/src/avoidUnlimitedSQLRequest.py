def display_message(argument1):
    print(argument1)

display_message('   sElEcT user fRoM myTable WhErE id > 0') # Noncompliant
display_message('   sElEcT user fRoM myTable WhErE id > 0 LiMiT 10')

requestNonCompiliant = '   SeLeCt user FrOm myTable WhErE id > 0' # Noncompliant
requestCompiliant = '   SeLeCt user FrOm myTable WhErE id > 0 LiMiT 10'
display_message(requestNonCompiliant)
display_message(requestCompiliant)
