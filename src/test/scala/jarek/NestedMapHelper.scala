package jarek

import java.util

object NestedMapHelper {
  def createNestedMap = {
    val m = new util.LinkedHashMap[String, Any]
    m.put("a", 1)
    m.put("b", 2)
    val m2 = new util.LinkedHashMap[String, Any]()
    m2.put("c", "weee")
    m.put("d", m2)
    val m3 = new util.LinkedHashMap[String, Any]()
    m3.put("e", "erere")
    m2.put("f", m3)
    m
  }
}
