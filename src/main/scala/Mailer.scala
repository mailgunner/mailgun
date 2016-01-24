
import models.EmailData
import play.api.libs.json.Json
import play.api.libs.ws.WSResponse
import util.MailGunClient

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

/**
 * Simple tool to send single email from JSON using Mailgun API.
 *
 * Takes 4 args: api key, domain, and JSON string.
 *
 * The JSON string has the schema:
 *  {
 *    "to": "target@some.com",
 *    "subject": "Hello!",
 *    "body": "<h1>Hey!</h1>"
 *  }
 *
 * Example usage:
 *
 * sbt 'mailgunner/run key-123 sandbox123.mailgun.org {"to":"me@some.com","subject":"Hi","body":"<html><body><h1>hi</h1></body></html>"}'
 */
object Mailer extends App {
  // Would process args properly for a real tool.
  if (args.length != 3) {
    () // Print usage here
  }
  else {
    // We'll assume the input is valid.
    val (apiKey, domain, data) = (args(0), args(1), args(2))
    val mailer = new MailGunClient(apiKey, domain)
    processEmail(mailer, data)
    mailer.close()
  }

  /*
   * Deserialize the JSON and send it with the mailer
   */
  private def processEmail(mailer: MailGunClient, input: String): Unit = {
    val res: Future[WSResponse] = for {
      data <- validateEmailData(input)
      resp <- mailer.sendEmail(data)
    } yield resp

    val recovered = res.map(_.body).recover { case e => s"An error occurred: ${e.getMessage}"}
    val output = Await.result(recovered, 3.seconds) // Block for the mailer.
    println(output) // Just print out the result
  }

  private def validateEmailData(s: String): Future[EmailData] = {
    Future {
      val js = Json.parse(s) // just let this throw the default json parse exception.
      js.validate[EmailData].fold(
        { e => throw new RuntimeException("Invalid json format")},
        { v => v }
      )
    }

    // Could also condense it like this:
    //
    // Future(Json.parse(s)).map(_.validate[EmailData].fold(
    //   { e => throw new RuntimeException("Invalid json format") },
    //   { v => v })
    // )
    //
    // But separating it out is more readable and allows us to handle
    // the exception from Json.parse().
  }
}

