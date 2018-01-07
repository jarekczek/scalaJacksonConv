package jarek

import java.util.concurrent.CountDownLatch

import com.fasterxml.jackson.databind.ObjectMapper
import com.twitter.finagle.Service
import com.twitter.finagle.http.{Request, Response}
import com.twitter.io.Buf
import com.twitter.util.Future

class RootService() extends Service[Request, Response]
{
  // Define our service: OK response for root, 404 for other paths

  def apply(req: Request) = {
    println("request: " + req.path)
    if (req.path.startsWith("/convert"))
      new ConvertService().apply(req)
    else {
      val r = req.path match {
        //case "/" => new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK)
        //case _ => new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND)
        case "/convformats" => {
          val resp = Response()
          resp.contentType_=("application/json")
          val mapper = new ObjectMapper()
          resp.write(mapper.writeValueAsBytes(Conv.getFormats.toArray))
          resp
        }
        case _ => {
          println("no specific match")
          val resp = Response(); resp.contentString_=("spox");
          resp.contentType_=("text/plain")
          resp
        }
      }
      Future.value(r)
    }
  }


}
