package jarek

import java.io._

import com.twitter.finagle.Service
import com.twitter.finagle.http.{Request, Response}
import com.twitter.util.Future

import scala.collection.mutable.ArrayBuffer

class StaticService extends Service[Request, Response] {

  private def getFileBytes(f: File): Array[Byte] = {
    val istr = new FileInputStream(f)
    val abuf = new ArrayBuffer[Byte]()
    val BUF_SIZE = 1000
    val singleBuf = new Array[Byte](1000)

    try {
      var bytesRead: Int = 0
      while (bytesRead >= 0) {
        bytesRead = istr.read(singleBuf)
        abuf.appendAll(singleBuf.take(bytesRead))
      }
      return abuf.toArray
    } finally {
      istr.close()
    }
  }

  def apply(req: Request) = {
    val resp = Response();
    println("static service called for " + req.path)
    val file = new File("html", req.path)
    println("reading file " + file.getAbsolutePath)
    println("file length: " + getFileBytes(file).length)
    resp.write(getFileBytes(file))
    Future.value(resp)
  }
}
