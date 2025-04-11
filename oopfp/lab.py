# f(a) -> a + b env: [[b: 4], [+: fn]]
# f(5)
# (a) (a + b) env

def account(balance, action):
  def deposit(amount):
    return balance + amount

  def withdraw(amount):
    return balance - amount

  if(action == 'deposit'):
    return deposit
  elif (action == 'withdraw'):
    return withdraw
  else:
    print("wat?")




print(account(50, "deposit")(10))