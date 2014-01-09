package actors.analysis

import akka.actor.Actor
import org.joda.time.DateTime
import actors.analysis.WordUsage._
import models.analysis.SlidingWindowCounter
import actors.TweetDispatcher
import play.api.libs.json.Json

/**
 * Created by vince on 09/01/14.
 */
class WordUsage extends Actor {

  val dates = collection.mutable.Map[String, List[DateTime]]()
  val count = SlidingWindowCounter.oneDay[String]

  def receive = {
    case Words(words, date) => for(w <- words) {
      count.increment(w, date)
      val list = dates.getOrElse(w, Nil)
      dates.update(w, date :: list)
    }

    case TweetDispatcher.Tick =>
      val json = Json.toJson(top10Words)
      TweetDispatcher.inWordUsage.push(json)
      cleanDates()
  }

  def top10Words : List[(String, List[DateTime])] = {
    val now = DateTime.now()
    count.top10.map(t => (t._1 -> dates.getOrElse(t._1, Nil).filter(datesAreSameDay(now, _))))
  }

  def datesAreSameDay(date1: DateTime, date2: DateTime): Boolean = {
    val diff = math.abs(date1.getMillis() - date2.getMillis())
    diff < 24 * 3600 * 1000
  }

  def cleanDates() {
    val now = DateTime.now()
    dates.foreach(t => dates.update(t._1, t._2.filter(datesAreSameDay(now, _))))
  }
}

object WordUsage {
  case class Words(words: Seq[String], date: DateTime)
}
