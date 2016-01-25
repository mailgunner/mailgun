package actors

import akka.actor.{Actor, ActorRef, Terminated}

import scala.collection.mutable.ArrayBuffer

/**
 * Derek Wyatt's reaper.
 */
object Reaper {
  case class WatchMe(ref: ActorRef)
  case object Shutdown
}

class Reaper extends Actor {
  import Reaper._

  val watched = ArrayBuffer.empty[ActorRef]

  def allSoulsReaped(): Unit = context.system.shutdown()

  // Watch and check for termination
  final def receive = {
    case WatchMe(ref) =>
      context.watch(ref)
      watched += ref

    case Terminated(ref) =>
      watched -= ref
      if (watched.isEmpty) allSoulsReaped()
  }
}
