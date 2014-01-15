package actors.analysis

import akka.actor.{Props, Actor}
import models.Tweet
import models.text._
import actors.text._
import actors.TweetDispatcher

class TextDispatcher extends Actor{

  // Text analysis actors
  val tokenizer = context.actorOf(Props[Tokenizer], "tokenizer")
  val stemmer = context.actorOf(Props[Stemmer], "stemmer")
  val analyser = context.actorOf(Props[Analyser], "analyser")

  // Process actors
  val wordUsage = context.actorOf(Props[WordUsage], "wordUsage")
  

  def receive = {
    case t : Tweet =>
      //tokenize
      tokenizer ! t

    case in : WithTweet with Tokenized =>
      // stem
      stemmer ! Stemmer.Input(in.tweet, in.tokens)

    case in: WithTweet with Stemmed =>
      // analyse
      analyser ! Analyser.Input(in.tweet, in.stems)
      wordUsage ! WordUsage.Words(in.stems, in.tweet.createdAt)

    case in: WithTweet with Analysed =>
      // do stuff

    case TweetDispatcher.Tick =>
      // dispatch to process actors
      wordUsage ! TweetDispatcher.Tick
  }
}
