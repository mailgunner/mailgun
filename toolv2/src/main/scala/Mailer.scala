
import actors.Reaper.WatchMe
import actors._
import akka.actor._
import com.typesafe.config.ConfigFactory
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Simple tool to pull SendEmailRequests from SQS and then send them using Mailgun API.
 */
object Mailer extends App {

  sys.addShutdownHook {
    manager ! Shutdown
    system.awaitTermination()
  }

  lazy val system = ActorSystem("toolv2")
  lazy val queueUrl = ConfigFactory.load().getString("mailgunner.sqs.queueUrl")
  lazy val getMessageInterval = ConfigFactory.load().getInt("mailgunner.get-message-interval").seconds
  lazy val maxMessages = ConfigFactory.load().getInt("mailgunner.max-messages-per-retrieval")
  lazy val maxMessageGetTime = ConfigFactory.load().getInt("mailgunner.max-message-get-time")

  val queueManager = system.actorOf(QueueManager.props(queueUrl), "queueManager")
  val supervisor = system.actorOf(Supervisor.props(queueManager), "router")
  val manager = system.actorOf(Manager.props(supervisor, queueManager), "manager")

  val reaper = system.actorOf(Props[Reaper], "reaper")
  reaper ! WatchMe(queueManager)
  reaper ! WatchMe(supervisor)
  reaper ! WatchMe(manager)

  system.scheduler.schedule(
    initialDelay = 0.seconds,
    interval = getMessageInterval,
    receiver = manager,
    message = GetFromQueue(maxMessages, 10)
  )
}
