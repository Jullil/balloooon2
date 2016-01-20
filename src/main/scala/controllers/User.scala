package controllers

import play.api.mvc.{Action, Controller}

/**
  * @author jul
  */
object User extends Controller with BaseController {

  def signIn = Action {
    Ok(renderView("Sign in", views.html.sign_in()))
  }

  def signUp = Action {
    Ok(renderView("Sign up", views.html.sign_up()))
  }
}
