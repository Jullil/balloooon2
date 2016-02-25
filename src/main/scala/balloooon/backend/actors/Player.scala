package balloooon.backend.actors

import akka.actor.{ActorLogging, ActorRef, Actor, Props}
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator.{Publish, Subscribe}
import akka.event.LoggingReceive
import akka.routing.FromConfig
import balloooon.backend.actors.Avatar.{DirectionMessage, NewAvatarMessage, LocationMessage}

import scala.util.Random

object Player {
  final val name = "player"

  def props(client: ActorRef) = Props(new Player(client))
}

class Player(client: ActorRef) extends Actor with ActorLogging {
  import Topics._

  val uid = Random.nextInt(100000)

  val mediator = DistributedPubSub(context.system).mediator
  mediator ! Subscribe(`avatar updated its location`, self)

  val avatar = context.system.actorOf(Avatar.props(uid, self), Avatar.name + uid)

  override def preStart() = log.info("A player replica #{} was created", uid)

  override def postStop() = log.info("A player replica #{} was removed", uid)

  override def receive = LoggingReceive {
    case msg: DirectionMessage => avatar forward msg
    case msg: LocationMessage => client ! msg
    case msg: NewAvatarMessage => client ! msg
  }
}
