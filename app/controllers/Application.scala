package controllers

import play.api._
import play.api.mvc._
import play.api.libs.ws.WS
import play.api.libs.oauth.OAuthCalculator
import utils.Conf
import play.api.libs.iteratee.{Iteratee, Concurrent}
import scala.concurrent._
import ExecutionContext.Implicits.global
import play.api.libs.json.{JsValue, Json}

object Application extends Controller {

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  val body = Map("track" -> Seq("Sarkozy", "Hollande", "gouvernement"))

  val (twitterStream, twitterChannel) = Concurrent.broadcast[JsValue]

  val r = WS.url(Conf.twitterStreamUrl + "?track=Hollande")
    .sign(OAuthCalculator(Conf.consumerKey, Conf.accessToken))
    .get{ responseHeaders =>
      Iteratee.foreach { bytes : Array[Byte] =>
        val s = new String(bytes, "UTF-8")
        println("Got one !: " + s)
        twitterChannel.push(Json.parse(s))
      }
    }

  def streamWebSocket = WebSocket.using[JsValue] { request => 
  
    // Log events to the console
    val in = Iteratee.foreach[JsValue](println).map { _ =>
      println("Disconnected")
    }
    
    val out = twitterStream
    
    (in, out)
  }

}