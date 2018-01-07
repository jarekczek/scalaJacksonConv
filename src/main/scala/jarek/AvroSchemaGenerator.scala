package jarek

import java.util
import java.util.Collections

import org.apache.avro.reflect.ReflectData
import org.apache.avro.{Schema, SchemaBuilder}

import scala.collection.JavaConverters

object AvroSchemaGenerator {

  private val reflect = ReflectData.get

  private def cloneField(f: Schema.Field)  =
    new Schema.Field(f.name, f.schema, f.doc, f.defaultVal, f.order)

  def createField(name: String, schema: Schema) = new Schema.Field(
    name, schema, null, null.asInstanceOf[Any]
  )

  def dummyEnumSchema = Schema.createEnum("dummyEnum", null, null, new util.ArrayList[String]())

  def emptyArraySchema: Schema =
    Schema.createArray(dummyEnumSchema)

  def isEmptyArraySchema(schema: Schema) =
    schema.getType == Schema.Type.ARRAY && schema.getElementType.equals(dummyEnumSchema)

  def createSchema(o: Any, nameGiven: Option[String] = Option.empty): Schema = {
    val name = nameGiven.orElse(Some(o.getClass.getSimpleName)).get
    o match {
      case m: util.Map[String, Any] => createSchemaForMap(m, nameGiven)
      case a: util.List[_] => createSchemaForArray(name, a)
      case o => reflect.getSchema(o.getClass)
    }
  }

  def createSchemaForMap(m: util.Map[String, Any], nameGiven: Option[String]): Schema = {
    val name = nameGiven.orElse(Some(m.getClass.getSimpleName)).get
    val fields = new NestedMapTraversable(m).foldRight(List[Schema.Field]())(
      (tuple, oldFields) => {
        val nullObject: Object = null
        tuple match {
          case (name, map: util.Map[_, _]) => {
            if (name.startsWith("start ")) {
              val innerRecord = Schema.createRecord(JavaConverters.seqAsJavaList(oldFields))
              List(createField(name.drop(6), innerRecord))
            } else {
              // End of schema may be ignored - passing the same back.
              oldFields
            }
          }
          case (name, value) => {
            createField(name, createSchema(value, Some(name))) :: oldFields
          }
        }
      }
    )
    assert(fields.size == 1)
    fields(0).schema()
  }

  def createSchemaForArray(name:String, javaList: util.List[_]): Schema = {
    val elems = JavaConverters.asScalaBuffer(javaList).toList
    if (elems.isEmpty)
      throw new UnsupportedOperationException("Array with no elements")
    val elemSchema = createSchema(elems.head)
    Schema.createArray(elemSchema)
  }
}
