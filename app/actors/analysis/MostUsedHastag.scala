package actors.analysis

import akka.actor.Actor
import models.Tweet
import models.analysis.SlidingWindowCounter
import actors.TweetDispatcher
import play.api.libs.json.{JsValue, Writes, Json}
import actors.utils.Restorable
import scala.pickling._
import binary._

class MostUsedHastag extends Actor with Restorable {

  var hastagsCount = SlidingWindowCounter.oneDay[String]
  val file = "saved/hash.bin"

  def receive = {
    case tweet : Tweet =>
      tweet.hashtags.foreach{ h =>
        hastagsCount.increment(h, tweet.createdAt)
      }
    case TweetDispatcher.Tick =>
      val json = Json.toJson(top10)
      TweetDispatcher.inHashTags.push(json)
    case TweetDispatcher.Save =>
      save(hastagsCount)
    case TweetDispatcher.Restore =>
      restore()
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

  def restore() = {
    val bytesOption = getFromFile()
    bytesOption.map { bytes =>
      hastagsCount = bytes.unpickle[SlidingWindowCounter[String]]
    }
  }
}
