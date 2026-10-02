from sklearn.ensemble import RandomForestClassifier  # Noncompliant {{Prefer XGBoost over RandomForest for better energy efficiency}}
from sklearn.ensemble import RandomForestRegressor  # Noncompliant {{Prefer XGBoost over RandomForest for better energy efficiency}}

model = RandomForestClassifier(n_estimators=100)  # Noncompliant {{Prefer XGBoost over RandomForest for better energy efficiency}}

model2 = RandomForestRegressor(n_estimators=50)  # Noncompliant {{Prefer XGBoost over RandomForest for better energy efficiency}}

model3 = sklearn.ensemble.RandomForestClassifier(n_estimators=100)  # Noncompliant {{Prefer XGBoost over RandomForest for better energy efficiency}}
