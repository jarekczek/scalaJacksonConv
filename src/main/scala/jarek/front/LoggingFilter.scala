package jarek.front

import com.twitter.finagle.http.{Request, Response}
import com.twitter.finagle.{Service, SimpleFilter}
import com.twitter.util.Future

class LoggingFilter extends SimpleFilter[Request, Response] {
  def apply(req: Request, service: Service[Request, Response]) = {
    println("now logging " + req.path)
    val response: Future[Response] = service.apply(req)
    println("service applied")
    response
  }
}
