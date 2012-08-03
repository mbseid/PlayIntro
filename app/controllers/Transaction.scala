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
import akka.routing._
import scala.concurrent.stm._
import java.util.concurrent.TimeUnit
import java.math.BigDecimal
import scala.util.control.Exception._
import play.api.cache.Cache
import play.api.cache._
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

    val resizer = DefaultResizer(lowerBound = 2, upperBound = 15)
    val reportRouter = Akka.system.actorOf(Props[ReportActor].withRouter(SmallestMailboxRouter(resizer = Some(resizer))))
  	
    val reportActor = Akka.system.actorOf(Props[ReportActor])
    
    val postForm = Form(
      tuple(
          "accountNumber" -> nonEmptyText,
          "amount" -> nonEmptyText
          )
      )

    val reportForm = Form(
      single(
          "accountNumber" -> nonEmptyText
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
    def generateReport(accountNumber:String) = Action { implicit request =>
      val account = Cache.getOrElse[Account]("account."+accountNumber) {
                      Account.find(accountNumber).get
                    }
      val transactions = Cache.getOrElse[List[Transaction]]("transactions."+accountNumber) {
                       models.Transaction.getAllAccount(accountNumber)
                    }

     
      Async{
        (reportRouter ? GenerateReport(account, transactions)).asPromise.map {
            case Report(html) => Ok(views.html.report(html))
            case _ => BadRequest
        }
      }
    }

    def generateReportNoRouter(accountNumber:String) = Action { implicit request =>
      val account = Cache.getOrElse[Account]("account."+accountNumber) {
                      Account.find(accountNumber).get
                    }
      val transactions = Cache.getOrElse[List[Transaction]]("transactions."+accountNumber) {
                       models.Transaction.getAllAccount(accountNumber)
                    }

     
      Async{
        (reportActor ? GenerateReport(account, transactions)).asPromise.map {
            case Report(html) => Ok(views.html.report(html))
            case _ => BadRequest
        }
      }
    }
    def dashboardStream = Action{
      Ok.stream( Streams.getTransactionAmount &> Comet(callback = "parent.dashboard.message"))
    }
}

object Streams{
  val getTransactionAmount = Enumerator.fromCallback { () =>
      Promise.timeout(
        Some("transactions:"+models.Transaction.getLastSecondTransactions(new java.util.Date()).foldLeft(""){ (s:String, t:Transaction) =>
          s + "account-"+t.account+",amount-"+t.amount+";"
          } + "#"),
        1000, TimeUnit.MILLISECONDS)
  }
}