package utils

import play.api.Play.current

object Conf {
  def getKey(key: String) : String = {
    current.configuration.getString(key).getOrElse(throw new Exception(s"Could not find $key"))
  }

  lazy val consumerKey = Conf.getKey("twitter.consumer.key")
  lazy val consumerSecret = Conf.getKey("twitter.consumer.secret")
  lazy val accessToken = Conf.getKey("twitter.client.accessToken")
  lazy val accessTokenSecret = Conf.getKey("twitter.client.accessTokenSecret")
  lazy val twitterStreamUrl = Conf.getKey("twitter.url")
}
