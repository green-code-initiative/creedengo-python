from datetime import date

class Something:
    def __init__(self, value):
        self.value = value

class Client():

    def __init__(self, age, weight):
        self.age = age
        self.weight = weight

    def get_age_in_five_years(self):
        a = Client()
        return a.age

    def is_major(self):
        return self.age >= 18

client = Client(25)
client.age
client.age = 25
client.weight
client.weight(5)