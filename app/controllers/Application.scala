package controllers

import play.api._
import play.api.mvc._

import akka.util.Timeout
import akka.util.Duration
import akka.util.duration._

import models._

object Application extends Controller {
  
  def index = Action {
    Ok(views.html.index())
  }
  
  def dashboard = Action{
  	val accounts = Account.findAll()
  	Ok(views.html.dashboard(accounts))
  }

  def javascriptRoutes = Action { implicit request =>
    
    import routes.javascript._
    Ok(
      Routes.javascriptRouter("jsRoutes")(
        Transaction.post, Transaction.postNonThread
      )
    ).as("text/javascript") 
  }
}

trait Timeouts {
    implicit val timeout = Timeout(5 seconds)
}