package actors

import utils.Conf
import play.api.libs.json.{JsValue, Json}
import twitter4j._
import twitter4j.conf.ConfigurationBuilder
import play.api._
import akka.actor._
import play.libs.concurrent.Akka

object TwitterStream {

  val cb = new ConfigurationBuilder()
  cb.setDebugEnabled(true)
    .setOAuthConsumerKey(Conf.consumerKey)
    .setOAuthConsumerSecret(Conf.consumerSecret)
    .setOAuthAccessToken(Conf.accessToken)
    .setOAuthAccessTokenSecret(Conf.accessTokenSecret)

  val twitterStream = new TwitterStreamFactory(cb.build()).getInstance()
  val tweetDispatcher : ActorRef = Akka.system.actorOf(Props[TweetDispatcher], name = "tweetDispatcher")
  
  def start() = {
    val query = new FilterQuery().track(Array("Hollande"))
    val listener = new MyListener
    twitterStream.addListener(listener)
    twitterStream.filter(query)
  }


  def stop() = {
    twitterStream.shutdown()
  }
}

class MyListener extends StatusListener {

  override def onStatus(status : Status) {
    tweetDispatcher ! status
  }

  override def onDeletionNotice(notice: StatusDeletionNotice): Unit = {}
  override def onScrubGeo(userId: Long,upToStatusId: Long): Unit = {}
  override def onStallWarning(warning: StallWarning): Unit = {}
  override def onTrackLimitationNotice(numberOfLimitedStatuses: Int): Unit = {}
  override def onException(ex: Exception): Unit = {
    Logger.error("Got error" + ex.getMessage())
  }
}

