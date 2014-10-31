package controllers

import models.{CodeGenerator, CodeSender, WeatherProvider}
import play.api.Logger
import play.api.data.Forms._
import play.api.data._
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class FormData(email: String)

object Application extends Controller {

  private val CITY = "Scottsdale"

  val emailForm: Form[FormData] = Form(
    mapping(
      "email" -> email
    )(FormData.apply)(FormData.unapply)
  )

  def resend = Action {
    Redirect("/").withNewSession
  }

  def validate(code: String) = Action {
    implicit request =>

      request.session.get("code") match {
        case Some(mustCode) if code.toString == mustCode =>
          Redirect("/").withSession("validated" -> "true")
        case _ =>
          BadRequest(views.html.fail())
      }
  }

  def index = Action.async {
    implicit request =>

      if (request.session.get("validated").nonEmpty) {
        WeatherProvider.getWeatherIn(CITY)
          .map(
            data => Ok(views.html.weather(data)).withSession(request.session)
          )
      } else if (request.session.get("sent").nonEmpty) {
        Future(Ok(views.html.sent()).withSession(request.session))
      } else {
        Future(Ok(views.html.index(emailForm)))
      }
  }

  def submit = Action {
    implicit request =>

      emailForm.bindFromRequest.fold(
      withErrors => {
        Ok(views.html.index(withErrors))
      }, {
        case FormData(yourEmail) =>

          val code = CodeGenerator.newCode.toString

          CodeSender.sendCode(code, yourEmail)

          Logger.debug(s"Sending validation code $code to $yourEmail")

          Ok(views.html.sent()).withSession(
            "sent" -> "true",
            "code" -> code
          )

      })
  }


}