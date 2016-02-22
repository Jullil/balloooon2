package actors

import akka.actor.{ActorLogging, Actor, ActorRef, Props}
import akka.cluster.routing.{ClusterRouterPoolSettings, ClusterRouterPool, ClusterRouterGroup, ClusterRouterGroupSettings}
import akka.event.LoggingReceive
import akka.routing.{ConsistentHashingPool, ConsistentHashingGroup, FromConfig}
import balloooon.backend.actors.Avatar.{DirectionMessage, NewAvatarMessage, LocationMessage}
import balloooon.backend.actors.Player
import play.api.libs.functional.syntax._
import play.api.libs.json._

class PlayerSocket(out: ActorRef) extends Actor with ActorLogging {

  import PlayerSocket._

//  val player = context.system.actorOf(
//    ClusterRouterPool(ConsistentHashingPool(0), ClusterRouterPoolSettings(
//        totalInstances = 100, maxInstancesPerNode = 3,
//        allowLocalRoutees = false, useRole = None)).props(Player.props(self))
//  //  FromConfig.props(Player.props(self))
//  , name = "player3")
//  val player = context.system.actorOf(FromConfig.props(Player.props(self)), name = "PlayerRouter")
  val player = context.system.actorOf(Player.props(self))


  player ! DirectionMessage(123, 123)

  override def preStart() = log.info("A new player session was opened")

  override def postStop() = log.info("A player session was closed")

  def receive = LoggingReceive {
    case msg: JsValue =>
      (msg \ "data").validate[JsObject].map(data =>
        (msg \ "type").validate[String].map {
          case "direction" =>
            Json.fromJson[DirectionMessage](data).map(player ! _)
        })
    case msg: LocationMessage =>
      out ! Json.toJson(msg)
    case msg: NewAvatarMessage =>
      out ! Json.toJson(msg)
  }
}


object PlayerSocket {
  def props(out: ActorRef) = Props(new PlayerSocket(out))

  implicit val locationMessageWriter: Writes[LocationMessage] =
    new Writes[LocationMessage] {
      def writes(message: LocationMessage) = Json.obj(
        "type" -> message.messageType,
        "uid" -> message.uid,
        "x" -> message.x,
        "y" -> message.y
      )
    }

  implicit val directionMessageReads: Reads[DirectionMessage] = (
    (JsPath \ "x").read[Int] and
      (JsPath \ "y").read[Int]
    ) (DirectionMessage.apply _)

  implicit val newAvatarMessageWriter: Writes[NewAvatarMessage] =
    new Writes[NewAvatarMessage] {
      def writes(message: NewAvatarMessage) = Json.obj(
        "type" -> message.messageType,
        "uid" -> message.uid,
        "x" -> message.x,
        "y" -> message.y
      )
    }
}
