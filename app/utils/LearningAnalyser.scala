package utils

import collection.mutable.Map
import collection.mutable.Set
import scala.collection.immutable

object LearningAnalyzer {

  lazy val map = readFile()
  val unknown = Set.empty[String]
  val fileName = "public/myDic.csv"

  def update(word: String, score: Int) {
    map += (word -> score)
  }

  def getUnknowns : immutable.Set[String] = unknown.toSet

  def analyse(words: Seq[String]) : (Double, Double) = {
    words.foldLeft((0.0, 0.0)){ (scores, word) =>
      map.get(word) match {
        case Some(s) =>
          if (s >= 0) {
            (scores._1 + s, scores._2)
          } else {
            (scores._1, scores._2 + s)
          }
        case None =>
          unknown += word
          scores
      }
    }
  }

  def saveToFile() {
    val file = new java.io.File(fileName)
    val p = new java.io.PrintWriter(file)
    map.foreach{
      case (word, score) => p.println(word + "," + score)
    }
    p.close()
  }

  def readFile() : Map[String, Int] = {
    val m = Map[String, Int]()
    val file = io.Source.fromFile(fileName)
    for (line <- file.getLines()) {
      val parts = line.split(',')
      m += (parts(0) -> parts(1).toInt)
    }
    m
  }
}
