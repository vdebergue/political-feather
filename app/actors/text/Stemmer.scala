package actors.text

import akka.actor.Actor
import org.tartarus.snowball.ext.FrenchStemmer
import actors.text.Stemmer._
import models.Tweet
import models.text.{Tokenized, Stemmed, WithTweet}

/**
 * Created by vince on 09/01/14.
 */
class Stemmer extends Actor {

  def receive = {
    case Input(tweet, tokens) => sender ! Result(tweet, tokens.map(stem))
  }

  val stemmer = new FrenchStemmer()
  def stem(word: String) : String = {
    stemmer.setCurrent(word)
    if(stemmer.stem()) {
      stemmer.getCurrent()
    } else {
      word
    }
  }
}

object Stemmer {
  case class Input(tweet: Tweet , tokens: List[String]) extends Tokenized with WithTweet
  case class Result(tweet: Tweet, stems: List[String]) extends Stemmed with WithTweet
}
