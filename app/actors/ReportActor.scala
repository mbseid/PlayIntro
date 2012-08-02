package actors

import akka.actor._
import models._

sealed trait ReportRequest
case class GenerateReport(account:Account, transactions:List[Transaction]) extends ReportRequest

sealed trait ReportReply
case class Report(html:String) extends ReportReply

class ReportActor
    extends Actor {


	private def generateHTML(account:Account, transactions:List[Transaction]) = {
		val transcationSummary = transactions.map {
			case (transaction) => "<li>" + transaction.amount + " - " + transaction.dateDone + "</li>"
		} mkString ("", "\n", "\n")

		val accountSumary = "<h1>Acount Summary for " + account.id + "</h1>"

		accountSumary + "<ul>" + transcationSummary + "</ul>"
	}

     def receive = {
        case request: ReportRequest => request match {
            case GenerateReport(account, transactions) =>
            	val html = generateHTML(account, transactions)

            	//Sends responce to itself, so it can be forwarded back out to the sender
                sender ! Report(html)
        }
    }
}