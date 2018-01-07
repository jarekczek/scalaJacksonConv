package jarek

import java.util.concurrent.BlockingQueue

import com.twitter.finagle.{Service, SimpleFilter}
import com.twitter.finagle.http.{Request, Response}
import com.twitter.util.Future

class MaintenanceFilter(
                         commandQueue: BlockingQueue[String]
                       )
  extends SimpleFilter[Request, Response] {
  def apply(req: Request, service: Service[Request, Response]) = {
    if (req.path.equals("/stop")) {
      val resp = Response();
      resp.contentString_=("stop");
      resp.contentType_=("text/plain")
      commandQueue.add("stop")
      Future.value(resp)
    } else if (req.path.equals("/reload")) {
      val resp = Response();
      resp.contentString_=("reload");
      resp.contentType_=("text/plain")
      commandQueue.add("reload")
      Future.value(resp)
    } else {
      println("MaintenanceFilter: doing apply")
      println("dummy line")
      service.apply(req)
    }
  }
}
