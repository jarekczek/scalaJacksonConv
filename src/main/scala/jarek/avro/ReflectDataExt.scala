package jarek.avro

import java.util

import org.apache.avro.Schema
import org.apache.avro.reflect.ReflectData

import scala.collection.JavaConverters

object ReflectDataExt extends ReflectData {
  type Mapa = util.Map[String, Any]

  def createField(name: String, schema: Schema) = new Schema.Field(
    name, schema, null, null.asInstanceOf[Any]
  )

  def createRecord(name: String, fields: List[Schema.Field]) =
    Schema.createRecord(name, null, null, false, JavaConverters.seqAsJavaList(fields))

  def createSchemaForMap(name: String, m: Mapa) = {
    val fields = JavaConverters.mapAsScalaMap(m).foldRight(List[Schema.Field]())(
      (t, fields) => createField(t._1, getSchemaForObject(t._2, Some(t._1))) :: fields
    )
    createRecord(name, fields)
  }

  def createSchemaForCollection(name: String, javaCollection: util.Collection[_]) = {
    val iter = JavaConverters.collectionAsScalaIterable(javaCollection)
    if (iter.isEmpty)
      throw new UnsupportedOperationException("Collection with no elements")
    val elemSchema = getSchemaForObject(iter.head)
    Schema.createArray(elemSchema)
  }

  def getSchemaForObject(o: Any, nameGiven: Option[String] = Option.empty): Schema = {
    val name = nameGiven.orElse(Some(o.getClass.getName + o.hashCode)).get
    val schema = o match {
      case m: Mapa => createSchemaForMap(name, m)
      case a: util.Collection[_] => createSchemaForCollection(name, a)
      case _ => super.getSchema(o.getClass)
    }
    //println("ReflectDataExt schema: " + schema.toString(true))
    schema
  }

}
