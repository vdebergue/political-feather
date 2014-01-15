package models.utils

import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import play.api.test.WithApplication
import utils.{Dictionary, TextAnalysis}

@RunWith(classOf[JUnitRunner])
class TextAnalysisTest extends Specification {

  "TextAnalysis" should {

    "read the right file" in new WithApplication{
      val (categories, words) = Dictionary.readFile
      categories must have size(64)
      categories(129) mustEqual "colère"
    }

    "classify words and phrases correctly" in new WithApplication {
      TextAnalysis.classifyWord("abandon") must  beSome.which(_== Seq(125,127,130,131,137,355))
      TextAnalysis.classifyWord("localisation") must  beSome.which(_== Seq(250,252))
      TextAnalysis.classifyWord("xcvbn") must beNone

      val cat = TextAnalysis.classifyPhrase("Nous avons un ensemble de localisation à abandonner")
      cat must have size(10)
      cat.maxBy(_._2)._1 mustEqual 252
    }

    "calculate the sentiment of a phrase" in new WithApplication() {
      val in = Map(123 -> 5, 126 -> 2, 19 -> 2, 128 -> 5, 130 -> 5)
      val wordCount = 21
      TextAnalysis.sentiment(in, 21) mustEqual (0.33, 0.57)

    }

  }


}
