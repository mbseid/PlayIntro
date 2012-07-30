package models

import play.api._
import play.api.db._
import play.api.Play.current

import anorm._
import anorm.SqlParser._
import java.math.{BigDecimal}


case class Account(id:String, balance:BigDecimal, email:String){
	def fundsAvailable(amount:BigDecimal):Boolean = {
		if(balance.doubleValue() >= amount.doubleValue()) true else false
	}
}

object Account {
	val simple = {
	    get[String]("id") ~
	    get[BigDecimal]("balance") ~
	    get[String]("email") map {
	      case id~balance~email => Account(id, balance, email)
	    }
  	}
  	def findAll():Seq[Account] = {
  		DB.withTransaction { implicit conn =>
  		    SQL("""
  		    	SELECT * FROM account
  		    	""").as(Account.simple.*)
  		}
  	}
  	def create( account: Account): Account  = {
  		DB.withTransaction { implicit conn =>
  		    SQL("""
  		    	INSERT INTO account values ( {id}, {balance}, {email} )
  		    	""").on(
  		    		'id -> account.id,
  		    		'balance -> account.balance,
  		    		'email -> account.email
  		    	).executeUpdate()
  		}	

  		account
  	}

    def updateBalance(account:Account, amount:BigDecimal) = {
      DB.withTransaction { implicit conn =>
          val acc:Account = SQL("""
              SELECT * FROM account
              WHERE id = {id}
            """).on(
              'id -> account.id
            ).as(Account.simple.singleOpt).get
            val newBalace = new java.math.BigDecimal(acc.balance.doubleValue() - amount.doubleValue())
            Logger.info("account id" + acc.id + " : " + newBalace)
          SQL("""
              UPDATE account SET balance = {balance} WHERE id = {id}
            """).on(
              'balance -> newBalace,
              'id -> account.id
            ).executeUpdate()
      }
    }

  	def find(id:String): Option[Account] = {
  		DB.withTransaction { implicit conn =>
  		    SQL("""
  		    		SELECT * FROM account
  		    		WHERE id = {id}
  		    	""").on(
  		    		'id -> id
  		    	).as(Account.simple.singleOpt)
  		}	
  	}
}