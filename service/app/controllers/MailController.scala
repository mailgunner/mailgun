package controllers

import errors.Errors.InvalidJsonError
import models.SendEmailRequest
import play.api.mvc._
import services.{DefaultConfigComponent, ConfigComponent}
import util.Implicits._
import mailgun.MailGunClient
import scala.concurrent.ExecutionContext.Implicits.global

trait MailControllerEndpoints extends Controller with ConfigComponent with BaseController {

  /**
   * POST /v1/mail
   *
   * Takes a SendEmailRequest and sends an email.
   */
  def sendMail = Action.async(tryJsonParser) { request =>
    val res = for {
      req <- request.body.toFuture
      data <- SendEmailRequest.validate(req.toString()).toFuture
      templatedData = data.copyTemplateToBody()
      client = new MailGunClient(apiKey, domain)
      resp <- client.sendEmail(templatedData)
    } yield Ok(resp.json)

    res.recover {
      case e: InvalidJsonError =>
        BadRequest(e.json)
      case e =>
        InternalServerError(getJsonError(e))
    }
  }
}

class MailController extends MailControllerEndpoints with DefaultConfigComponent