package balloooon.frontend.controllers

import play.api.mvc.{Flash, Controller}
import play.twirl.api.Html
import play.api.i18n.{MessagesApi, I18nSupport, Messages}
import javax.inject._

abstract class BaseController extends Controller with I18nSupport {
  @Inject var messagesApi: MessagesApi = null

  def renderView(title: String, view: Html)(implicit flash:Flash, messages: Messages) = {
    views.html.main(title)(view)(flash, messages)
  }

}
