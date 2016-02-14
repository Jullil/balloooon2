package balloooon.backend

import akka.actor.ActorSystem
import balloooon.backend.actors.Game
import com.typesafe.config.ConfigFactory

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object Backend extends App {
  val system = ActorSystem("backend", ConfigFactory.load())

  Game startOn system

  Await.result(system.whenTerminated, Duration.Inf)
}
