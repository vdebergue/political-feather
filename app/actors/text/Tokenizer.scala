package actors.text

import akka.actor.Actor
import actors.text.Tokenizer.Result
import models.Tweet
import models.text.{WithTweet, Tokenized}

/**
 * Created by vince on 09/01/14.
 */
class Tokenizer extends Actor {

  def receive = {
    case tweet : Tweet => {
      sender ! Result(tweet, tokenize(tweet.text))
    }
  }

  def tokenize(text: String): List[String] = {
    val lowered = text.replaceAll("#", "").replaceAll("'", " ").toLowerCase()
    val userNameRemoved = Tokenizer.userNameRegex replaceAllIn(lowered, "")
    userNameRemoved.split(' ')
      .map{word : String => word.filter(_.isLetterOrDigit) }
      .filter(_.length > 1)
      .filterNot(isStopWord).toList
  }


  def isStopWord(word: String) : Boolean = {
    stopWords.contains(word.trim)
  }

  val stopWords = List("au", "aux", "avec", "ce", "ces", "dans", "de", "des", "du",
    "elle", "en", "et", "eux", "il", "je", "la", "le", "leur", "lui", "ma", "mais",
    "me", "même", "mes", "moi", "mon", "ne", "nos", "notre", "nous", "on", "ou", "par",
    "pas", "pour", "qu", "que", "qui", "sa", "ses", "son", "sur", "ta", "te", "tes",
    "toi", "ton", "tu", "un", "une", "vos", "votre", "vous", "c", "d", "j", "l", "à",
    "m", "n", "s", "t", "y", "été", "étée", "étées", "étés", "étant",
    "suis", "es", "est", "sommes", "êtes", "sont", "serai", "seras", "sera", "serons", "serez", "seront", 
    "serais", "serait", "serions", "seriez", "seraient", "étais", "était", "étions", 
    "étiez", "étaient", "fus", "fut", "fûmes", "fûtes", "furent", "sois", "soit", 
    "soyons", "soyez", "soient", "fusse", "fusses", "fût", "fussions", "fussiez",
    "fussent", "ceci", "cela", "cet", "cette", "ici", "ils", "les", "leurs", "quel",
    "quels", "quelle", "quelles", "sans", "soi", "ai", "as", "eu", "avons", "avez",
    "avait", "avaient", "avions", "ont", "a", "ça",
    // Special twitter stop words:
    "rt"
  )


}

object Tokenizer {
  val userNameRegex = """@\w{1,15}""".r
  case class Result(tweet: Tweet, tokens: List[String]) extends Tokenized with WithTweet
}
