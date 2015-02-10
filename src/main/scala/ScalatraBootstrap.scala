import javax.servlet.ServletContext

import fi.eraleijonat.emailer._
import org.scalatra._

class ScalatraBootstrap extends LifeCycle {

  override def init(context: ServletContext) {
    context.mount(new EmailerServlet, "/*")
  }

}
