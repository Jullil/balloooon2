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

case class LocationMessage(uid: Long, x: Int, y: Int) {
  val messageType = "location"

  def unapply() = {

  }
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

  implicit val locationMessageReads: Reads[LocationMessage] = (
    (JsPath \ "uid").read[Long] and
      (JsPath \ "x").read[Int] and
      (JsPath \ "y").read[Int]
    ) (LocationMessage.apply _)
}

class GameActor(uid: Long, out: ActorRef) extends Actor {
  val locationUpdatesTopic = "location_updates"

  val mediator = DistributedPubSub(context.system).mediator

  mediator ! Subscribe(locationUpdatesTopic, self)

  mediator ! Publish(locationUpdatesTopic, new LocationMessage(uid, Random.nextInt(1000), Random.nextInt(1000)))

  import LocationMessage.locationMessageWriter
  import LocationMessage.locationMessageReads

  def receive = LoggingReceive {
    case msg: JsValue =>
      (msg \ "data").validate[JsObject].map(data =>
        (msg \ "type").validate[String].map {
          case "location" =>
            Json.fromJson[LocationMessage](data).map { request =>
              mediator ! Publish(locationUpdatesTopic, new LocationMessage(uid, request.x + 100, request.y + 100))
            }
        })
    case msg: LocationMessage =>
      out ! Json.toJson(msg)
      println("New location: " + msg)
  }
}

case class Event(eventType: String, data: EventData)

trait EventData

case class Location(x: Int, y: Int) extends EventData