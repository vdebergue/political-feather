package actors.analysis

import akka.actor.Actor
import actors.TweetDispatcher
import models.Tweet
import models.analysis.{WithDate, SlidingBuffer}
import org.joda.time.DateTime
import actors.analysis.Activity.Date
import play.api.libs.json.Json

/**
 * Created by vince on 26/01/14.
 */
class Activity extends Actor {

  val buffer = SlidingBuffer.oneDay[Date]

  def receive = {
    case TweetDispatcher.Tick =>
      TweetDispatcher.inActivity.push{
        Json.toJson(buffer.toList.map(_.date))
      }

    case tweet: Tweet =>
      buffer.push(Date(tweet.createdAt))

  }
}

object Activity {
  case class Date(date: DateTime) extends WithDate
}