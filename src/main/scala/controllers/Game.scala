package controllers

import play.api.libs.json.{Json, JsValue}
import play.api.mvc.WebSocket.FrameFormatter
import play.api.mvc._
import play.api.Play.current

/**
  * @author jul
  */
object Game extends Controller {
  var avatarCount = 0

  def create = WebSocket.acceptWithActor[JsValue, JsValue] { request => out =>
    avatarCount += 1
    GameActor.props(avatarCount)(out)
  }
}
