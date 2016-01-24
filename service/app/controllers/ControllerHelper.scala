package controllers

import errors.Errors.InvalidJsonError
import play.api.libs.json.{Json, JsValue}
import play.api.mvc.BodyParser
import play.api.mvc.BodyParsers._

import scala.util.{Failure, Try}
import scala.concurrent.ExecutionContext.Implicits.global

trait ControllerHelper {
  val tryJsonParser: BodyParser[Try[JsValue]] =
    parse.tolerantText.map(text => Try(Json.parse(text)).recoverWith { case e => Failure(InvalidJsonError()) })
}
