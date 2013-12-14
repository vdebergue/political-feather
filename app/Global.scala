import play.api._
import actors.TwitterStream
import play.api.libs.concurrent.Akka
import play.api.Play.current

object Global extends GlobalSettings {

  override def onStart(app: Application) {
    Logger.info("Starting twitter actor")
    TwitterStream.start()
  }

  override def onStop(app: Application) {
    Logger.info("Application shutdown...")
    TwitterStream.stop()
  }

}