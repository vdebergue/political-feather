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


  def increment(t : T, date: DateTime, times : Int) {
    val hour = date.getHourOfDay()
    if (currentHour != hour && currentHour != -1) {
      currentHour = hour
      advancePosition()
    } else if(currentHour == -1) {
      currentHour = hour
    }
    map.get(t) match {
      case Some(arr) => arr(currentPosition) = arr(currentPosition) + times
      case None => {
        val arr = new Array[Long](numberOfSlots)
        arr(currentPosition) = times
        map += (t -> arr)
      }
    }
  }

  def increment(t : T, date: DateTime) {
    increment(t,date,1)
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

  def getTop(topN: Ranking[T]): List[(T, Long)] = {
    map.foreach{
      case (t, arr: Array[Long]) =>
        val count = arr.sum
        topN.add(t, count)
    }
    topN.getTop()
  }

  def top10: List[(T, Long)] = {
    val r10 = Ranking[T](10)
    getTop(r10)
  }

  def top1: (T, Long) = {
    val r1 = Ranking[T](1)
    getTop(r1)
    r1.getMax()
  }
}

object SlidingWindowCounter {

  def oneDay[T] : SlidingWindowCounter[T] = {
    new SlidingWindowCounter[T]()
  }

  def oneWeek[T] : SlidingWindowCounter[T] = {
    new SlidingWindowCounter[T](Period.weeks(1))
  }

  def apply[T](p : Period) = {
    new SlidingWindowCounter[T](p)
  }
}
