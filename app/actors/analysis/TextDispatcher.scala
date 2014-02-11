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
  val analyserBis = context.actorOf(Props[AnalyserBis], "analyserBis")

  // Process actors
  val wordUsage = context.actorOf(Props[WordUsage], "wordUsage")
  val mostUsedCategories = context.actorOf(Props[MostUsedCategories], "mostUsedCategories")
  val sentiment = context.actorOf(Props[Sentiment], "sentiments")
  

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
      // analyserBis ! AnalyserBis.Input(in.tweet, in.stems)
      wordUsage ! WordUsage.Words(in.stems, in.tweet.createdAt)

    case in: WithTweet with Analysed =>
      // send to categories result
      mostUsedCategories ! MostUsedCategories.Input(in.categories, in.tweet.createdAt)
      sentiment ! Sentiment.Input(in.positive, in.negative, in.tweet.createdAt, in.tweet.id.toString())

    case in: WithTweet with AnalysedBis =>
      // TODO send to a sentiment actor

    case TweetDispatcher.Tick =>
      // dispatch to process actors
      wordUsage ! TweetDispatcher.Tick
      mostUsedCategories ! TweetDispatcher.Tick
      sentiment ! TweetDispatcher.Tick
  }
}
