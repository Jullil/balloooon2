package controllers

import controllers.Application._
import play.twirl.api.Html

/**
  * @author jul
  */
trait BaseController {
  def renderView(title: String, view: Html) = {
    views.html.main(title)(view)
  }

}
