package controllers

import errors.Errors.InvalidJsonError
import play.api.libs.json.{JsString, Json, JsValue}
import play.api.mvc.BodyParser
import play.api.mvc.BodyParsers._
import services.ConfigComponent

import scala.util.{Failure, Try}
import scala.concurrent.ExecutionContext.Implicits.global

trait BaseController extends ConfigComponent {

  // Get all our config values or exit.
  lazy val apiKey = config.getString("MAILGUN_API_KEY").getOrElse(sys.error("mailgun.apiKey must be set"))
  lazy val domain = config.getString("MAILGUN_DOMAIN").getOrElse(sys.error("mailgun.domain must be set"))

  // We want to parse the incoming body as text first, then do JSON validation.
  val tryJsonParser: BodyParser[Try[JsValue]] =
    parse.tolerantText.map(text => Try(Json.parse(text)).recoverWith { case e => Failure(InvalidJsonError()) })

  def getJsonError(e: Throwable): JsValue = {
    // If there isn't a message return nothing
    if (e.getMessage == null)
      Json.obj()
    else
      Json.obj("errors" -> JsString(e.getMessage))
  }
}
