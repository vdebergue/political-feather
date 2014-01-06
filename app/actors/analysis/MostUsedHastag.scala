package actors.analysis

import akka.actor.Actor
import models.Tweet
import models.analysis.SlidingWindowCounter
import actors.TweetDispatcher
import play.api.libs.json.{JsValue, Writes, Json}

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

  def top10 : List[(String, Long)] = {
    hastagsCount.top10
  }

  implicit val topWrites: Writes[(String, Long)] = new Writes[(String, Long)] {
    def writes(o: (String, Long)): JsValue = Json.obj(
      "hashtag" -> o._1,
      "count" -> o._2
    )
  }

}
