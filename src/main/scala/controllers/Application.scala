package controllers

import play.api._
import play.api.mvc._

object Application extends Controller with BaseController {

  def index = Action {
    Ok(renderView("Your new application is ready.", views.html.index()))
  }

  def game = Action {
    Ok(renderView("Lets play!", views.html.game()))
  }
}
