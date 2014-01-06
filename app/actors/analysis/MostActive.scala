package actors.analysis

import akka.actor.Actor
import models.{User, Tweet}
import models.analysis.{Ranking, SlidingWindowCounter}
import actors.TweetDispatcher
import play.api.libs.json._
import models.JsonFormat._

class MostActive extends Actor {

  val usersCount = SlidingWindowCounter.oneDay[User]

  def receive = {
    case tweet : Tweet =>
      usersCount.increment(tweet.user, tweet.createdAt)
    case TweetDispatcher.Tick =>
      val json = Json.toJson(mostActiveTop10)
      TweetDispatcher.inMostActive.push(json)

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

}
