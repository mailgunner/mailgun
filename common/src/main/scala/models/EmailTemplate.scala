package models

import play.api.libs.functional.syntax._
import play.api.libs.json._

import scalatags.Text.all._

sealed trait EmailTemplate {
  def `type`: String
  def toHtml: String
  def subject: String
}

case class PasswordResetTemplate(`type`: String = "passwordReset", userName: String, accountName: Option[String], resetPassUrl: String) extends EmailTemplate {
  val subject = "Reset your password"

  def toHtml = {
    val maybeAccountBlurb = accountName.map(n => s" for $n").getOrElse("")
    html(
      body(
        div(
          h2(color := "blue")(s"Hi $userName,"),
          p("We received a request to reset the password for your account."),
          p(s"If you requested a password reset$maybeAccountBlurb, click the link below:"),
          a(href := resetPassUrl)(p(resetPassUrl))
        )
      )
    ).toString()
  }
}

case class WelcomeTemplate(`type`: String = "welcome", userName: String, confirmAcctUrl: String) extends EmailTemplate {
  val subject = "Welcome to Acme Corp!"

  def toHtml = {
    html(
      body(
        div(
          h2(color := "blue")(s"Hello $userName,"),
          p("Welcome to Acme Corp, thank you for signing up. Please confirm your registration by clicking the link below:"),
          a(href := confirmAcctUrl)(p(confirmAcctUrl))
        )
      )
    ).toString()
  }
}

object EmailTemplate {
  implicit val emailTemplateReads: Reads[EmailTemplate] = {
    val pr = Json.reads[PasswordResetTemplate]
    val wr = Json.reads[WelcomeTemplate]

    __.read[PasswordResetTemplate](pr).map(x => x: PasswordResetTemplate) |
    __.read[WelcomeTemplate](wr).map(x => x: WelcomeTemplate)
  }

  implicit val emailTemplateWrites = Writes[EmailTemplate] {
    case password: PasswordResetTemplate => Json.writes[PasswordResetTemplate].writes(password)
    case welcome: WelcomeTemplate => Json.writes[WelcomeTemplate].writes(welcome)
  }
}
