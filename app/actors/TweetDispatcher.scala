package actors

import akka.actor._
import twitter4j._
import models.Tweet
import actors.analysis.{MostUsedHastag, MostActive}

class TweetDispatcher extends Actor with ActorLogging {

  var received = 0
  
  val mongoStore = context.actorOf(Props[MongoStoreActor], name = "mongoStore")
  val mostActive = context.actorOf(Props[MostActive], "mostActive")
  val mostUsedHashtags = context.actorOf(Props[MostUsedHastag], "mostUsedHashtags")
  
  def receive = {
    case tweet : Tweet =>
      received += 1
      // if we want to store the tweet in mongo
      // mongoStore ! tweet

      // mostActive ! tweet
      // mostUsedHashtags ! tweet
      //log.info(s"Received $received tweets so far ...")
  }

}