package actors.analysis

import akka.actor.Actor
import models.Tweet

class MostUsedHastag extends Actor {

  val hastagsCount = collection.mutable.Map[String, Int]()

  def receive = {
    case tweet : Tweet =>
      tweet.hashtags.foreach{ h =>
        hastagsCount.get(h) match {
          case Some(count) => hastagsCount.update(h, count + 1)
          case None => hastagsCount += (h -> 1)
        }
      }
      println("Most Used Hastag: " + mostUsed)

  }

  def mostUsed : (String, Int) = {
    hastagsCount.maxBy(_._2)
  }

}
