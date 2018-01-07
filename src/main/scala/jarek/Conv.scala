package jarek

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.dataformat.avro.AvroMapper
import jarek.avro.AvroRecMapper

object Conv
{
  def getHello(x: Int): String =
  {
    "hello with " + x
  }
  
  def getMapper(format: String) = {
    format.toUpperCase() match  {
      case "XML" => new XmlMapper()
      case "JSON" => new ObjectMapper()
      case "AVROREC" => new AvroRecMapper()
      case _ => throw new IllegalArgumentException(format)
    }
  }

  def getFormats(): List[String] = {
    List("XML", "JSON", "AVROREC")
  }

  class Pojo {
    var a: String = ""
  }
  
  def convert(input: Input): Array[Byte] = 
  {
    val inMapper: ObjectMapper = getMapper(input.inFormat)
    val outMapper: ObjectMapper = getMapper(input.outFormat)
    val o = inMapper.readValue(input.data, classOf[Object])
    outMapper.writeValueAsBytes(o)
  }
  
  def main1(args: Array[String])
  {
    println(new String(Conv.convert(new Input(
      "<data><tag>this is the data</tag></data>".getBytes(),
      "xml",
      "json"))))
    println(new String(Conv.convert(new Input(
      """{ "field1": "value1" }""".getBytes(),
      "json",
      "xml"))))
  }
}
