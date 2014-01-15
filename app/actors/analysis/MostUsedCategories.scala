package actors.analysis

import akka.actor.Actor
import org.joda.time.DateTime
import MostUsedCategories._
import models.analysis.SlidingWindowCounter
import actors.TweetDispatcher

class MostUsedCategories extends Actor {

  val categoriesCount = SlidingWindowCounter.oneDay[String]

  def receive = {
    case Input(categories, date) => categories.foreach{ catCount =>
      categoriesCount.increment(catCount._1, date, catCount._2)
    }
    case TweetDispatcher.Tick =>
      //TODO

  }

}

object MostUsedCategories {
  case class Input(categories: Map[String, Int], date: DateTime)
}
