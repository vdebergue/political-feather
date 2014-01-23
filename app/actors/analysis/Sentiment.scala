package actors.analysis

import akka.actor.Actor
import org.joda.time.DateTime
import actors.TweetDispatcher
import actors.analysis.Sentiment.Input
import models.analysis.{WithDate, SlidingBuffer}
import play.api.libs.json.{Json, JsValue, Writes}
/**
 * Created by vince on 23/01/14.
 */
class Sentiment extends Actor {

  val buffer = SlidingBuffer.oneDay[Input]

  def receive = {
    case in: Input =>
      buffer.push(in)
    case TweetDispatcher.Tick =>
      TweetDispatcher.inSentiment.push(Json.toJson(buffer.toList))
  }

  implicit val inputWrites : Writes[Input] = new Writes[Input] {
    def writes(o: Input): JsValue = Json.obj(
      "pos" -> o.pos,
      "neg" -> o.neg,
      "date" -> o.date,
      "tweet_id" -> o.tweetId
    )
  }

}

object Sentiment {
  case class Input(pos: Double, neg: Double, date: DateTime, tweetId: String) extends WithDate
}
