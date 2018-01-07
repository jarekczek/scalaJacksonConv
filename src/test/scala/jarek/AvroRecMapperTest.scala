package jarek

import java.util

import com.fasterxml.jackson.databind.ObjectMapper
import jarek.avro.AvroSchemaGenerator
import org.scalatest.{FlatSpec, Matchers}

class AvroRecMapperTest  extends FlatSpec with Matchers {

  def processObject(o: Any) = {
    val mapper = new AvroRecMapper()
    val bytes = mapper.writeValueAsBytes(o)
    println("bytes: " + new String(bytes))
  }

  "simple object" should "be mapped" in {
    val o = new util.HashMap[String, Any]
    o.put("asdf", 3)
    val mapper = new AvroRecMapper()
    val bytes = mapper.writeValueAsBytes(o)
    println("bytes: " + new String(bytes))
  }

  "nested map" should "work" in {
    val o = NestedMapHelper.createNestedMap
    val mapper = new AvroRecMapper()
    val bytes = mapper.writeValueAsBytes(o)
    println("bytes: " + new String(bytes))
  }

  "avro mapper" should "handle arrays" in {
    val jsonStr =
      """{"b":"werwe","c":"asdf",
        |  "r":{"e":3,"f":"5"},
        |  "g":[{"h": "h1"}, {"h": "h2"}]
        |}""".stripMargin
    val o = new ObjectMapper().readValue(jsonStr, classOf[Any])
    val schema = AvroSchemaGenerator.createSchema(o)
    println("schema: " + schema.toString(true))
    processObject(o)
  }

  "fields after nested record" should "pass" in {
    val jsonStr =
      """{
        |"b":"werwe",
        |"r":{"e":3},
        |"g":5
        |}""".stripMargin
    val o = new ObjectMapper().readValue(jsonStr, classOf[Any])
    val schema = AvroSchemaGenerator.createSchema(o)
    println("schema: " + schema.toString(true))
    processObject(o)
  }
}
