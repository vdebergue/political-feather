import play.api._
import actors.TwitterActor
import play.api.libs.concurrent.Akka
import play.api.Play.current
import akka.actor._

object Global extends GlobalSettings {

  override def onStart(app: Application) {
    Logger.info("Starting twitter actor")
    val actor = Akka.system.actorOf(Props[TwitterActor], name = "twitterActor")
  }

  override def onStop(app: Application) {
    Logger.info("Application shutdown...")
  }

}