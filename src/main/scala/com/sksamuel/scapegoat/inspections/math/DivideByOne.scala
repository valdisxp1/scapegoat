package com.sksamuel.scapegoat.inspections.math

import com.sksamuel.scapegoat.{Levels, Feedback, Inspection}

import scala.tools.nsc.Global

/** @author Stephen Samuel */
class DivideByOne extends Inspection {

  override def traverser(global: Global, feedback: Feedback): global.Traverser = new global.Traverser {

    import global._

    private def isNumber(tree: Tree) = {
      tree.tpe <:< typeOf[Int] ||
        tree.tpe <:< typeOf[Long] ||
        tree.tpe <:< typeOf[Double] ||
        tree.tpe <:< typeOf[Float]
    }

    private def isOne(value: Any): Boolean = value match {
      case i: Int => i == 1
      case _ => false
    }

    override def traverse(tree: Tree): Unit = {
      tree match {
        case Apply(Select(lhs, TermName("$div")), List(Literal(Constant(x)))) if isNumber(lhs) && isOne(x) =>
          feedback.warn("Divide by one", tree.pos, Levels.Warning, "Divide by one will always return the original value")
        case _ => super.traverse(tree)
      }
    }
  }

}
