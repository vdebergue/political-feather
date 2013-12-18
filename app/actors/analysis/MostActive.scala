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
      // TODO
      //val json = Json.toJson(mostActiveTop10)
      //TweetDispatcher.inMostActive.push(json)

  }

  def mostActive : (User, Long) = {
    usersCount.top1
  }

  def mostActiveTop10: Map[User, Long] = {
    usersCount.top10
  }

}
