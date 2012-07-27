package controllers

import play.api._
import play.api.mvc._
import play.api.data.Forms._
import play.api.data._

import play.api.libs.concurrent._
import play.api.Play.current
import akka.pattern.ask
import akka.actor._

import actors._
import models._
/** Uncomment the following lines as needed **/
/**
import play.api.Play.current
import play.api.libs._
import play.api.libs.iteratee._
import play.api.libs.concurrent._
import java.util.concurrent._
import scala.concurrent.stm._
import akka.util.duration._
import play.api.cache._
import play.api.libs.json._
**/

object Transaction extends Controller with Timeouts{

	//val transactionActor = Akka.system.actorOf(Props[TransactionActor])
  
  	val postForm = Form(
      tuple(
          "accountNumber" -> nonEmptyText,
          "amount" -> number
          )
      )
      
  	// def post = Action { implicit request =>
   //  	Async{
   //    		(transactionActor ? Post("123ac", 13.75)).asPromise.map {
   //      		case PostSuccess() => Ok("Success")
   //      		case PostError( cause ) => BadRequest("Failure - " + cause)
   //    		}
   //  	}
  	// }
  
  	def postNonThread = Action { implicit request =>
  		postForm.bindFromRequest.fold (
  			errors => BadRequest,
  			{
  				case (accountNumber, amount) =>
					val account = Account.find(accountNumber).get
					if(account.fundsAvailable(amount)){


						Ok("{success:true}").as("application/json")
					}else{
						Ok("{success: false, cause: 'insufficient funds'").as("application/json")
					}
  			}

  		)
  	}
}