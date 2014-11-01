import com.typesafe.plugin.MailerAPI
import models.CodeSender
import org.junit.runner._
import org.mockito.ArgumentCaptor
import org.mockito.Mockito._
import org.specs2.mock.Mockito
import org.specs2.mutable._
import org.specs2.runner._
import play.api.test.Helpers._
import play.api.test._

/**
 * Author: Dmytro Bischak
 * Date: 01.11.2014
 */
@RunWith(classOf[JUnitRunner])
class CodeSenderSpec extends Specification with Mockito {
  "CodeSender" should {
    "send email to recipient with code" in {

      running(FakeApplication()) {

        val mailerMock = mock[MailerAPI]


        val sender = new CodeSender {
          override val mailer: MailerAPI = mailerMock

          override def delayedExecution(block: => Unit): Unit = block
        }

        sender.sendCode("12345", "supercool@email.com")

        val bodyCaptor = ArgumentCaptor.forClass(classOf[String])

        verify(mailerMock).sendHtml(bodyCaptor.capture())

        bodyCaptor.getValue must contain("12345")

        there was one(mailerMock).setRecipient("supercool@email.com")

      }

    }
  }
}
