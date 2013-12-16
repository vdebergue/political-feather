package models.analysis

import org.joda.time.{Period, DateTime}

/**
 * Allow to count events on a sliding window and get a top on this window
 *
 * <b>This implementation divides the time by hour and needs at least an event every hour </b>
 */
class SlidingWindowCounter[T](val timespan: Period = Period.days(1)) {

  private val map = collection.mutable.Map[T, Array[Long]]()
  private var currentPosition = 0
  private var currentHour = -1
  // One slot per hour
  private val numberOfSlots = timespan.toStandardHours().getHours()


  def increment(t : T, date: DateTime) {
    val hour = date.getHourOfDay()
    if (currentHour != hour) {
      currentHour = hour
      advancePosition()
    }
    map.get(t) match {
      case Some(arr) => arr(currentPosition) = arr(currentPosition) + 1
      case None => {
        val arr = new Array[Long](numberOfSlots)
        arr(currentPosition) = 1
        map += (t -> arr)
      }
    }
  }

  private def advancePosition() {
    currentPosition = (currentPosition + 1) % numberOfSlots
    map.foreach{
      case (t, arr) => arr(currentPosition) = 0
    }
    clean()
  }

  // Remove the elements if the array is all zero
  private def clean() {
    val elementstoRemove = map.filter {
      case (t, arr: Array[Long]) => arr.sum == 0
    }.keys
    elementstoRemove.foreach(map.remove(_))
  }

  def getTop(topN: Ranking[T]): Map[T, Long] = {
    map.foreach{
      case (t, arr: Array[Long]) =>
        val count = arr.sum
        topN.add(t, count)
    }
    topN.getTop()
  }

  def getTop10(): Map[T, Long] = {
    val r10 = Ranking[T](10)
    getTop(r10)
  }
}

object SlidingWindowCounter {

  def oneDay[T] : SlidingWindowCounter[T] = {
    new SlidingWindowCounter[T]()
  }

  def oneWeek[T] : SlidingWindowCounter[T] = {
    new SlidingWindowCounter[T](Period.weeks(1))
  }
}