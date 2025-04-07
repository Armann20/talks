import Prefix.*

extension (inline ctx: StringContext) inline def prefix(): Int = Prefix.prefix(ctx)

@main def run(): Unit =
  println(prefix"+ 2 5 9")
  println(prefix"* 3 4 2")

  println(prefix"+ 5 5" + prefix"- 10 5")

// sql macro example in dooby and JDBC

// def updatePerson(name: String, age: Int): Boolean =
//   ...
//   sql"update person set age = $age where name = $name"
//     .unsafeRunSync()
//   ...

// def updatePerson(name: String, age: Int): Boolean =
//   ...
//   val stmt = conn.prepareStatement("update person set age = ? where name = ?")
//   stmt.setInt(1, age)
//   stmt.setString(2, name)
//   stmt.executeUpdate()
//   stmt.close()
//   ...
