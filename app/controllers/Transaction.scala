package controllers

import play.api._
import play.api.mvc._
import play.api.data.Forms._
import play.api.data._
import play.api.data.format._
import play.api.data.format.Formats.stringFormat
import play.api.libs._
import play.api.libs.iteratee._
import play.api.libs.concurrent._
import play.api.Play.current
import akka.pattern.ask
import akka.actor._
import scala.concurrent.stm._
import java.util.concurrent.TimeUnit
import java.math.BigDecimal
import scala.util.control.Exception._

import play.api.libs.ws.WS


import utils._

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

	  val transactionActor = Akka.system.actorOf(Props[TransactionActor])
  	val postForm = Form(
      tuple(
          "accountNumber" -> nonEmptyText,
          "amount" -> nonEmptyText
          )
      )
  
  	def postNonThread = Action { implicit request =>
  		postForm.bindFromRequest.fold (
  			errors => BadRequest,
  			{
  				case (accountNumber, amount) =>
            val amountNumber = new BigDecimal(amount)
    				var account = Account.find(accountNumber).get
    				if(account.fundsAvailable(amountNumber)){
              Account.updateBalance(account,amountNumber)
              MailUtility.sendEmail(account.email, "New Transaction", views.html.email())
              models.Transaction.create(account, amountNumber, true)
    					Ok("{success:true}").as("application/json")
    				}else{
              models.Transaction.create(account, amountNumber, false)
    					Ok("{success: false, cause: 'insufficient funds'}").as("application/json")
    				}
  			}

  		)
  	}
    def post = Action { implicit request =>
      postForm.bindFromRequest.fold (
          errors => BadRequest,
          {
            case (accountNumber, amount) =>
              val amountNumber = new BigDecimal(amount)
              Async{
                (transactionActor ? Post(accountNumber, amountNumber)).asPromise.map {
                    case PostSuccess() => Ok("{success:true}").as("application/json")
                    case PostError( cause ) => Ok("{success: false, cause: '"+cause+"'}").as("application/json")
                }
              }
          }
        )
    }
    def dashboardStream = Action{
      Ok.stream( Streams.getHeap &> Comet(callback = "window.dashboard.message"))
    }
}

object Streams{
  val cpu = new models.CPU()

  val getHeap = Enumerator.fromCallback{ () =>
    Promise.timeout(
      Some((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024*1024) + ":memory"),
      100, TimeUnit.MILLISECONDS)
  }

  val getCPU = Enumerator.fromCallback{ () =>
    Promise.timeout(
      Some((cpu.getCpuUsage()*1000).round / 10.0 + ":cpu"),
      100, TimeUnit.MILLISECONDS)
  }
}