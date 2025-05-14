import pandas as pd
import pandas as pandas_alias

df = pd.read_csv('data.csv') # Noncompliant {{Use Parquet or Feather format instead of CSV}}


df.to_csv('output.csv') # Noncompliant {{Use Parquet or Feather format instead of CSV}}


df = pd.read_parquet('data.parquet')


path_to_file = 'MNIST.csv' # Noncompliant {{Use Parquet or Feather format instead of CSV}}


df2 = pandas_alias.read_csv('another_data.csv') # Noncompliant {{Use Parquet or Feather format instead of CSV}}


with open('data.csv') as f:# Noncompliant {{Use Parquet or Feather format instead of CSV}}
    df3 = pd.read_csv(f) # Noncompliant {{Use Parquet or Feather format instead of CSV}}

df4 = pd.read_csv(# Noncompliant {{Use Parquet or Feather format instead of CSV}}
    'complex_data.csv', # Noncompliant {{Use Parquet or Feather format instead of CSV}}

    sep=',',
    header=0
) 

df4.to_csv("output.csv") # Noncompliant {{Use Parquet or Feather format instead of CSV}}

df5 = pd.DataFrame({'col1': [1, 2], 'col2': [3, 4]})


other_path = 'data.json'