package balloooon.frontend.controllers

import play.api._
import play.api.mvc._

class Application extends BaseController {

  def index = Action { implicit request =>
    Ok(renderView("Your new application is ready.", views.html.index()))
  }

  def game = Action { implicit request =>
    Ok(renderView("Lets play!", views.html.game()))
  }
}
