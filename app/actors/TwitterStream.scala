package actors

import utils.Conf
import twitter4j._
import twitter4j.conf.ConfigurationBuilder
import play.api._
import akka.actor._
import play.api.libs.concurrent.Akka
import play.api.Play.current
import models.TweetAdapter

trait TweetStream {
  def start() : Unit
  def stop() : Unit
}

class TwitterStream(tweetDispatcher: ActorRef) extends TweetStream {

  val cb = new ConfigurationBuilder()
  cb.setDebugEnabled(true)
    .setOAuthConsumerKey(Conf.consumerKey)
    .setOAuthConsumerSecret(Conf.consumerSecret)
    .setOAuthAccessToken(Conf.accessToken)
    .setOAuthAccessTokenSecret(Conf.accessTokenSecret)

  val twitterStream = new TwitterStreamFactory(cb.build()).getInstance()

  def start() = {
    val query = new FilterQuery().follow(Conf.ids.toArray)
    val listener = new MyListener(tweetDispatcher)
    twitterStream.addListener(listener)
    twitterStream.filter(query)
  }


  def stop() = {
    twitterStream.shutdown()
  }
}

class MyListener(tweetDispatcher : ActorRef) extends StatusListener {

  override def onStatus(status : Status) {
    tweetDispatcher ! new TweetAdapter(status)
  }

  override def onDeletionNotice(notice: StatusDeletionNotice): Unit = {}
  override def onScrubGeo(userId: Long,upToStatusId: Long): Unit = {}
  override def onStallWarning(warning: StallWarning): Unit = {}
  override def onTrackLimitationNotice(numberOfLimitedStatuses: Int): Unit = {}
  override def onException(ex: Exception): Unit = {
    Logger.error("Got error " + ex.getMessage())
  }
}

