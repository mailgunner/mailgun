package actors

import akka.actor._

/**
 * The entry point of the actor system
 */
class Manager(supervisor: ActorRef, queueManager: ActorRef) extends Actor with ActorLogging {
  context.watch(supervisor)
  context.watch(queueManager)

  def receive = {
    case m: GetFromQueue =>
      queueManager ! m

    case m: SendEmail =>
      supervisor ! m

    /*
     * On shutdown, we wait for the supervisor to shutdown,
     */
    case Shutdown =>
      log.info("Shutdown request received...")
      context.become(shuttingDown)
      self ! Shutdown
  }

  def shuttingDown: Receive = {
    case Shutdown =>
      supervisor ! Shutdown
      context.become(waitingForSupervisorToDie)
  }

  def waitingForSupervisorToDie: Receive = {
    case Terminated(a) if a == supervisor =>
      queueManager ! PoisonPill
      context.become(waitingForQueueManagerToDie)
  }

  def waitingForQueueManagerToDie: Receive = {
    case Terminated(a) if a == queueManager =>
      self ! PoisonPill
  }
}

object Manager {
  def props(supervisor: ActorRef, queueManager: ActorRef) = {
    Props.create(classOf[Manager], supervisor, queueManager)
  }
}