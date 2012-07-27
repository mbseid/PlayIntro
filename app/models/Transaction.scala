package models

import java.util.{Date}
import play.api.db._
import play.api.Play.current

import anorm._
import anorm.SqlParser._

case class Transaction(id:Pk[Long], account:String, amount:Double, date:Date)

object Transaction {
	val simple = {
	    get[Pk[Long]]("transaction.id") ~
      get[String]("transaction.acount")~
	    get[Double]("transaction.amount")~
      get[Date]("transaction.date") map {
	      case id~acount~amount~date => Transaction(id, acount, amount, date)
	    }
  	}

  	def create( account:Account, amount:Double)  = {
  		DB.withTransaction { implicit conn =>
  		    // Get the project id
       val id: Long = SQL("select next value for transaction_seq").as(scalar[Long].single)
       
       // Insert the project
       SQL(
         """
           insert into transaction values (
             {id}, {account}, {amount}, {date}
           )
         """
       ).on(
         'id -> id,
         'acount -> account.id,
         'amount -> amount,
         'date -> new Date()
       ).executeUpdate()
  		}	
  	}
}
