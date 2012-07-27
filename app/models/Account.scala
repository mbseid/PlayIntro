package models

import play.api.db._
import play.api.Play.current

import anorm._
import anorm.SqlParser._

case class Account(id:String, balance:Double, email:String){
	def fundsAvailable(amount:Double):Boolean = {
		if(balance > amount) true else false
	}
}

object Account {
	val simple = {
	    get[String]("id") ~
	    get[Double]("balance") ~
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
  		    	INSERT INTO account values( {id}, {balance}, {email} )
  		    	""").on(
  		    		'id -> account.id,
  		    		'balance -> account.balance,
  		    		'email -> account.email
  		    	).executeUpdate()
  		}	

  		account
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

  	def postTransaction(account:Account, amount:Double) = {

  	}
}