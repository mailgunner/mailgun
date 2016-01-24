package models

import com.ning.http.client.multipart.StringPart
import errors.Errors.InvalidJsonError
import play.api.libs.functional.syntax._
import play.api.libs.json._

import scala.util.Try

case class SendEmailRequest(to: String, from: String, subject: String, body: Option[String] = None, template: Option[EmailTemplate] = None) {
  /**
   * Converts the fields into StringParts.
   */
  def toStringParts(): Seq[StringPart] = {
    Seq(
      new StringPart("to", to, "UTF-8"),
      new StringPart("from", from, "UTF-8"),
      new StringPart("subject", subject, "UTF-8"),
      new StringPart("html", body.getOrElse(""), "UTF-8")
    )
  }

  /**
   * Replaces the body with the template if it exists, or returns the original data.
   */
  def copyTemplateToBody(): SendEmailRequest = {
    template.flatMap(t =>
      Some(this.copy(body = Some(t.toHtml), subject = t.subject))
    ).getOrElse(this)
  }
}

object SendEmailRequest {
  implicit lazy val reads: Reads[SendEmailRequest] = (
    (__ \ "to").read[String](Reads.email) and
    (__ \ "from").read[String] and // Need to add validation.
    (__ \ "subject").read[String] and
    (__ \ "body").readNullable[String] and
    (__ \ "template").readNullable[EmailTemplate]
  )(SendEmailRequest.apply _)

  implicit lazy val writes: Writes[SendEmailRequest] = Json.writes[SendEmailRequest]

  /**
   * Validates and deserializes the input to an email data.
   */
  def validate(s: String): Try[SendEmailRequest] = {
     Try(Json.parse(s)).map(_.validate[SendEmailRequest].fold(
       { e => throw InvalidJsonError() },
       { v => v })
     )
  }
}
