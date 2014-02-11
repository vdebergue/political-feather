package controllers

import play.api._
import play.api.mvc._
import play.api.libs.iteratee.{Enumeratee, Iteratee}
import actors.TweetDispatcher
import play.api.libs.json.{JsObject, Json, JsValue}
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.libs.ws.WS
import utils.LearningAnalyzer

object Application extends Controller {

  def index = Action { implicit request =>
    Ok(views.html.index(request))
  }

  lazy val inIgnore = Iteratee.ignore[JsValue]
  lazy val outStream = (TweetDispatcher.outHashtags &> enumerateeHeader("hashtags"))
    .interleave(TweetDispatcher.outMostActive &> enumerateeHeader("mostActive"))
    .interleave(TweetDispatcher.outWordUsage &> enumerateeHeader("wordUsage"))
    .interleave(TweetDispatcher.outTweetNumber &> enumerateeHeaderInt("tweetNumber"))
    .interleave(TweetDispatcher.outCategories &> enumerateeHeader("categories"))
    .interleave(TweetDispatcher.outSentiment &> enumerateeHeader("sentiments"))
    .interleave(TweetDispatcher.outActivity &> enumerateeHeader("activity"))


  def feed = WebSocket.using[JsValue] { request =>
    TweetDispatcher.actor ! TweetDispatcher.Tick
    (inIgnore, outStream)
  }

  def enumerateeHeader(header: String) : Enumeratee[JsValue, JsValue] = Enumeratee.map[JsValue] { js =>
    Json.obj(header -> js)
  }

  def enumerateeHeaderInt(header: String) : Enumeratee[Int, JsValue] = Enumeratee.map[Int] { i =>
    Json.obj(header -> i)
  }

  // Proxy for Twitter API
  def tweet(id: String) = Action.async {
    WS.url("https://api.twitter.com/1/statuses/oembed.json?id=" + id).get().map( resp =>
      Ok(resp.json)
    )
  }

  def getUnknowWords = Action {
    val set = LearningAnalyzer.getUnknowns
    Ok(set.mkString)
  }

}