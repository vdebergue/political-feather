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

  lazy val inIgnore = Iteratee.ignore[String]
  lazy val outStream = (TweetDispatcher.outHashtags &> enumerateeHeader("hashtags"))
    .interleave(TweetDispatcher.outMostActive &> enumerateeHeader("mostActive"))
    .interleave(TweetDispatcher.outWordUsage &> enumerateeHeader("wordUsage"))
    .through(Enumeratee.map[JsObject](_.toString()))

  def feed = WebSocket.using[String] { request =>
    TweetDispatcher.actor ! TweetDispatcher.Tick
    (inIgnore, outStream)
  }

  def enumerateeHeader(header: String) : Enumeratee[JsValue, JsObject] = Enumeratee.map[JsValue] { js =>
    Json.obj(header -> js)
  }

}