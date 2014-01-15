package actors.text

import akka.actor.Actor
import models.Tweet
import models.text.{WithTweet, Analysed, Stemmed}
import Analyser._
import utils.TextAnalysis

class Analyser extends Actor{

  def receive = {
    case Input(tweet, stems) =>
      val (pos, neg, cat) = TextAnalysis.analyse(stems)
      val categoriesWithName: Map[String, Int] = cat.map( CatCount => TextAnalysis.getCategoryName(CatCount._1) -> CatCount._2)
      sender ! Result(tweet, pos, neg, categoriesWithName)
  }
}

object Analyser {
  case class Input(tweet: Tweet, stems: List[String]) extends Stemmed with WithTweet
  case class Result(tweet: Tweet, positive: Double, negative: Double, categories: Map[String, Int]) extends Analysed with WithTweet
}
