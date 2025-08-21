import pandas as pd
import pandas as pandas_alias

df = pd.read_csv('data.csv') # Noncompliant {{Use Parquet or Feather format instead of CSV}}

df.to_csv('output.csv') # Noncompliant {{Use Parquet or Feather format instead of CSV}}

df = pd.read_parquet('data.parquet')

path_to_file = 'MNIST.csv' # Noncompliant {{Use Parquet or Feather format instead of CSV}}

df2 = pandas_alias.read_csv('another_data.csv') # Noncompliant {{Use Parquet or Feather format instead of CSV}}

with open('data.csv') as f: # Noncompliant {{Use Parquet or Feather format instead of CSV}}
    df3 = pd.read_csv(f) # Noncompliant {{Use Parquet or Feather format instead of CSV}}

df4 = pd.read_csv(
    'complex_data.csv', # Noncompliant {{Use Parquet or Feather format instead of CSV}}
    sep=',',
    header=0
)

df4.to_csv("output.csv") # Noncompliant {{Use Parquet or Feather format instead of CSV}}

df5 = pd.DataFrame({'col1': [1, 2], 'col2': [3, 4]})

other_path = 'data.json'

df6 = pd.read_json(other_path)

df7 = pd.read_feather('features.feather')

df8 = pd.read_parquet("file.parquet")

df9 = pandas_alias.read_feather("something.feather")

df10 = pandas_alias.read_parquet("nested/dir/file.parquet")

result = pd.read_csv("log.csv", encoding='utf-8') # Noncompliant {{Use Parquet or Feather format instead of CSV}}

df11 = pd.DataFrame({'a': [1, 2], 'b': [3, 4]})
df11.to_parquet("output.parquet")

df12 = pd.DataFrame({'x': [5, 6]})
df12.to_feather("output.feather")

filename = "report.csv" # Noncompliant {{Use Parquet or Feather format instead of CSV}}
data = pd.read_csv(filename) # Noncompliant {{Use Parquet or Feather format instead of CSV}}

log_file = "logfile.log"
df13 = pd.read_table(log_file, delimiter='|')
