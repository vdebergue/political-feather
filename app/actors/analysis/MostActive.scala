package actors.analysis

import akka.actor.Actor
import models.{User, Tweet}
import models.analysis.{Ranking, SlidingWindowCounter}
import actors.TweetDispatcher
import play.api.libs.json._
import models.JsonFormat._
import actors.utils.Restorable
import scala.util.Marshal
import java.io.FileOutputStream

class MostActive extends Actor with Restorable {

  var usersCount = SlidingWindowCounter.oneDay[User]
  val file = "saved/users.bin"

  def receive = {
    case tweet : Tweet =>
      usersCount.increment(tweet.user, tweet.createdAt)
    case TweetDispatcher.Tick =>
      val json = Json.toJson(mostActiveTop10)
      TweetDispatcher.inMostActive.push(json)
    case TweetDispatcher.Save =>
      save()
    case TweetDispatcher.Restore =>
      restore()

  }

  def mostActive : (User, Long) = {
    usersCount.top1
  }

  def mostActiveTop10: List[(User, Long)] = {
    usersCount.top10
  }

  implicit val topWrites: Writes[(User, Long)] = new Writes[(User, Long)] {
    def writes(o: (User, Long)): JsValue = Json.obj(
      "user" -> Json.toJson(o._1),
      "count" -> o._2
    )
  }

  def restore() = {
    val bytes = getFromFile()
    bytes.map { b =>
      usersCount = Marshal.load[SlidingWindowCounter[User]](b)
    }
  }

  def save() = {
    val out = new FileOutputStream(file)
    val bytes = Marshal.dump(usersCount)
    out.write(bytes)
    out.close()
  }

}
