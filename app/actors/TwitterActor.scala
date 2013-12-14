package actors

import play.api.libs.ws.WS
import play.api.libs.oauth.OAuthCalculator
import utils.Conf
import play.api.libs.iteratee.{Iteratee, Concurrent}
import akka.actor._
import play.api.libs.json.{JsValue, Json}
import scala.concurrent._
import ExecutionContext.Implicits.global

class TwitterActor extends Actor with ActorLogging{

  val r = WS.url(Conf.twitterStreamUrl + "?track=Hollande")
    .sign(OAuthCalculator(Conf.consumerKey, Conf.accessToken))
    .get{ responseHeaders =>
      Iteratee.foreach { bytes : Array[Byte] =>
        val s = new String(bytes, "UTF-8")
        val t = Json.parse(s)
        self ! t
      }
    }

  def receive = {
    case tweet : JsValue => 
      val createdAt = (tweet \ "created_at").as[String]
      log.info("received tweet created at: " + createdAt)
  }

}

