package models.analysis

import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import org.specs2.mutable.Specification
import org.joda.time.DateTime
import models._

@RunWith(classOf[JUnitRunner])
class SlidingWindowCounterTest extends Specification {

  "SlidingWindowCounter" should {

    "be updated with all events at now" in {
      val swc = new SlidingWindowCounter[String]()
      val now = DateTime.now()
      swc.increment("test", now)
      swc.increment("test", now)
      swc.increment("notest", now)

      swc.top10 must_== List(("test" -> 2), ("notest" -> 1))
    }

    "getTop with events on several hours" in {
      val swc = new SlidingWindowCounter[String]()
      val now = DateTime.now()
      swc.increment("test", now)
      swc.increment("test", now)
      swc.increment("test2", now)
      swc.top10 must_== List("test" -> 2, "test2" -> 1)

      (1 to 24).foreach { i =>
        val date = now.plusHours(i)
        swc.increment("test" , date)
      }

      swc.top10 must_== List("test" -> 24)
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
      swc.top10 must_== List("test" -> delta*2)
    }

    "get top 10" in {
      val swc = SlidingWindowCounter.oneWeek[String]
      val now = DateTime.now()
      val m = collection.mutable.ListBuffer[(String, Long)]()

      (1 to 10).foreach { i =>
        swc.increment("test" + i, now)
        m+= ("test"+i -> 1)
      }
      swc.top10.toMap must_== m.toList.toMap
    }

    "work with users" in {
      val u1 = new User {
        val id = 1L
        val name = "toto"
        val screenName = "toto"
        val profileImageUrl = ""
      }

      val u2 = new User {
        val id = 1L
        val name = "toto"
        val screenName = "toto"
        val profileImageUrl = ""
      }
      val swc = SlidingWindowCounter.oneDay[User]
      val now = DateTime.now()
      println(u2 == u1)

      swc.increment(u1, now)
      swc.increment(u2, now)

      swc.top10.toMap must_== Map(u1 -> 2)
    }
  }

}
