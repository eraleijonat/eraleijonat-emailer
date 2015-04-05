package fi.eraleijonat.emailer

import _root_.akka.actor.ActorSystem
import dispatch.{Future, as}
import org.json4s.JsonAST.{JObject, JString}
import org.json4s.{DefaultFormats, Formats, JField}
import org.scalatra._
import org.scalatra.json.JacksonJsonSupport

import scala.concurrent.{ExecutionContext, Promise}
import scala.util.Try

class EmailerServlet extends ScalatraServlet with FutureSupport with CorsSupport with JacksonJsonSupport {

  protected implicit def executor: ExecutionContext = ActorSystem("actors").dispatcher
  protected implicit val jsonFormats: Formats = DefaultFormats

  val apiKey = System.getenv("mailgun_api_key")
  val apiLogin = System.getenv("mailgun_api_login")
  val newMemberRecipients = System.getenv("new_member_recipients")

  requireEnvsPresent()

  val apiUrl = "https://api.mailgun.net/v2/" + apiLogin + "/messages"

  options("/*"){
    response.setHeader("Access-Control-Allow-Headers", request.getHeader("Access-Control-Request-Headers"))
  }

  def requireEnvsPresent(): Unit = {
    Seq(apiKey, apiLogin, newMemberRecipients).foreach(
      env ⇒ require(env != null && env.nonEmpty)
    )
  }

  post("/ping") {
    contentType = "application/json"
    Map("ping" → "pong")
  }

  post("/new-member") {
    new AsyncResult() {
      val is = sendNewMemberMail()
    }
  }

  def sendNewMemberMail(): Future[JObject] = {
    val promise = Promise[JObject]()

    // Halt if a required field is not present in parameters.
    JoinForm.requiredFields.foreach(f ⇒ {
      if ((parsedBody \ f.id).asInstanceOf[JString].s.isEmpty) {
        halt(
          status = 400,
          body   = JObject(JField("fail", JString("Pakollinen kenttä " + f.name + " on tyhjä!")))
        )
      }
    })

    val validFields: Seq[JField] = parsedBody.filterField(f ⇒ JoinForm.fieldNames.contains(f._1))
    val data: Map[String, String] = validFields.map(f ⇒ (JoinForm.fieldById(f._1).name, f._2.asInstanceOf[JString].s)).toMap

    // Send email using mailgun
    val req = dispatch.url(apiUrl).POST.secure
      .as_!("api", apiKey)
      .addParameter("from", "noreply@era-leijonat.fi")
      .addParameter("to", newMemberRecipients)
      .addParameter("subject", "Uusi jäsenhakemus lippukunnan nettisivuilla")
      .addParameter("text", "Uusi jäsen haluaa liittyä lippukuntaan:\n\n" + data.mkString("\n"))

    // Asynchronous HTTP using Dispatch
    val result: Future[String] = dispatch.Http(req OK as.String)

    result.onSuccess({
      case res ⇒ promise.complete(Try(JObject(JField("success", JString("Kiitos, ja tervetuloa partioon! :) Hakemuksesi on lähetetty jäsenrekisterin hoitajalle.")))))
    })
    result.onFailure({
      case res ⇒ promise.complete(Try(JObject(JField("fail", JString(res.toString)))))
    })

    promise.future
  }
  
}