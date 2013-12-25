package fi.eraleijonat.emailer

import org.scalatra._
import scalate.ScalateSupport

class EmailerServlet extends ScalatraServlet {

  val newMemberFieldsRequired = Seq("firstNames", "lastName", "address", "dob")
  val newMemberFieldsOptional = Seq("phone", "email", "huoltaja-name", "huoltaja-phone", "huoltaja-email")

  post("/new-member") {
    // Halt if a required field is not present in parameters.
    if (newMemberFieldsRequired.map(params.get(_)).exists(_.isEmpty)) {
      halt(400)
    }

    // Otherwise send email.

  }
  
}
