package actors

import models.SendEmailRequest

sealed trait Messages
case class SendEmail(receipt: String, data: SendEmailRequest) extends Messages
case class FinishedSending(receipt: String) extends Messages
case class GetFromQueue(maxMsgs: Int, maxWaitTime: Int) extends Messages
case class RemoveFromQueue(receipt: String) extends Messages
case object Shutdown extends Messages
case object RouteeDead extends Messages
