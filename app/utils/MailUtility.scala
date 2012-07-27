package utils

import play.modules.mail._
import play.modules.mail.MailBuilder._
import play.api.Play.current

object MailUtility {

	def sendEmail(to:String, subject:String, template:play.api.templates.Html) = {
		val m = Mail()
                    .from("Play Framework!","no-reply@mbseid.com")
                    .to(List(("no name",to ,To())))
                    .subject(subject)
                    .html(template)
        MailPlugin.send(m)
	}
}