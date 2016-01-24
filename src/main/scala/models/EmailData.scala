package models

import com.ning.http.client.multipart.StringPart
import play.api.libs.json._
import play.api.libs.functional.syntax._

// We might use type aliases for these Strings in some cases
case class EmailData(to: String, subject: String, body: String) {

  /**
   * Converts the fields into StringParts.
   */
  def toStringParts(): Seq[StringPart] = {
    Seq(
      new StringPart("to", to, "UTF-8"),
      new StringPart("subject", subject, "UTF-8"),
      new StringPart("html", body, "UTF-8")
    )
  }
}

object EmailData {
  // Play's JSON library is easy to use.
  implicit val reads: Reads[EmailData] = (
    (__ \ "to").read[String](Reads.email) and
    (__ \ "subject").read[String] and
    (__ \ "body").read[String]
  )(EmailData.apply _)

  implicit val writes: Writes[EmailData] = Json.writes[EmailData]
}

