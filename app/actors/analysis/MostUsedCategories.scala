package actors.analysis

import akka.actor.Actor
import org.joda.time.DateTime
import MostUsedCategories._
import models.analysis.SlidingWindowCounter
import actors.TweetDispatcher
import play.api.libs.json.{Json, JsValue, Writes}

class MostUsedCategories extends Actor {

  val categoriesCount = SlidingWindowCounter.oneDay[String]

  def receive = {
    case Input(categories, date) => categories.foreach{ catCount =>
      categoriesCount.increment(catCount._1, date, catCount._2)
    }
    case TweetDispatcher.Tick =>
      TweetDispatcher.inCategories.push(Json.toJson(categoriesCount.top10))
  }

  implicit val topWrites: Writes[List[(String, Long)]] = new Writes[List[(String, Long)]] {
    def writes(o: List[(String, Long)]): JsValue =
      Json.toJson(o.map(t => Json.obj(
        "categorie" -> t._1,
        "count" -> t._2
      )))
  }
}

object MostUsedCategories {
  case class Input(categories: Map[String, Int], date: DateTime)
}
