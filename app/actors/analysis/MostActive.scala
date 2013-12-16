package actors.analysis

import akka.actor.Actor
import models.{User, Tweet}
import models.analysis.{Ranking, SlidingWindowCounter}

class MostActive extends Actor {

  val usersCount = SlidingWindowCounter.oneDay[User]

  def receive = {
    case tweet : Tweet =>
      usersCount.increment(tweet.user, tweet.createdAt)

    //println("MA: " + mostActive)
  }

  def mostActive : (User, Long) = {
    usersCount.top1
  }

  def mostActiveTop10 = {
    usersCount.top10
  }

}
