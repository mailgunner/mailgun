package util

import com.ning.http.client.AsyncHttpClientConfig.Builder
import com.ning.http.client.Realm.AuthScheme
import com.ning.http.client._
import com.ning.http.client.multipart.StringPart
import models.EmailData
import play.api.libs.ws.WSResponse
import play.api.libs.ws.ning.{NingWSClient, NingWSResponse}

import scala.concurrent.{Future, Promise}

/**
 * Sends messages via MailGun
 */
class MailGunClient(apiKey: String, domain: String) {
  private val realm = new Realm.RealmBuilder()
    .setPrincipal("api")
    .setPassword(apiKey)
    .setUsePreemptiveAuth(true)
    .setScheme(AuthScheme.BASIC)
    .build()

  // Just hardcode the http client and timeout. For the Play app, would just use the built in WS client. Probably wouldn't accept any cert.
  private val config = new Builder().setRequestTimeout(10000).setAcceptAnyCertificate(true).setRealm(realm).build()
  private val wsClient = new NingWSClient(config)

  // Same with the config. This would go into reference.conf
  private val mailgunSendUrl = s"https://api.mailgun.net/v3/$domain/messages"

  // We'll hardcode the sender.
  private val sender = "Mailgun Sandbox <postmaster@sandbox7c022de15a5c47d3b6a22af2652c6e38.mailgun.org>"
  private val senderPart = new StringPart("from", sender, "UTF-8")

  /**
   * Sends an email
   */
  def sendEmail(data: EmailData): Future[WSResponse] = {
    // Play's WS client doesn't support multipart form uploads... should probably use Dispatch instead.
    val asyncHttpClient: AsyncHttpClient = wsClient.underlying
    val postBuilder = asyncHttpClient.preparePost(mailgunSendUrl)
    val Seq(to, subject, body) = data.toStringParts()
    val request = postBuilder
      .addBodyPart(to)
      .addBodyPart(subject)
      .addBodyPart(body)
      .addBodyPart(senderPart)
      .build()

    val promise = Promise[WSResponse]()
    asyncHttpClient.executeRequest(request, new AsyncCompletionHandler[Unit] {
      override def onCompleted(response: Response) = promise.success(NingWSResponse(response))
      override def onThrowable(t: Throwable) = promise.failure(t)
    })
    promise.future
  }

  def close() = wsClient.close()
}
