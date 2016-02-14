package balloooon.backend.actors

import akka.actor.{ActorSystem, Actor, ActorLogging, Props}
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator.Publish

object Game {
  def props = Props[Game]

  def startOn(system: ActorSystem) {
    system.actorOf(props, name = "game")
  }

  case class TickAction(time: Long)
}

class Game extends Actor with ActorLogging {

  import Game._
  import Topics._
  import scala.concurrent.duration._
  import scala.concurrent.ExecutionContext.Implicits.global

  val moveTopic = "tick"
  val mediator = DistributedPubSub(context.system).mediator

  val cancellable = context.system.scheduler.schedule(0.microseconds, 100.milliseconds) {
    mediator ! Publish(`system tick`, new TickAction(100))
  }

  override def receive: Receive = {
    case msg => println("Game received unhandled message: " + msg)
  }
}
