package controllers

import javax.inject.Inject

import models.User
import play.api.data.Forms._
import play.api.data.Form
import play.api.mvc.{Action, Controller}
import play.api.i18n.{I18nSupport, MessagesApi}
import views.html.helper.form

class UserController extends BaseController {
  val userForm = Form(
    mapping(
      "email" -> email,
      "name" -> optional(nonEmptyText),
      "password" -> nonEmptyText
    )(User.apply)(User.unapply)
  )
  val loginForm = Form(
    tuple(
      "email" -> email,
      "password" -> nonEmptyText
    )
  )

  def signIn = Action { implicit request =>
    Ok(renderView("Sign in", views.html.sign_in(loginForm)))
  }

  def signUp = Action { implicit request =>
    Ok(renderView("Sign up", views.html.sign_up(userForm)))
  }

  def newUser = Action { implicit request =>
    userForm.bindFromRequest.fold(
      formWithErrors => {
        BadRequest(renderView("Sign up", views.html.sign_up(formWithErrors)))
      },
      user => {
        //val id = models.User.create(user)
        Redirect(routes.Application.game).flashing("success" -> "You have been registered successfully!")
      }
    )
  }

  def auth = Action { implicit request =>
    loginForm.bindFromRequest.fold(
      formWithErrors => {
        BadRequest(renderView("Sign in", views.html.sign_in(formWithErrors)))
      },
      data => {
        println(data)
        //val id = models.User.create(user)
        Redirect(routes.Application.index).flashing("success" -> "You have been registered successfully!")
      }
    )
  }
}
