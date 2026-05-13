def main():
    import sqlite3

    try:
        connection = sqlite3.connect('database.sqlite')
        cursor = connection.cursor()

        ### WITH CODE-SMELL
        REQUEST = """
                  SELECT EmployeeName, JobTitle, Benefits, TotalPayBenefits, Year
                  FROM Salaries
                  WHERE BasePay >= 5000 \
                  """

        ### WITHOUT CODE-SMELL
        # REQUEST = """
        #           SELECT  EmployeeName, JobTitle, Benefits, TotalPayBenefits, Year
        #           FROM Salaries
        #           WHERE BasePay >= 5000
        #               LIMIT 10_000 \
        #           """

        cursor.execute(REQUEST)
        rows = cursor.fetchall()
        print(f"Nombre de lignes lues : {len(rows)}")
    except Exception as e:
        print(f"An error occurred: {e}")
    finally:
        if 'connection' in locals():
            connection.close()

if __name__ == "__main__":
    main()