package actors

import akka.actor._
import akka.routing.{ActorRefRoutee, Broadcast, RoundRobinRoutingLogic, Router}
import com.typesafe.config.ConfigFactory

class Supervisor(queueManager: ActorRef) extends Actor with ActorLogging {

  val numRoutees = ConfigFactory.load().getInt("mailgunner.num-workers")

  var router = {
    val routees = Vector.fill(numRoutees) {
      val r = context.actorOf(Props[MailGunner])
      context watch r
      ActorRefRoutee(r)
    }
    Router(RoundRobinRoutingLogic(), routees)
  }

  def receive = {
    case m: SendEmail =>
      router.route(m, sender())

    case r: RemoveFromQueue =>
      queueManager ! r

    case RouteeDead =>
      router = router.removeRoutee(sender())

    case Shutdown =>
      context.become(shuttingDown)
      router.route(Broadcast(PoisonPill), sender())
  }

  def shuttingDown: Receive = {
    // Watch the routees; if none are alive, take poison pill
    case Terminated(a) =>
      if (router.routees.isEmpty) self ! PoisonPill

    case RouteeDead =>
      router = router.removeRoutee(sender())

    case r: RemoveFromQueue =>
      queueManager ! r
  }
}

object Supervisor {
  def props(queueManager: ActorRef) = {
    Props.create(classOf[Supervisor], queueManager)
  }
}
