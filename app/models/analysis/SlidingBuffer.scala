package models.analysis

import org.joda.time.{Days, Period, DateTime}
import scala.collection.mutable

/**
 * Created by vince on 23/01/14.
 */
trait WithDate {
  def date: DateTime
}

/**
 * Store objects thar are within the timespan
 * Clears the old objects after each push
 * @param timespan
 * @tparam T
 */
class SlidingBuffer[T <: WithDate](val timespan: Period = Period.days(1)) {

  var lastDate : DateTime = _
  val buffer = mutable.Buffer[T]()

  private def clean() {
    buffer.headOption.map(h =>
      if(lastDate.minus(timespan).isAfter(h.date)) {
        buffer.remove(0)
        clean()
      }
    )
  }

  def push(t: T) {
    buffer += t
    lastDate = t.date
    clean()
  }

  def toList = {
    buffer.toList
  }

}

object SlidingBuffer {

  def oneDay[T <: WithDate] : SlidingBuffer[T] = {
    new SlidingBuffer[T]()
  }

  def oneWeek[T <: WithDate] : SlidingBuffer[T] = {
    new SlidingBuffer[T](Period.weeks(1))
  }

  def apply[T <: WithDate](p : Period) = {
    new SlidingBuffer[T](p)
  }

}
