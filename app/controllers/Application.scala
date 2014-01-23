package controllers

import play.api._
import play.api.mvc._
import play.api.libs.iteratee.{Enumeratee, Iteratee}
import actors.TweetDispatcher
import play.api.libs.json.{JsObject, Json, JsValue}
import scala.concurrent.ExecutionContext.Implicits.global

object Application extends Controller {

  def index = Action { implicit request =>
    Ok(views.html.index(request))
  }

  lazy val inIgnore = Iteratee.ignore[JsValue]
  lazy val outStream = (TweetDispatcher.outHashtags &> enumerateeHeader("hashtags"))
    .interleave(TweetDispatcher.outMostActive &> enumerateeHeader("mostActive"))
    .interleave(TweetDispatcher.outWordUsage &> enumerateeHeader("wordUsage"))
    .interleave(TweetDispatcher.outTweetNumber &> enumerateeHeaderInt("tweetNumber"))


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

}