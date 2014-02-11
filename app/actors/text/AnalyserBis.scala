package actors.text

import akka.actor.Actor
import models.Tweet
import models.text.{AnalysedBis, WithTweet, Stemmed}
import AnalyserBis._
import utils.LearningAnalyzer

/**
 * Created by vince on 03/02/14.
 */
class AnalyserBis extends Actor {

  def receive = {
    case Input(tweet, stems) =>
      val scores = LearningAnalyzer.analyse(stems)
      sender ! Result(tweet, scores._1, scores._2)
  }
}

object AnalyserBis {
  case class Input(tweet: Tweet, stems: List[String]) extends Stemmed with WithTweet
  case class Result(tweet: Tweet, positive: Double, negative: Double) extends AnalysedBis with WithTweet
}
