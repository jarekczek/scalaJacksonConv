package jarek

class Input(var data: Array[Byte], var inFormat: String, var outFormat: String)
{
  override def toString(): String = {
    "data: " + new String(data) + ", " +
      "outFormat: " + outFormat
  }
  
}
