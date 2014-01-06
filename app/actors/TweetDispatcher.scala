package actors

import akka.actor._
import models.Tweet
import TweetDispatcher._
import scala.concurrent.duration._
import actors.analysis.{MostUsedHastag, MostActive}
import scala.collection.mutable.ListBuffer
import play.api.libs.iteratee.{Iteratee, Concurrent, Enumerator}
import play.api.libs.json.JsValue
import play.api.libs.iteratee.Concurrent.Channel

class TweetDispatcher extends Actor with ActorLogging {

  var received = 0

  //val mongoStore = context.actorOf(Props[MongoStoreActor], name = "mongoStore")

  val analysisActors = ListBuffer[ActorRef]()
  val mostActive = context.actorOf(Props[MostActive], "mostActive")
  val mostUsedHashtags = context.actorOf(Props[MostUsedHastag], "mostUsedHashtags")
  analysisActors += mostActive
  analysisActors += mostUsedHashtags

  // Send a tick every 30 sec
  context.system.scheduler.schedule(30.seconds, 30.seconds, self, Tick)(context.dispatcher)
  
  def receive = {
    case tweet : Tweet =>
      received += 1
      // if we want to store the tweet in mongo
      // mongoStore ! tweet

      analysisActors.foreach { actor =>
        actor ! tweet
      }
      log.info(s"Received $received tweets so far ...")
    case Tick =>
      analysisActors.foreach(_ ! Tick)
  }

}

object TweetDispatcher {
  object Tick
  lazy val (outMostActive: Enumerator[JsValue], inMostActive: Channel[JsValue]) = Concurrent.broadcast[JsValue]
  lazy val (outHashtags: Enumerator[JsValue], inHashTags: Channel[JsValue]) = Concurrent.broadcast[JsValue]

//  import scala.concurrent.ExecutionContext.Implicits.global
//  outHashtags.interleave(outMostActive) |>>> Iteratee.foreach[JsValue](println)
}