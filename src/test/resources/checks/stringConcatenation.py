text = "hello"
following_words = ["world", "I", "am", "a", "string", "concatenation"]

for word in following_words:
    text += word  # Noncompliant {{Concatenation of strings should be done using f-strings or str.join()}}

text = "init"
text += " add this"  # Noncompliant {{Concatenation of strings should be done using f-strings or str.join()}}

text += [word for word in following_words]  # Noncompliant {{Concatenation of strings should be done using f-strings or str.join()}}

text = 0
text +=1 

text = "start"
result = " ".join([text] + following_words) 


final = f"{text} {' '.join(following_words)}" 


def build_string(base, parts):
    return f"{base} {' '.join(parts)}"  

mylist = []
mylist += [1, 2, 3]  # Compliant


count = 0
count += 1  # Compliant


msg = "start"
if True:
    msg += " continued"  # Noncompliant {{Concatenation of strings should be done using f-strings or str.join()}}


def get_text():
    return "function text"

text += get_text()  # Noncompliant {{Concatenation of strings should be done using f-strings or str.join()}}
