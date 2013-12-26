package fi.eraleijonat.emailer

import _root_.akka.actor.ActorSystem
import org.scalatra._
import dispatch.Future
import dispatch.as
import scala.concurrent.{Promise, ExecutionContext}
import org.scalatra.json.JacksonJsonSupport
import org.json4s.{JField, DefaultFormats, Formats}
import org.json4s.JsonAST.JString

class EmailerServlet extends ScalatraServlet with FutureSupport with CorsSupport with JacksonJsonSupport {

  protected implicit def executor: ExecutionContext = ActorSystem("actors").dispatcher
  protected implicit val jsonFormats: Formats = DefaultFormats

  val apiKey              = System.getenv("mailgun_api_key")
  val apiLogin            = System.getenv("mailgun_api_login")
  val newMemberRecipients = System.getenv("new_member_recipients")

  requireEnvsPresent()

  val apiUrl = "https://api.mailgun.net/v2/" + apiLogin + "/messages"

  val newMemberFieldsRequired: Seq[String] = Seq("firstNames", "lastName", "address", "dob")
  val newMemberFieldsOptional: Seq[String] = Seq("phone", "email", "huoltaja-name", "huoltaja-phone", "huoltaja-email", "details")

  options("/*"){
    response.setHeader("Access-Control-Allow-Headers", request.getHeader("Access-Control-Request-Headers"))
  }

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
      if ((parsedBody \ requiredParam).asInstanceOf[JString].s.isEmpty) {
        halt(status = 400, body = requiredParam + " missing")
      }
    })

    val formFields: Seq[JField] = parsedBody.filterField(field => newMemberFieldsRequired.contains(field._1) || newMemberFieldsOptional.contains(field._1))
    val data: Map[String, String] = formFields.map(field => (field._1, field._2.asInstanceOf[JString].s)).toMap

    // Send email using mailgun
    val req = dispatch.url(apiUrl).POST.secure
      .as_!("api", apiKey)
      .addParameter("from",     "noreply@era-leijonat.fi")
      .addParameter("to",       newMemberRecipients)
      .addParameter("subject", "Uusi jäsenhakemus lippukunnan nettisivuilla")
      .addParameter("text",     "Uusi jäsen haluaa liittyä lippukuntaan:\n\n" + data.mkString("\n"))

    // Asynchronous HTTP using Dispatch
    dispatch.Http(req OK as.String).onComplete({
      case res => promise.complete(res)
    })

    promise.future
  }
  
}