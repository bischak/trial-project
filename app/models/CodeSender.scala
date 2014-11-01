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

class CodeSender {

  protected val mailer: MailerAPI = use[MailerPlugin].email

  protected def delayedExecution(block: => Unit): Unit = Akka.system.scheduler.scheduleOnce(1.seconds)(block)

  def sendCode(code: String, email: String) = {

    delayedExecution {

      mailer.setSubject("Code Verification Trial Project")
      mailer.setRecipient(email)

      mailer.setFrom("Dmytro Bischak <dmytro.bischak@gmail.com>")
      mailer.sendHtml(views.html.email_template(code).body)
    }

  }

}

object CodeSender extends CodeSender
