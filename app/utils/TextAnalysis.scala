package utils

import scala.io.Source
import scala.collection.mutable

object TextAnalysis {
  lazy val (categories, wordsDic) = Dictionary.readFile

  val nonMeaningfullCategories = Seq(1,2,3,9,10,11,12,14,15,16,17,18,20,21)
  val positiveCategories = Seq(123, 126)
  val negativeCategories = Seq(19, 22, 127, 128, 129, 130, 360)

  def classifyWord(word: String): Option[Seq[Int]] = {
    for((wordRoot: String, cat) <- wordsDic) {
      if(word.matches(wordRoot.replace("*", ".*"))) return Some(cat)
    }
    None
  }

  def classifyPhrase(phrase: String) = {
    val words = phrase.split(' ').toList
    classify(words)
  }

  def classify(words: List[String]) = {
    var classes = mutable.Buffer[Int]()
    for( word <- words) {
      classifyWord(word.toLowerCase).map(s => classes ++= s)
    }
    classes.groupBy(identity).mapValues(_.size) -- nonMeaningfullCategories
  }

  def sentiment(categories: Map[Int, Int], wordCount: Int) : (Double, Double) = {
    val (positive, negative) = categories.foldLeft((0.0,0.0))((scores, catTuple) =>
      if(positiveCategories.contains(catTuple._1)) (scores._1 + catTuple._2, scores._2 )
      else if (negativeCategories.contains(catTuple._1)) (scores._1, scores._2 + catTuple._2)
      else scores
    )
    (round2(positive/wordCount), round2(negative/wordCount))
  }

  def getCategoryName(category: Int) : String = {
    categories(category)
  }

  def analyse(stems: List[String]) : (Double, Double, Map[Int, Int]) = {
    val wordCount = stems.length
    val categories = classify(stems)
    val (pos, neg) = sentiment(categories, wordCount)
    (pos, neg, categories)
  }

  private def round2(x: Double) : Double = {
    (math.round(x * 100)) / 100.0
  }
}

object Dictionary {

  def readFile : (Map[Int, String], Map[String, Seq[Int]]) = {
    val file = Source.fromFile(Conf.getKey("dictionary.file"))
    val categories = collection.mutable.Map[Int, String]()
    val words = collection.mutable.Map[String, Seq[Int]]()

    var mode = 0
    for(line <- file.getLines()) {
      val splitted = line.split('\t')
      splitted.length match {
        case 1 => mode += 1
        case 2 => {
          if(mode == 1) categories += (splitted(0).toInt -> splitted(1)) else words += (splitted(0) -> splitted.tail.toSeq.map(_.toInt))
        }
        case _ => {
          words += (splitted(0) -> splitted.tail.toSeq.map(_.toInt))
        }
      }
    }
    (categories.toMap, words.toMap)
  }
}
