package utils

import scala.collection.mutable.ArrayBuffer

/**
 * Class to store a maximum number of ordered values
 * The bigger value will be stored first
 */
trait CappedOrderedQueue[T] {

    def add(e: T): Unit
    def remove(e: T): Unit
    def min: T
    def max: T
    def size : Int
    def toList: List[T]
}


class CappedOrderedQueueImpl[T](val maxSize: Int)(implicit order: Ordering[T]) extends CappedOrderedQueue[T] {
    
    private val buffer = new ArrayBuffer[T](maxSize)
    var size = 0

    def min : T = buffer(0)

    def max : T = {
        if (size == maxSize) buffer(maxSize -1)
        else buffer(size - 1)
    }

    def add(e: T) {
      // si size = 0, insert à 0
      if (size == 0) {
        buffer.insert(0, e)
      } else {
        var inserted = false
        // pour i de 0 à size -1
        for(i <- 0 until size; if !inserted) {
          // comparer e à buffer(i)
          if (order.gteq(e, buffer(i))) {
            // si plus grand, insérer à la place i et le reste "se décale" (virer les éléments plus grands ?)
            buffer.insert(i, e)
            inserted = true
          }
        }
        if( !inserted && size < maxSize) {
          // S'il reste de la place, insérer à la fin
          buffer.insert(size, e)
          inserted = true
        }
      }
      size = math.min(size + 1, maxSize)
    }

    def remove(e: T) {
        val index = buffer.indexOf(e)
        if (index >= 0) {
          buffer.remove(index)
          size -= 1
        }
    }

    def toList : List[T] = {
      buffer.toList.slice(0, size)
    }
}

object CappedOrderedQueue {

  def apply[T](n: Int)(implicit order: Ordering[T]) : CappedOrderedQueue[T] = {
    new CappedOrderedQueueImpl[T](n)(order)
  }
}