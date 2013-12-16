package actors.analysis

import akka.actor.Actor
import models.Tweet
import models.analysis.SlidingWindowCounter

class MostUsedHastag extends Actor {

  val hastagsCount = SlidingWindowCounter.oneDay[String]

  def receive = {
    case tweet : Tweet =>
      tweet.hashtags.foreach{ h =>
        hastagsCount.increment(h, tweet.createdAt)
      }

  }

  def mostUsed : (String, Long) = {
    hastagsCount.top1
  }

  def top10 = {
    hastagsCount.top10
  }

}
