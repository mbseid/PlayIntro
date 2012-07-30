package models

import java.util.{Date}
import play.api.db._
import play.api.Play.current
import java.math.BigDecimal
import anorm._
import anorm.SqlParser._

case class Transaction(id:Pk[Long], account:String, amount:BigDecimal, dateDone:Date, success:Boolean)

object Transaction {
	val simple = {
	    get[Pk[Long]]("transaction.id") ~
      get[String]("transaction.acount")~
	    get[BigDecimal]("transaction.amount")~
      get[Date]("transaction.dateDone")~
      get[Boolean]("transaction.success") map {
	      case id~acount~amount~dateDone~success => Transaction(id, acount, amount, dateDone, success)
	    }
  	}

  	def create( account:Account, amount:BigDecimal, success:Boolean)  = {
  		DB.withTransaction { implicit conn =>
  		    // Get the project id
       val id: Long = SQL("SELECT nextval('transaction_seq')").as(scalar[Long].single)
       
       // Insert the project
       SQL(
         """
           insert into transaction values (
             {id}, {account}, {amount}, {dateDone}, {success}
           )
         """
       ).on(
         'id -> id,
         'account -> account.id,
         'amount -> amount,
         'dateDone -> new Date(),
         'success -> success
       ).executeUpdate()
  		}	
  	}
}
