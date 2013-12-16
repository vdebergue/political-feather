package models.analysis

import org.joda.time.{Period, DateTime}

/**
 *
 */
class SlidingWindowCounter[T](val timespan: Period = Period.days(1)) {

  var map : Map[T, Array[Long]]
  var currentPosition = 0

  def increment(t : T, date: DateTime) = {

  }

  private def getTop(topN: Ranking[T]) : Map[T, Long] = {
    map.foreach{ tuple =>
      // get the sliding count TODO
      val count = 0
      topN.add(tuple._1, count)
    }
    topN.getTop()
  }

  def getTop10() = {
    val r10 = Ranking[T](10)
    getTop(r10)
  }
}

object SlidingWindowCounter {

  def apply() = {

  }
}
