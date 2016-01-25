package actors

import akka.actor.{ActorLogging, Actor, Stash}
import akka.pattern.pipe
import com.typesafe.config.ConfigFactory
import mailgun.MailGunClient

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

class MailGunner extends Actor with Stash with ActorLogging {
  val apiKey = ConfigFactory.load().getString("mailgunner.apiKey")
  val domain = ConfigFactory.load().getString("mailgunner.domain")
  val timeout = ConfigFactory.load().getInt("mailgunner.timeout")
  val client = new MailGunClient(apiKey, domain)

  def receive = waiting

  def waiting: Receive = {
    case m @ SendEmail(receipt, data) =>
      /*
       * Blocking so that we ensure we remove the message from the queue even if we get poison pilled.
       *
       * Should validate the response from mailgun here since a 400 means we'll delete the message.
       */
      val futResp = client.sendEmail(data).map(_ => RemoveFromQueue(receipt)) pipeTo context.parent
      Await.result(futResp, 5.seconds)
      log.info(s"Email sent: ${data}")

  }

  override def postStop() {
    context.parent ! RouteeDead
    client.close()
  }
}
