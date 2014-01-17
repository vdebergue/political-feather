package actors.analysis

import akka.actor.{ActorLogging, Actor}
import models.Tweet
import models.analysis.SlidingWindowCounter
import actors.TweetDispatcher
import play.api.libs.json.{JsValue, Writes, Json}
import actors.utils.Restorable
import scala.util.Marshal
import java.io.FileOutputStream

class MostUsedHastag extends Actor with Restorable with ActorLogging {

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
      save()
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

  def save() = {
    log.info("Saving hashtags ...")
    val out = new FileOutputStream(file)
    val bytes = Marshal.dump(hastagsCount)
    out.write(bytes)
    out.close()
  }

  def restore() = {
    println("Restoring...")
    val bytesOption = getFromFile()
    bytesOption.map { bytes =>
      hastagsCount = Marshal.load[SlidingWindowCounter[String]](bytes)
      println("Restored ! " + top10.length)
    }
  }
}
