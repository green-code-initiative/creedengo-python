from xgboost import XGBClassifier
from xgboost import XGBRegressor
from sklearn.ensemble import GradientBoostingClassifier


# Compliant: using XGBoost classifier
model = XGBClassifier(eval_metric="logloss")
model.fit(X_train, y_train)

# Compliant: using XGBoost regressor
model2 = XGBRegressor(n_estimators=100)
model2.fit(X_train, y_train)

# Compliant: using other sklearn classifiers
model3 = GradientBoostingClassifier(n_estimators=100)
model3.fit(X_train, y_train)
