package jarek.avro

import java.io.ByteArrayOutputStream
import java.util

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.avro.generic.{GenericData, GenericDatumWriter}
import org.apache.avro.io.EncoderFactory
import org.apache.avro.reflect.ReflectData
import org.apache.avro.{Conversion, Schema, SchemaBuilder}

import scala.collection.JavaConverters

class AvroRecMapper extends ObjectMapper
{
  private val reflect = ReflectData.get
  type Mapa = util.Map[String, Any]

  override def readValue[T](src: Array[Byte], valueType: Class[T]): T = {
    println("is reading type " + valueType)
    valueType.newInstance()
  }

  def buildRecordForObject(value: Any): GenericData.Record = {
    val schema = ReflectDataExt.getSchemaForObject(value)
    buildRecordForObjectRecur(value, schema)
  }

  def buildRecordForObjectRecur(value: Any, schema: Schema): GenericData.Record = {
    println("buildRecordForObjectRec starts for " + schema.getName)
    if (!value.isInstanceOf[Mapa])
      throw new UnsupportedOperationException("Can't build a record from " + value.getClass)
    val rec = new GenericData.Record(schema)
    val map = value.asInstanceOf[Mapa]
    map.forEach( (k, v) => {
      println("building for " + (k,v) + " in record " + schema.getName + ", type: " + v.getClass)
      v match {
        case v: Mapa => rec.put(k, buildRecordForObjectRecur(v, schema.getField(k).schema))
        case a: util.List[Any] => {
          println("got array")
          val arraySchema = schema.getField(k).schema
          val elemSchema = arraySchema.getElementType
          val avroArray = arraySchema.getElementType.getType match {
            case Schema.Type.RECORD => {
              val arrayElems = JavaConverters.asScalaBuffer(a).toList
              val recordElems = arrayElems.map { buildRecordForObjectRecur(_, elemSchema) }
              assert(arrayElems.forall(_.isInstanceOf[Mapa]))
              new GenericData.Array[GenericData.Record](
                schema.getField(k).schema,
                JavaConverters.seqAsJavaList(recordElems)
              )
            }
            case _ => {
              // We don't expect to ever get here. All arrays will probably be made of records.
              throw new UnsupportedOperationException();
              // But this should work:
              //new GenericData.Array[Any](schema.getField(k).schema, a)
            }
          }
          rec.put(k, avroArray)
        }
        case _ => rec.put(k, v)
      }
      println("value for " + k + " set to " + rec.get(k))
    })
    println("buildRecordForObjectRec ends for " + schema.getName)
    rec
  }

  override def writeValueAsBytes(value: Any): Array[Byte] = {
    println("is writing type " + value.getClass)
    val rec = buildRecordForObject(value)
    val schema = rec.getSchema
    val ostr = new ByteArrayOutputStream()
    val enc = EncoderFactory.get().binaryEncoder(ostr, null)
    val w = GenericData.get.createDatumWriter(schema).asInstanceOf[GenericDatumWriter[Any]]
    w.write(rec, enc)
    enc.flush()
    //ostr.toByteArray
    rec.toString.getBytes("utf-8")
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
