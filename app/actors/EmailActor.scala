package actors

import akka.actor.Actor
import akka.util.Timeout
import akka.util.Duration
import akka.util.duration._
import controllers.Timeouts
import utils._

case class Send(to:String, subject:String, message:play.api.templates.Html)


class EmailActor extends Actor with Timeouts{
  
  
  def receive = {
    case Send(to, subject, message) =>
       MailUtility.sendEmail(to, subject, message)
      
  }

  override def postStop = {
        
  }
}