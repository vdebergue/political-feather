package utils

import play.api.Play.current
import play.api.libs.oauth.{RequestToken, ConsumerKey}

object Conf {
  def getKey(key: String) : String = {
    current.configuration.getString(key).getOrElse(throw new Exception(s"Could not find $key"))
  }

  lazy val consumerKey = ConsumerKey(Conf.getKey("twitter.consumer.key"), Conf.getKey("twitter.consumer.secret"))
  lazy val accessToken = RequestToken(Conf.getKey("twitter.client.accessToken"), Conf.getKey("twitter.client.accessTokenSecret"))
  lazy val twitterStreamUrl = Conf.getKey("twitter.url")
}
