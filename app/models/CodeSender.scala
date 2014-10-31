package models

import com.typesafe.plugin._
import play.api.Play.current
import play.api.libs.concurrent.Akka

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

/**
 * Author: Dmytro Bischak
 * Date: 30.10.2014
 */
object CodeSender {

  def sendCode(code: String, email: String) = {

    Akka.system.scheduler.scheduleOnce(1.seconds) {

      val mail = use[MailerPlugin].email
      mail.setSubject("Code Verification Trial Project")
      mail.setRecipient(email)

      mail.setFrom("Dmytro Bischak <dmytro.bischak@gmail.com>")
      mail.sendHtml(views.html.email_template(code).body)
    }

  }

}
