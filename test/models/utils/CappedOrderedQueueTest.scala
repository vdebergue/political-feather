package models.utils

import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import org.specs2.mutable.Specification
import utils.CappedOrderedQueue

@RunWith(classOf[JUnitRunner])
class CappedOrderedQueueTest extends Specification {

   "CappedOrderedQueue" should {

     "be able to add elements to it" in {
       val q = CappedOrderedQueue[Int](3)
       q.size must_== 0
       q.add(3)
       q.size must_== 1
       q.add(2)
       q.add(5)
       q.add(1)
       q.add(2)
       q.size must_== 3
       q.toList must_== List(5,3,2)

     }
   }
}


