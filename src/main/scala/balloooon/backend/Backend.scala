package balloooon.backend

import akka.actor.ActorSystem
import balloooon.backend.actors.Game
import com.typesafe.config.ConfigFactory
import scala.collection.JavaConversions._

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object Backend extends App {
  val port = args match {
    case Array() => "0"
    case Array(p) => p
    case args => throw new IllegalArgumentException(s"only ports. Args [ $args ] are invalid")
  }

  val properties = Map(
    "akka.remote.netty.tcp.port" -> port
  )

  val system = ActorSystem("application", (ConfigFactory parseMap properties)
    .withFallback(ConfigFactory.load())
  )

  Game startOn system

  Await.result(system.whenTerminated, Duration.Inf)
}
