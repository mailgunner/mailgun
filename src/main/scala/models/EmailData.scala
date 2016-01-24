package models

import com.ning.http.client.multipart.StringPart
import play.api.libs.functional.syntax._
import play.api.libs.json._

case class EmailData(to: String, subject: String, body: Option[String] = None, template: Option[EmailTemplate] = None) {
  /**
   * Converts the fields into StringParts.
   */
  def toStringParts(): Seq[StringPart] = {
    Seq(
      new StringPart("to", to, "UTF-8"),
      new StringPart("subject", subject, "UTF-8"),
      new StringPart("html", body.getOrElse(""), "UTF-8")
    )
  }
}

object EmailData {
  // Play's JSON library is easy to use.
  implicit lazy val reads: Reads[EmailData] = (
    (__ \ "to").read[String](Reads.email) and
    (__ \ "subject").read[String] and
    (__ \ "body").readNullable[String] and
    (__ \ "template").readNullable[EmailTemplate]
  )(EmailData.apply _)

  implicit lazy val writes: Writes[EmailData] = Json.writes[EmailData]
}
