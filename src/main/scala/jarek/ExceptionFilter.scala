package jarek

import java.io.{PrintWriter, StringWriter}

import com.twitter.finagle.http.{Request, Response}
import com.twitter.finagle.{Service, SimpleFilter}
import com.twitter.util.Future

class ExceptionFilter extends SimpleFilter[Request, Response] {
  def apply(req: Request, service: Service[Request, Response]) = {
    service.apply(req)
      .handle {
        case e => {
          println("exception caught " + e)
          val sb = new StringBuilder()
          val sw = new StringWriter()
          val pw = new PrintWriter(sw)
          e.printStackTrace(pw)
          pw.close()
          sb.append("Exception during processing: " + e.getClass + e.getMessage)
          sb.append("\n")
          sb.append(sw.getBuffer.toString)
          MyServer.textResponse(sb.toString)
        }
      }
  }
}
