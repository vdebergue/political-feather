package actors.analysis

import akka.actor.Actor
import models.Tweet
import models.analysis.SlidingWindowCounter
import actors.TweetDispatcher
import play.api.libs.json.Json

class MostUsedHastag extends Actor {

  val hastagsCount = SlidingWindowCounter.oneDay[String]

  def receive = {
    case tweet : Tweet =>
      tweet.hashtags.foreach{ h =>
        hastagsCount.increment(h, tweet.createdAt)
      }
    case TweetDispatcher.Tick =>
      val json = Json.toJson(top10)
      TweetDispatcher.inHashTags.push(json)
  }

  def mostUsed : (String, Long) = {
    hastagsCount.top1
  }

  def top10 = {
    hastagsCount.top10
  }

}
