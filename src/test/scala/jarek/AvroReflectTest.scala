package jarek

import java.io.{ByteArrayOutputStream, FileOutputStream}
import java.lang.reflect.ParameterizedType
import java.util

import com.fasterxml.jackson.databind.ObjectMapper
import com.twitter.finagle.http.{Request, Response}
import com.twitter.util.Await
import jarek.avro.AvroSchemaGenerator
import org.apache.avro.{Conversion, Schema, SchemaBuilder}
import org.apache.avro.generic.{GenericData, GenericDatumReader}
import org.apache.avro.io.{DatumReader, DatumWriter, DecoderFactory, EncoderFactory}
import org.apache.avro.reflect.{ReflectData, ReflectDatumWriter}
import org.apache.avro.specific.SpecificData
import org.mockito.Mockito
import org.scalatest.{FlatSpec, Matchers}

import scala.collection.JavaConverters
import scala.reflect.ClassTag

class AvroReflectTest extends FlatSpec with Matchers {
  "Reflect" should "do sth" in {
    val p = new Pojo1("eee", 5)
    val w = new ReflectDatumWriter(classOf[Pojo1])
    val ostr = new ByteArrayOutputStream()
    val enc = EncoderFactory.get().binaryEncoder(ostr, null)
    println(ReflectData.get().getSchema(classOf[Pojo1]))
    w.write(p, enc)
    enc.flush()
    println("p: " + p)
    println("bytes: " + ostr.toString)
    println("json: " + ReflectData.get.toString(p))
    println("json generic: " + GenericData.get.toString(p))
    println("json specific: " + SpecificData.get.toString(p))
    println("record: " + GenericData.get.newRecord(p, ReflectData.get.getSchema(classOf[Pojo1])).getClass)

    println("reading")
    val readSchema = new Schema.Parser().parse("{\"type\":\"record\",\"name\":\"Pojo1\",\"namespace\":\"jarek\",\"fields\":[]}")
    //val reader = SpecificData.get.createDatumReader(readSchema)
    val reader = new GenericDatumReader[GenericData.Record](readSchema)
    val dec = DecoderFactory.get.binaryDecoder(ostr.toByteArray, null)
    val rec = reader.read(null, dec)
    println("read data: " + GenericData.get.toString(rec))

    println("ok")
  }

  "plaing map" should "be converted to avro" in {
    println("hi map")
    val map = new util.LinkedHashMap[String, Any]()
    map.put("entry1", "v1")
    map.put("entry2", 3)

    val schema = AvroSchemaGenerator.createSchema(map)
    println("schema: " + schema.toString(true))
  }

  "nested map" should "be converted to avro" in {
    val schema = AvroSchemaGenerator.createSchema(NestedMapHelper.createNestedMap)
    println("schema: " + schema.toString(true))
  }

  "avro" should "handle arrays" in {
    val jsonStr =
      """{"b":"werwe",
        | "c":"asdf",
        | "r":{"e":3,"f":"5"},
        | "g":[{"h": "h"}, {"i": "i"}]
        |}""".stripMargin
    val o = new ObjectMapper().readValue(jsonStr, classOf[Any])
    val schema = AvroSchemaGenerator.createSchema(o)
    println("schema with array: " + schema.toString(true))
    schema.getField("b") should not be (null)
    schema.getField("g") should not be (null)
    schema.getField("r").schema.getField("g") should be (null)
  }

  "avro record" should "be generated" in {
    val mapper = new AvroRecMapper()
    val o = NestedMapHelper.createNestedMap
    val schema = AvroSchemaGenerator.createSchema(o)
    println("schema: " + schema.toString(true))
    val rec = new GenericData.Record(schema)
    rec.put("a", 3)
    rec.put("b", 4)
    val recD = new GenericData.Record(schema.getField("d").schema)
    recD.put("c", "aaa")
    rec.put("d", recD)
    val recF = new GenericData.Record(recD.getSchema.getField("f").schema)
    recF.put("e", "erter")
    recD.put("f", recF)

    println("record: " + rec.toString)
    val wr = GenericData.get.createDatumWriter(schema).asInstanceOf[DatumWriter[GenericData.Record]]
    val enc = EncoderFactory.get.binaryEncoder(System.out, null)
    wr.write(rec, enc)
    enc.flush()
    println("record written")
  }
}

class Pojo1(a: String, var b: Int) {
  var c: String = "rr"
}
