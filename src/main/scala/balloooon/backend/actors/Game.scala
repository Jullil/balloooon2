package balloooon.backend.actors

import akka.actor._
import akka.cluster.Cluster
import akka.cluster.ClusterEvent._
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator.{Subscribe, Publish}
import akka.cluster.singleton.{ClusterSingletonManagerSettings, ClusterSingletonManager}

import scala.collection.script.End

object Game {
  final val name = "game"

  def props = Props[Game]

  def startOn(system: ActorSystem) {
    Cluster(system) registerOnMemberUp {
      val game = system.actorOf(ClusterSingletonManager.props(
        singletonProps = props,
        terminationMessage = PoisonPill,
        settings = ClusterSingletonManagerSettings(system)),
        name = name)
      system.log info s"Game started at ${game.path}"
    }
  }

  case class TickAction(time: Long)

}

class Game extends Actor with ActorLogging {

  import Game._
  import Topics._
  import scala.concurrent.duration._
  import scala.concurrent.ExecutionContext.Implicits.global

  val cluster = Cluster(context.system)

  val moveTopic = "tick"
  val mediator = DistributedPubSub(context.system).mediator
  //mediator ! Subscribe(`system tick`, self)

  val cancellable = context.system.scheduler.schedule(0.microseconds, 1.second) {
    mediator ! Publish(`system tick`, new TickAction(100))
  }

  override def preStart() = {
    log.info("The game is starting...")
    cluster.subscribe(self, initialStateMode = InitialStateAsEvents,
      classOf[MemberEvent], classOf[UnreachableMember])
  }

  override def postStop() = {
    cluster unsubscribe self
    log.info("The game was stopped")
  }

  override def receive: Receive = {
    case msg => log.info("Game received a message: " + msg)
  }
}
