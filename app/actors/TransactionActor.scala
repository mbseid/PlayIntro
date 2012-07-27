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
import java.math.BigDecimal
import models._

case class Post(accountNumber:String, amountNumber:BigDecimal)
case class PostSuccess()
case class PostError( cause: String )

class TransactionActor extends Actor with Timeouts{

  val emailActor = Akka.system.actorOf(Props[EmailActor])
  
  def receive = {
    case Post(accountNumber, amountNumber) => {
      val account = Account.find(accountNumber).get
      val success = account.fundsAvailable(amountNumber);
    	if(success){
        sender ! PostSuccess()
      }else{
        sender ! PostError("insufficient funds")
      }
      if(success) Account.updateBalance(account,amountNumber) 
      models.Transaction.create(account, amountNumber, success)
   	  (emailActor ! Send(account.email, "Transaction", views.html.email()))
    }
  }
}