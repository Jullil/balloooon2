package actors

import akka.actor.{ActorLogging, Actor, ActorRef, Props}
import akka.event.LoggingReceive
import balloooon.backend.actors.Avatar.{NewAvatarMessage, DirectionMessage, LocationMessage}
import balloooon.backend.actors.Player
import play.api.libs.functional.syntax._
import play.api.libs.json._

class PlayerSocket(out: ActorRef) extends Actor with ActorLogging {

  import PlayerSocket._

  val player = context.system.actorOf(Player.props(self))

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
    case msg =>
      log.error("Unhandled message: {}", msg)
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
