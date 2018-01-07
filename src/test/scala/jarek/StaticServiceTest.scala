package jarek

import com.twitter.finagle.http.{Request, Response}
import com.twitter.util.{Await, Future}
import org.mockito.Mockito
import org.scalatest.{FlatSpec, Matchers}
;

class StaticServiceTest extends FlatSpec with Matchers {
  "Static service" should "be able to read a file" in {
    val req = Mockito.mock(classOf[Request])
    Mockito.when(req.path).thenReturn("testFile.html")
    val resp: Response = Await.result(new StaticService().apply(req))
    println("got resp, len " + resp.content.length)
    println("got resp " + resp.contentString)

    resp.content.length should be (2)

    println("ok")
  }
}
