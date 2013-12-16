package models.analysis

import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import org.specs2.mutable.Specification
import org.joda.time.DateTime

@RunWith(classOf[JUnitRunner])
class SlidingWindowCounterTest extends Specification {

  "SlidingWindowCounter" should {

    "be updated with all events at now" in {
      val swc = new SlidingWindowCounter[String]()
      val now = DateTime.now()
      swc.increment("test", now)
      swc.increment("test", now)
      swc.increment("notest", now)

      swc.getTop10() must_== Map(("test" -> 2), ("notest" -> 1))
    }

    "getTop with events on several hours" in {
      val swc = new SlidingWindowCounter[String]()
      val now = DateTime.now()
      swc.increment("test", now)
      swc.increment("test", now)
      swc.increment("test2", now)
      swc.getTop10() must_== Map("test" -> 2, "test2" -> 1)

      (1 to 24).foreach { i =>
        val date = now.plusHours(i)
        swc.increment("test" , date)
      }

      swc.getTop10() must_== Map("test" -> 24)
    }

    "getTop with one week window" in {
      val swc = SlidingWindowCounter.oneWeek[String]
      val now = DateTime.now()
      val delta = 24*7

      // two increment each hour for two weeks
      (1 to delta*2).foreach { i=>
        val date = now.plusHours(i)
        swc.increment("test", date)
        swc.increment("test", date)
      }

      // value should be of two increments for a week
      swc.getTop10() must_== Map("test" -> delta*2)
    }
  }


}
