package utils

import play.modules.mail._
import play.modules.mail.MailBuilder._
import play.api.Play.current

import play.api.libs.json._
import play.api.libs.ws._

object MailUtility {

	def sendEmail(to:String, subject:String, template:play.api.templates.Html) = {
		val m = Mail()
                    .from("Play Framework!","no-reply@mbseid.com")
                    .to(List(("no name",to ,To())))
                    .subject(subject)
                    .html(template)
        MailPlugin.send(m)

        val url = "http://godhatesfags.com"
        WS.url(url).withHeaders(
	        "Accept-Language" -> "en-US,en;q=0.8",
	        "Accept-Charset" -> "utf-8;q=0.7,*;q=0.3",
	        "Cache-Control" -> "max-age=0"
	      ).get().map { response =>
	        // Do some sync work
	      }
	}
}