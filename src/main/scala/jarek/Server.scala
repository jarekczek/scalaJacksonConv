package jarek

import java.util.concurrent.{BlockingQueue, CountDownLatch, LinkedBlockingQueue}
import java.util.concurrent.atomic.AtomicReference
import java.util.function.BinaryOperator

import com.twitter.finagle.{Http, ListeningServer, Service, SimpleFilter}
import com.twitter.finagle.http.{Request, Response}
import com.twitter.util.{Await, Duration, Future}
//import org.jboss.netty.handler.codec.http.{DefaultHttpResponse, HttpVersion, HttpResponseStatus, HttpRequest, HttpResponse}
import java.net.{SocketAddress, InetSocketAddress}
import com.twitter.finagle.builder.{Server, ServerBuilder}

object MyServer {

  val serverRef = new AtomicReference[ListeningServer]()
  val latch = new CountDownLatch(1)
  val commandQueue = new LinkedBlockingQueue[String]()

  def reloadServer(): Unit = {

    val staticFilter = new SimpleFilter[Request, Response] {
      def apply(request: Request, service: Service[Request, Response]) = {
        if (request.path.endsWith(".html"))
          new StaticService().apply(request)
        else
          service.apply(request)
      }
    }

    val completeService =
        new LoggingFilter()
        .andThen(new ExceptionFilter())
        .andThen(staticFilter)
        .andThen(new MaintenanceFilter(commandQueue))
        .andThen(new RootService())

    val oldServer = serverRef.getAndSet(null)
    if (oldServer != null) {
      println("stopping old server")
      Await.ready(oldServer.close())
    }
    println("starting a new server")
    val newServer = Http.server.serve(":8123", completeService)
    if (!serverRef.weakCompareAndSet(null, newServer))
      newServer.close()
  }

  def main(args: Array[String]) {
    println("ok s")

    /*
    val server = ServerBuilder()
      .stack(Http.server
        .withLabel("jareksl1")
      )
  //    .codec(Http())
      .bindTo(new InetSocketAddress(8123))
      .name("jareks1")
      .hostConnectionMaxLifeTime(Duration.parse("5.minutes"))
      .readTimeout(Duration.parse("2.minutes"))
      .build(rootService)
      */

    reloadServer()
    println("klasa servera: " + serverRef.get.getClass)
    var finish = false
    while (!finish) {
      val command = commandQueue.take()
      commandQueue.synchronized {
        command match {
          case "stop" => finish = true
          case "reload" => reloadServer()
        }
      }
    }
    println("closing server")
    Await.ready(serverRef.get.close())
    println("done")
  }

  def textResponse(contents: String): Response = {
    val resp = Response()
    resp.contentType_=("text/plain")
    resp.contentString_=(contents)
    resp
  }
}
