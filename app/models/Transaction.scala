package models

import java.util.{Date}
import play.api.db._
import play.api.Play.current
import java.math.BigDecimal
import anorm._
import anorm.SqlParser._

case class Transaction(id:Pk[Long], account:String, amount:BigDecimal, date:Date, success:Boolean)

object Transaction {
	val simple = {
	    get[Pk[Long]]("transaction.id") ~
      get[String]("transaction.acount")~
	    get[BigDecimal]("transaction.amount")~
      get[Date]("transaction.date")~
      get[Boolean]("transaction.success") map {
	      case id~acount~amount~date~success => Transaction(id, acount, amount, date, success)
	    }
  	}

  	def create( account:Account, amount:BigDecimal, success:Boolean)  = {
  		DB.withTransaction { implicit conn =>
  		    // Get the project id
       val id: Long = SQL("select next value for transaction_seq").as(scalar[Long].single)
       
       // Insert the project
       SQL(
         """
           insert into transaction values (
             {id}, {account}, {amount}, {date}, {success}
           )
         """
       ).on(
         'id -> id,
         'account -> account.id,
         'amount -> amount,
         'date -> new Date(),
         'success -> success
       ).executeUpdate()
  		}	
  	}
}
