package controllers

import play.api._
import play.api.mvc._

import akka.util.Timeout
import akka.util.Duration
import akka.util.duration._

object Application extends Controller {
  
  def index = Action {
    Ok(views.html.index())
  }
  
}

trait Timeouts {
    implicit val timeout = Timeout(5 seconds)
}