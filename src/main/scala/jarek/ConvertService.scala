package jarek

import java.io._

import com.twitter.finagle.Service
import com.twitter.finagle.http.{Request, Response}
import com.twitter.util.Future

import scala.collection.mutable.ArrayBuffer

class ConvertService extends Service[Request, Response] {
  def apply(req: Request) = {
    val resp = Response()
    val converterInput = new Input(
      req.getParam("input").getBytes("utf-8"),
      req.getParam("inputFormat"),
      req.getParam("outputFormat")
      )
    val output: Array[Byte] = Conv.convert(converterInput)
    resp.write(output)
    req.getParam("outputFormat").toLowerCase match {
      case "json" => resp.contentType_=("application/json")
      case "xml" => resp.contentType_=("application/xml")
      case _ => resp.contentType_=("text/plain")
    }
    Future.value(resp)
  }
}
