package actors.text

import akka.actor.Actor
import org.tartarus.snowball.ext.FrenchStemmer
import actors.text.Stemmer._

/**
 * Created by vince on 09/01/14.
 */
class Stemmer extends Actor {

  def receive = {
    case Input(words) => sender ! Result(words.map(stem))
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
  case class Input(words: List[String])
  case class Result(stemmedWords: List[String])
}
