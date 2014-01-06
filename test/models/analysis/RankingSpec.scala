package models.analysis

import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import org.specs2.mutable.Specification

@RunWith(classOf[JUnitRunner])
class RankingSpec extends Specification {

  "Ranking" should {

    "be able to get a top 2" in {
      val r = Ranking[String](2)
      r.add("test1", 2)
      r.add("test2", 4)
      r.add("test3", 5)
      r.add("test4", 1)

      r.getTop() must_== List("test3" -> 5, "test2" -> 4)
    }

    "be able to get a top with all at 1" in {
      val r = Ranking[String](10)
      r.add("test1",1)
      r.add("test2",1)
      r.add("test3",1)
      r.add("test4",1)

      r.getTop() must_== List("test4" -> 1, "test3" -> 1, "test2" -> 1, "test1" -> 1)
    }
  }
}
