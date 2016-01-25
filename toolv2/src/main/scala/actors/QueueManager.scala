package actors

import akka.actor.{ActorLogging, Actor, Props}
import com.amazonaws.services.sqs.AmazonSQSClient
import com.amazonaws.services.sqs.model.{DeleteMessageRequest, ReceiveMessageRequest, ReceiveMessageResult}
import models.SendEmailRequest

import scala.collection.JavaConverters._

class QueueManager(queueUrl: String) extends Actor with ActorLogging {
  // Use the default creds provider chain.
  val sqs: AmazonSQSClient = new AmazonSQSClient

  def receive = {
    case GetFromQueue(maxMsgs, waitTime) =>
      // Should probably use a Scala wrapper for SQS, and put this into a component.
      val req: ReceiveMessageRequest = new ReceiveMessageRequest(queueUrl)
        .withMaxNumberOfMessages(maxMsgs)
        .withWaitTimeSeconds(waitTime)

      val res: ReceiveMessageResult = sqs.receiveMessage(req)
      val messages = res.getMessages.asScala

      log.info(s"${messages.length} messages retrieved from queue")
      messages.foreach { m =>
        // Bad data will kill the actor; need to add supervision strategy
        val data = SendEmailRequest.validate(m.getBody).getOrElse(throw new RuntimeException("invalid data"))
        val msg = SendEmail(m.getReceiptHandle, data)
        sender ! msg
      }

    case RemoveFromQueue(receipt) =>
      val req = new DeleteMessageRequest(queueUrl, receipt)
      sqs.deleteMessage(req) // fire and forget
  }
}

object QueueManager {
  def props(queueUrl: String) = {
    Props.create(classOf[QueueManager], queueUrl)
  }
}