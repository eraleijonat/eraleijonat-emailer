package fi.eraleijonat.emailer


import _root_.akka.actor.ActorSystem
import org.scalatra._
import dispatch.Future
import dispatch.as
import scala.concurrent.{Promise, ExecutionContext}

class EmailerServlet(system: ActorSystem) extends ScalatraServlet with FutureSupport {

  protected implicit def executor: ExecutionContext = system.dispatcher

  val apiKey              = System.getenv("mailgun_api_key")
  val apiLogin            = System.getenv("mailgun_api_login")
  val newMemberRecipients = System.getenv("new_member_recipients")

  requireEnvsPresent()

  val apiUrl = "https://api.mailgun.net/v2/" + apiLogin + "/messages"

  val newMemberFieldsRequired: Seq[String] = Seq("firstNames", "lastName", "address", "dob")
  val newMemberFieldsOptional: Seq[String] = Seq("phone", "email", "huoltaja-name", "huoltaja-phone", "huoltaja-email")

  def requireEnvsPresent(): Unit = {
    Seq(apiKey, apiLogin, newMemberRecipients).foreach(
      env => require(env != null && env.nonEmpty)
    )
  }

  post("/new-member") {
    new AsyncResult() {
      val is = sendNewMemberMail()
    }
  }

  def sendNewMemberMail(): Future[String] = {
    val promise = Promise[String]()

    // Halt if a required field is not present in parameters.
    newMemberFieldsRequired.foreach(requiredParam => {
      if (params.get(requiredParam).isEmpty) {
        halt(status = 400, body = requiredParam + " missing")
      }
    })

    // Collect all member info fields from params
    val member: Map[String, String] = params.filterKeys(key => newMemberFieldsRequired.contains(key) || newMemberFieldsOptional.contains(key))

    // Send email using mailgun
    val req = dispatch.url(apiUrl).POST.secure
      .as_!("api", apiKey)
      .addParameter("from",     "noreply@era-leijonat.fi")
      .addParameter("to",       newMemberRecipients)
      .addParameter("subject", "Uusi jäsenhakemus lippukunnan nettisivuilla")
      .addParameter("text",     member.mkString("\n"))

    // Asynchronous HTTP using Dispatch
    dispatch.Http(req OK as.String).onComplete({
      case res => promise.complete(res)
    })

    promise.future
  }
  
}