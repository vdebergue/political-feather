package models.analysis

import scala.math.Ordering.Implicits._

trait Ranking[T] {
  def add(t: T, count: Long) : Unit
  def getTop() : Map[T, Long]
}

class RankingImpl[T](numberOfElements: Int) extends Ranking[T]{

  val tree = collection.mutable.TreeSet[(T, Long)]()
  var size = 0

  def add(t : T, count: Long) {
    if(size < numberOfElements) {
      tree.add(t -> count)
      size += 1
    } else {
      // get the min element
      val min = tree.minBy(_._2)
      if(min._2 < count) {
        tree.remove(min)
        tree.add(t -> count)
        size += 1
      }
    }
  }

  def getTop() : Map[T, Long] = {
    tree.toMap
  }
}

class RankingOne[T] extends Ranking[T] {
  var elem : Option[T] = None
  var c : Long = -1

  def add(t: T, count: Long) {
    if(count > c) {
      elem = Some(t)
      c = count
    }
  }

  def getTop() : Map[T, Long] = {
    Map(elem.get -> c)
  }
}

object Ranking {

  def apply[T](n: Int) : Ranking[T] = {
    if(n == 1) new RankingOne[T]
    else new RankingImpl[T](n)
  }
}
