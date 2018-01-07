package jarek

import java.util

import org.scalatest.{FlatSpec, Matchers}

class NestedMapTraversableTest extends FlatSpec with Matchers {
  "nested map" should "work" in {
    val m = NestedMapHelper.createNestedMap
    new NestedMapTraversable(m).foreach(println(_))
  }
}
