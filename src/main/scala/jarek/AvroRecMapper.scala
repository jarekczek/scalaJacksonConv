package jarek

import java.io.ByteArrayOutputStream
import java.util

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.avro.{Conversion, Schema, SchemaBuilder}
import org.apache.avro.generic.GenericDatumWriter
import org.apache.avro.io.EncoderFactory
import org.apache.avro.reflect.{ReflectData, ReflectDatumWriter}

import scala.collection.JavaConverters

class AvroRecMapper extends ObjectMapper
{
  override def readValue[T](src: Array[Byte], valueType: Class[T]): T = {
    println("is reading type " + valueType)
    valueType.newInstance()
  }

  override def writeValueAsBytes(value: Any): Array[Byte] = {
    println("is writing type " + value.getClass)
    val data = ReflectData.get
    val schema = AvroSchemaGenerator.createSchema(value.asInstanceOf[util.Map[String, Any]])
    println("schema: " + schema.toString(true))
    val ostr = new ByteArrayOutputStream()
    val enc = EncoderFactory.get().binaryEncoder(ostr, null)
    val w = data.createDatumWriter(schema).asInstanceOf[ReflectDatumWriter[Any]]
    //w.write(value, enc)
    enc.flush()
    //ostr.toByteArray
    schema.toString(true).getBytes("utf-8")
  }
}

class MapConversion(v: util.LinkedHashMap[String, Any])
  extends Conversion[util.LinkedHashMap[String, Any]]
{
  override def getRecommendedSchema: Schema = {
    v match {
      case m: util.LinkedHashMap[_, _] => {
        println ("mpa size " + m.size())
        var builder = SchemaBuilder.builder().record(v.getClass.getSimpleName)
          .fields()
        val scalaMap = JavaConverters.mapAsScalaMap(m)
        builder = scalaMap.foldLeft(builder)(
          (builder, t) => builder
            .name(t._1)
            .`type`(ReflectData.get.getSchema(t._2.getClass))
            .noDefault()
        )
        builder.endRecord()
      }
      case _ => { throw new UnsupportedOperationException () }
    }
  }

  override def getLogicalTypeName: String = "LinkedHashMapAsAvro"

  override def getConvertedType = classOf[util.LinkedHashMap[String, Any]]
}
