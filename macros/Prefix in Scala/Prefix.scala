import scala.quoted.*

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
