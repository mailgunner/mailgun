import models.{PasswordResetTemplate, SendEmailRequest, WelcomeTemplate}
import org.scalatest.{FlatSpec, Matchers}
import play.api.libs.json.Json

/**
 * Simple tests to make sure we're deserializing properly.
 */
class SendEmailRequestTest extends FlatSpec with Matchers {
  "EmailData" should "read valid json to an email data" in {
    val s =
      s"""
         |{
         |  "to": "target@some.com",
         |  "from": "sender@some.com",
         |  "subject": "Hello!",
         |  "body": "<h1>Hey!</h1>",
         |  "template": {
         |    "type": "passwordReset",
         |    "userName": "Joe Black",
         |    "resetPassUrl": "http://password-reset-url"
         |  }
         |}
         |
     """.stripMargin

    val expected = SendEmailRequest(
      to = "target@some.com",
      from = "sender@some.com",
      subject = "Hello!",
      body = Some("<h1>Hey!</h1>"),
      template = Some(PasswordResetTemplate("passwordReset", "Joe Black", None, "http://password-reset-url"))
    )

    val deserialized = Json.parse(s).validate[SendEmailRequest].getOrElse(fail("Could not deserialize test data"))
    deserialized shouldEqual expected
  }

  it should "handle welcome templates" in {
    val s =
      s"""
         |{
         |  "to": "target@some.com",
         |  "from": "sender@some.com",
         |  "subject": "Hello!",
         |  "body": "<h1>Hey!</h1>",
         |  "template": {
         |    "type": "welcome",
         |    "userName": "Joe Black",
         |    "confirmAcctUrl": "http://confirm-acct-url"
         |  }
         |}
         |
     """.stripMargin

    val expected = SendEmailRequest(
      to = "target@some.com",
      from = "sender@some.com",
      subject = "Hello!",
      body = Some("<h1>Hey!</h1>"),
      template = Some(WelcomeTemplate("welcome", "Joe Black", "http://confirm-acct-url"))
    )

    val deserialized = Json.parse(s).validate[SendEmailRequest].getOrElse(fail("Could not deserialize test data"))
    deserialized shouldEqual expected
  }

  it should "handle data without templates" in {
    val s =
      s"""
         | {
         |  "to": "target@some.com",
         |  "from": "sender@some.com",
         |  "subject": "Hello!",
         |  "body": "<h1>Hey!</h1>"
         | }
     """.stripMargin

    val expected = SendEmailRequest(
      to = "target@some.com",
      from = "sender@some.com",
      subject = "Hello!",
      body = Some("<h1>Hey!</h1>"),
      template = None
    )

    val deserialized = Json.parse(s).validate[SendEmailRequest].getOrElse(fail("Could not deserialize test data"))
    deserialized shouldEqual expected
  }

  it should "handle data optional fields" in {
    val s =
      s"""
         | {
         |  "to": "target@some.com",
         |  "from": "sender@some.com",
         |  "subject": "Hello!"
         | }
     """.stripMargin

    val expected = SendEmailRequest(
      to = "target@some.com",
      from = "sender@some.com",
      subject = "Hello!"
    )

    val deserialized = Json.parse(s).validate[SendEmailRequest].getOrElse(fail("Could not deserialize test data"))
    deserialized shouldEqual expected
  }
}