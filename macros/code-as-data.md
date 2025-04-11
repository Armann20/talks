# Code as data

Our systems often deal with ideas from different domains, sometimes they need to speak the language of a peer system, e.g. a database, sometimes both. In such events the closer our language to express that part of the system to the source the better.

Different languages approach this issue in different ways.One way of doing this is going full native with domain specific languages, like SQL or ???.

```sql
SELECT name, position
FROM employees
WHERE active = true
```

```regex
^\(\d{3}\)\s\d{3}-\d{4}$ ............. (matches phone numbers like (123) 456-7890)
```

```lilypond
% The Nokia tune from Gran Vals by Tarrega

\relative c'' {
  \key a \major
  \time 3/4

  e8 d | fis4 gis | cis,8 b d4 e | b8 a cis4 e | a,2. |
}
```

Other ways to bridge the gap are by baking in a specific domain like Scala did with XML, or allow for certain *natural* properties of the test via syntax sugar, like the builder pattern in Scala.

```scala
// XML literals

import scala.xml._

...
val company = "Acme Inc."

val employee =
  <employee company={company}>
    <name>Jane Smith</name>
    <position>Developer</position>
    <active>{true}</active>
  </employee>
...
```

```scala
// The builder pattern

import darling.{..., Mail, send}

def main(args: Array[String]) {
  ...
  send a new Mail
    from "leia@gmail.com"
    to "obi_wan@wherever.io"
    withSubject "Help"
    andMessage "Help me, Obi-Wan Kenobi. You're my only hope."
    darling
  ...
}
```

A different approach is to go for a *general* language with syntactic extension capabilities. The latter can be a powerful tool and is achieved with what's commonly known as macros - actions that are taken at compile time and can be used to manipulate code.

```scala
// SQL : UPDATE person SET age = 30 WHERE name = 'John Doe'

// SQL interaction in dooby
def updatePerson(name: String, age: Int): Boolean =
  ...
  sql"UPDATE person SET age = $age WHERE name = $name"
    .unsafeRunSync()
  ...

// ... and plain JDBC
def updatePerson(name: String, age: Int): Boolean =
  ...
  val stmt = conn.prepareStatement("update person set age = ? where name = ?")
  stmt.setInt(1, age)
  stmt.setString(2, name)
  stmt.executeUpdate()
  stmt.close()
  ...
```

Different languages provide different lingo to do such things making the feature more or less wieldy. Let's look at how this is achieved in Scala and Clojure.

Scala code looks like a text, it's readable ok, but it does not quite resemble a data structure. In fact, the only data structure it resembles is a string of characters, and that is not the most convenient representation to do do operations with. In order to evaluate and\or manipulate it Scala turns it into a tree, where each token in the string is marked and placed appropriately. This is done by the compiler and not exposed to the user. The tree is then traversed and evaluated. This means that what we will be manipulating is not the original string, but a tree representation of it.

Let's see how we can use macros to introduce simple prefix expressions in our Scala code.

```scala
// Macros in Scala

object Prefix:

  inline def prefix(inline ctx: StringContext): Int =
    ${ prefixImpl('ctx) }

  private def calculate(op: String, args: List[Int]): Int =
    op match
      case "+" => args.sum
      case "*" => args.product
      case "-" => args.reduce(_ - _)
      case "/" => args.reduce(_ / _)
      case _   =>
        throw new IllegalArgumentException(s"Unknown operator: $op")

  private def prefixImpl(ctxExpr: Expr[StringContext])(using Quotes): Expr[Int] =
    import quotes.reflect.*

    ctxExpr match
      case '{ StringContext(${ Expr(parts) }*) } =>
        val tokens = parts.head.split("\\s+").toList
        tokens match
          case op :: args =>
            val ints   = args.map(_.toInt)
            val result = calculate(op, ints)
            Expr(result)
          case Nil        =>
            report.error("Empty expression")
            Expr(0)
      case _                                     =>
        report.error("Expected a literal string")
        Expr(0)

...

  println(prefix"+ 2 5 9")
```

Now, let's look at the same example in Clojure. Clojure is a Lisp dialect, which means that it treats code as data. This means that the code itself is a data structure, and we can manipulate it directly.


```clojure
;; Macros in Clojure

(defn process-tokens [tokens]
  (let [first-num (first tokens)
        op (second tokens)
        second-num (nth tokens 2)
        remaining (drop 3 tokens)]
    (if (empty? remaining)
      (list op first-num second-num)
      (list op first-num
            (process-tokens (cons second-num remaining))))))

(defmacro infix
  "Interprets infix expressions with multiple operands"
  [infixed-expression]
  (process-tokens (seq infixed-expression)))

(println (infix (1 + 1)))
(println (infix ((infix (1 + 2)) * 4 / 5)))
```


Look at the uniformity of the code, the words used in both cases. This is because LISPs treat code as data. The interpreter itself operates on a list.