package balloooon.backend.actors

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator.{Publish, Subscribe}
import akka.event.LoggingReceive
import balloooon.backend.actors.Game.TickAction

class Avatar(id: Long, player: ActorRef) extends Actor with ActorLogging {
  import Avatar._
  import Topics._

  val mediator = DistributedPubSub(context.system).mediator
  mediator ! Subscribe(`system tick`, self)

  val startCoords: (Long, Long) = (0, 0)

  player ! new NewAvatarMessage(id, startCoords._1, startCoords._2)

  def avatar(coords: (Long, Long), normDelta: (Double, Double)): Receive = LoggingReceive {
    case msg: DirectionMessage =>
      val modD = Math.sqrt(msg.dX * msg.dX + msg.dY * msg.dY)
      context.become(avatar(coords, (msg.dX / modD, msg.dY / modD)))
      log.info("Avatar " + id + ": direction was changed")
    case TickAction(time) =>
      log.info("Avatar " + id + ": I should react on tick")
      val speed = 1
      val x = Math.round(normDelta._1 * speed * time + coords._1)
      val y = Math.round(normDelta._2 * speed * time + coords._1)
      context.become(avatar((x, y), normDelta))

      mediator ! Publish(`avatar updated its location`, new LocationMessage(id, coords._1, coords._2))
  }

  override def preStart() = log.info("Avatar #{} was created", id)

  override def postStop() = log.info("Avatar #{} was removed", id)

  override def receive = avatar(startCoords, (0.0, 0.0))
}

object Avatar {
  final val name = "avatar"

  def props(id: Long, player: ActorRef) = Props(new Avatar(id, player))

  case class LocationMessage(uid: Long, x: Long, y: Long) {
    val messageType = "location"
  }

  case class DirectionMessage(dX: Int, dY: Int) {
    val messageType = "direction"
  }

  case class NewAvatarMessage(uid: Long, x: Long, y: Long) {
    val messageType = "newAvatar"
  }

}
