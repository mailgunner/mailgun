package util

import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

object Implicits {

  /*
   * Convert Try's to Future's
   */
  implicit class tryToFuture[A](t: Try[A]) {
    def toFuture: Future[A] = t match {
      case Success(v) => Future.successful(v)
      case Failure(e) => Future.failed(e)
    }
  }
}
