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
      get[String]("transaction.account")~
	    get[BigDecimal]("transaction.amount")~
      get[Date]("transaction.dateDone")~
      get[Boolean]("transaction.success") map {
	      case id~account~amount~dateDone~success => Transaction(id, account, amount, dateDone, success)
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

    def getAllAccount( accountId:String ):List[Transaction] ={
      DB.withTransaction { implicit conn =>
          SQL("""
            SELECT * FROM transaction WHERE account = {accountId}
            """).on(
              'accountId -> accountId
            ).as(Transaction.simple.*)
      }
    }

    def getLastSecondTransactions(date:Date):List[Transaction] = {
      val time = date.getTime()
      val secondAgo = time - 1000
      val secondAgoDate = new Date(secondAgo)
      DB.withTransaction { implicit conn =>
          SQL("""
            SELECT * FROM transaction WHERE dateDone > {date}
            """).on(
              'date -> secondAgoDate
            ).as(Transaction.simple.*)
      }
    }
}
