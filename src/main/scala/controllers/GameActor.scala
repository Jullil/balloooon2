package controllers

import akka.actor.{Props, Actor, ActorRef}
import akka.event.LoggingReceive
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator.{Publish, Subscribe}
import play.api.libs.json._
import play.api.libs.functional.syntax._

import scala.util.Random

object GameActor {
  def props(uid: Long)(out: ActorRef) = Props(new GameActor(uid, out))
}

case class LocationMessage(uid: Long, x: Long, y: Long) {
  val messageType = "location"
}

object LocationMessage {
  implicit val locationMessageWriter = new Writes[LocationMessage] {
    def writes(message: LocationMessage) = Json.obj(
      "type" -> message.messageType,
      "uid" -> message.uid,
      "x" -> message.x,
      "y" -> message.y
    )
  }
}

case class DirectionMessage(dX: Int, dY: Int) {
  val messageType = "direction"
}
object DirectionMessage {
  implicit val directionMessageReads: Reads[DirectionMessage] = (
    (JsPath \ "x").read[Int] and
      (JsPath \ "y").read[Int]
    ) (DirectionMessage.apply _)
}

case class NewAvatarMessage(uid: Long, x: Int, y: Int) {
  val messageType = "newAvatar"
}
object NewAvatarMessage {
  implicit val newAvatarMessageWriter = new Writes[NewAvatarMessage] {
    def writes(message: NewAvatarMessage) = Json.obj(
      "type" -> message.messageType,
      "uid" -> message.uid,
      "x" -> message.x,
      "y" -> message.y
    )
  }
}

class GameActor(uid: Long, out: ActorRef) extends Actor {
  val locationUpdatesTopic = "locationUpdates"

  val mediator = DistributedPubSub(context.system).mediator

  private val posX = Random.nextInt(400)
  private val posY = Random.nextInt(400)

  out ! Json.toJson(new NewAvatarMessage(uid, posX, posY))

  mediator ! Subscribe(locationUpdatesTopic, self)

  mediator ! Publish(locationUpdatesTopic, new LocationMessage(uid, posX, posY))

  val avatar = new Avatar(posX, posY)

  var time = 0

  def receive = LoggingReceive {
    case msg: JsValue =>
      (msg \ "data").validate[JsObject].map(data =>
        (msg \ "type").validate[String].map {
          case "direction" =>
            Json.fromJson[DirectionMessage](data).map(self ! _)
        })
    case msg: DirectionMessage =>
      val modD = Math.sqrt(msg.dX * msg.dX + msg.dY * msg.dY)
      val normD = (msg.dX / modD, msg.dY / modD)
      println("Norm delta: " + normD)
      val speed = 1
      avatar.x = Math.round(normD._1 * speed * time + avatar.x)
      avatar.y = Math.round(normD._2 * speed * time + avatar.y)
      time = time + 1

      mediator ! Publish(locationUpdatesTopic, new LocationMessage(uid, avatar.x, avatar.y))
    case msg: LocationMessage =>
      out ! Json.toJson(msg)
      println("New location: " + msg)
    case msg =>
      println("Unhandled message: " + msg)
  }
}

case class Avatar(var x: Long, var y: Long)

case class Event(eventType: String, data: EventData)

trait EventData

case class Location(x: Int, y: Int) extends EventData
