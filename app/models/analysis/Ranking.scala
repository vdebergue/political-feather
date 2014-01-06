package models.analysis

trait Ranking[T] {
  def add(t: T, count: Long) : Unit
  def getTop() : Map[T, Long]
  def getMax() : (T, Long)
}

class RankingImpl[T](numberOfElements: Int) extends Ranking[T]{

  val queue = utils.CappedOrderedQueue[(T, Long)](numberOfElements)(Ordering.by[(T, Long), Long](_._2))

  def add(t : T, count: Long) {
    queue.add(t -> count)
  }

  def getTop() : Map[T, Long] = {
    queue.toList.toMap
  }

  def getMax() = {
    queue.max
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

  def getMax() : (T, Long) = {
    (elem.get -> c)
  }

}

object Ranking {

  def apply[T](n: Int) : Ranking[T] = {
    if(n == 1) new RankingOne[T]
    else new RankingImpl[T](n)
  }
}
