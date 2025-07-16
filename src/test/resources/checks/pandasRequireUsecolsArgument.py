import pandas as pd

df1 = pd.read_csv('data.csv')  # Noncompliant {{Specify 'usecols' or 'columns' when reading a DataFrame using Pandas to load only necessary columns}}
df2 = pd.read_parquet('data.parquet')  # Noncompliant {{Specify 'usecols' or 'columns' when reading a DataFrame using Pandas to load only necessary columns}}
df3 = pd.read_excel('data.xlsx')  # Noncompliant {{Specify 'usecols' or 'columns' when reading a DataFrame using Pandas to load only necessary columns}}
df4 = pd.read_json('data.json')  # Noncompliant {{Specify 'usecols' or 'columns' when reading a DataFrame using Pandas to load only necessary columns}}
df5 = pd.read_feather('data.feather')  # Noncompliant {{Specify 'usecols' or 'columns' when reading a DataFrame using Pandas to load only necessary columns}}

df7 = pd.read_csv('data.csv', usecols=['col1', 'col2'])
df8 = pd.read_parquet('data.parquet', columns=['col1', 'col2'])
df9 = pd.read_excel('data.xlsx', usecols=[0, 1, 2])
df10 = pd.read_json('data.json', columns=['col1', 'col2'])
df11 = pd.read_feather('data.feather', columns=['col1', 'col2'])

import pandas as pandas_alias
df14 = pandas_alias.read_csv('data.csv')  # Noncompliant {{Specify 'usecols' or 'columns' when reading a DataFrame using Pandas to load only necessary columns}}
df15 = pandas_alias.read_csv('data.csv', usecols=['col1'])

df16 = pd.read_csv('data.csv', sep=',', header=0)  # Noncompliant {{Specify 'usecols' or 'columns' when reading a DataFrame using Pandas to load only necessary columns}}
df17 = pd.read_csv('data.csv', sep=',', header=0, usecols=['col1', 'col2'])

cols_to_use = ['col1', 'col2', 'col3']
df18 = pd.read_parquet('data.parquet', columns=cols_to_use)