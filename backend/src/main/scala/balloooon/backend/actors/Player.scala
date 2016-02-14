package balloooon.backend.actors

import akka.actor.{ActorRef, Actor, Props}
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator.{Publish, Subscribe}
import balloooon.backend.actors.Avatar.{NewAvatarMessage, DirectionMessage, LocationMessage}

import scala.util.Random

object Player {
  def props(client: ActorRef) = Props(new Player(client))
}

class Player(client: ActorRef) extends Actor {
  import Topics._

  val uid = Random.nextInt(100000)

  val mediator = DistributedPubSub(context.system).mediator
  mediator ! Subscribe(`avatar updated its location`, self)

  val avatar = context.system.actorOf(Avatar.props(uid, self))

  override def receive: Receive = {
    case msg: DirectionMessage => avatar forward msg
    case msg: LocationMessage => client ! msg
    case msg: NewAvatarMessage => client ! msg
  }
}
