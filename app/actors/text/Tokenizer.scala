package actors.text

import akka.actor.Actor
import actors.text.Tokenizer.Result

/**
 * Created by vince on 09/01/14.
 */
class Tokenizer extends Actor {

  def receive = {
    case phrase : String => sender ! Result(tokenize(phrase))
  }

  def tokenize(text: String): List[String] = {
    text.replaceAll("#", "").replaceAll("'", " ").toLowerCase().split(' ').filterNot(isStopWord).toList
  }

  def isStopWord(word: String) : Boolean = {
    stopWords.contains(word)
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
    "avait", "avaient", "avions", "ont"
  )

}

object Tokenizer {
  case class Result(words: List[String])
}
