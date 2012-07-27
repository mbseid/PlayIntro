import play.api._

import models._
import anorm._

object Global extends GlobalSettings {
  
  override def onStart(app: Application) {
    InitialData.insert()
  }
  
}

/**
 * Initial set of data to be imported 
 * in the sample application.
 */
object InitialData {
  
  def date(str: String) = new java.text.SimpleDateFormat("yyyy-MM-dd").parse(str)
  
  def insert() = {
    
    if(Account.findAll.isEmpty) {
      
      Seq(
        Account("ABC123", new java.math.BigDecimal(140.00), "mbseid@gmail.com"),
        Account("TES71G", new java.math.BigDecimal(2000.00), "test@test.com")
      ).foreach(Account.create)

    }
    
  }
  
}