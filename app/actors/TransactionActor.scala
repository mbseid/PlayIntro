package actors

import akka.actor.Actor
import akka.util.Timeout
import akka.util.Duration
import akka.util.duration._
import controllers.Timeouts
import akka.pattern.ask
import akka.actor._
import play.api.libs.concurrent._
import play.api.Play.current

import models._

case class Post(account:String, amount:Double)
case class PostSuccess()
case class PostError( cause: String )

class TransactionActor extends Actor with Timeouts{

  def receive = {
    case PostSuccess() =>{
      
    }
  }
  //   val emailActor = Akka.system.actorOf(Props[EmailActor])
  
  // def receive = {
  //   case Post(acount, amount) => {
  //   	val postSuccess = Account.postTransaction("123ac", 13.75)
  //   	postSuccess match {
  //   		case Some(bool) => {
  //       		if(bool){
  //          			sender ! PostSuccess()
		//         }else{
		//            sender ! PostError("Failure - Lack of funds")
		//         }
       
		//       }
	 //      case None => {
	 //        sender ! PostError("Failure - No account present")
	 //      }
  //   	}
  //  		emailActor ! Send("test@dev.com", "Transaction", views.html.email.transaction())
  //     Account.saveChange("123ac", 13.75);
  //   }
  // }
}