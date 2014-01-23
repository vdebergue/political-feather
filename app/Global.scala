import akka.actor.{Props, ActorRef}
import play.api._
import actors.{MongoStream, TweetStream, TweetDispatcher, TwitterStream}
import play.api.libs.concurrent.Akka
import play.api.Play.current

object Global extends GlobalSettings {

  var stream : TweetStream = null

  override def onStart(app: Application) {
    Logger.info("Starting twitter actor")
    if(!Play.isTest) {
      val tweetDispatcher : ActorRef = Akka.system.actorOf(Props[TweetDispatcher], name = "tweetDispatcher")
      //stream = new TwitterStream(tweetDispatcher)
      stream = new MongoStream(tweetDispatcher)
      stream.start()
    }
  }

  override def onStop(app: Application) {
    Logger.info("Application shutdown...")
    if(!Play.isTest) {
      stream.stop()
    }
  }

}