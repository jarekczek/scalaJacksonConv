package jarek

import java.util

class NestedMapTraversable(m: util.Map[String, Any])
extends Traversable[Tuple2[String, Any]]
{
  def traverse[U](k0: String, v0: Any, f: Function2[String, Any, U]): U = {
    v0 match {
      case m: util.Map[String, Any] => {
        f("start " + k0, m)
        m.forEach ( (k, v) => traverse(k, v, f) )
        f("end " + k0, m)
      }
      case v: Any => { f(k0, v) }
    }
  }

  override def foreach[U](f: Tuple2[String, Any] => U): Unit = {
    traverse(m.getClass.getSimpleName, m,
      (k, v) => f(Tuple2(k, v))
    )
  }
}
