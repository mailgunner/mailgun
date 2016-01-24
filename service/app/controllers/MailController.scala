package controllers

import errors.Errors.InvalidJsonError
import models.SendEmailRequest
import play.api.libs.json.Json
import play.api.mvc._
import util.Implicits._
import mailgun.MailGunClient
import scala.concurrent.ExecutionContext.Implicits.global

trait MailControllerEndpoints extends Controller with ControllerHelper {

  val apiKey = "some key"
  val domain = "some domain"

  def sendMail = Action.async(tryJsonParser) { request =>
    val res = for {
      req <- request.body.toFuture
      data <- SendEmailRequest.validate(req.toString()).toFuture
      client = new MailGunClient(apiKey, domain)
      resp <- client.sendEmail(data)
    } yield Ok(resp.json)

    res.recover {
      case e: InvalidJsonError =>
        BadRequest(e.json)
      case e =>
        InternalServerError(Json.parse("{}"))
    }
  }
}

class MailController extends MailControllerEndpoints