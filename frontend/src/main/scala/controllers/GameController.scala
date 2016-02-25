package controllers

import javax.inject.{Inject, _}
import akka.actor.ActorSystem
import actors.PlayerSocket
import play.api.Play.current
import play.api.libs.json.JsValue
import play.api.mvc.{Controller, WebSocket}

@Singleton
class GameController @Inject()(system: ActorSystem) extends Controller {

  def create = WebSocket.acceptWithActor[JsValue, JsValue] { request => out =>
    PlayerSocket.props(out)
  }
}
