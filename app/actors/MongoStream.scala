package actors

import akka.actor._
import play.api.libs.json._
import play.modules.reactivemongo.ReactiveMongoPlugin
import play.api.Play.current
import reactivemongo.api._
import play.modules.reactivemongo.json.collection.JSONCollection
import play.api.libs.iteratee.Iteratee
import models.Tweet
import models.JsonFormat._
import scala.concurrent._
import ExecutionContext.Implicits.global

class MongoStream(tweetDispatcher: ActorRef) extends TweetStream {

  def start() = {
    val query = Json.obj()
    // we get all the tweets
    val cursor : Cursor[Tweet] = collection.find(query).cursor[Tweet]
    val iteratee = Iteratee.foreach[Tweet](t =>
      tweetDispatcher ! t
    )
    cursor.enumerate().apply(iteratee)
  }

  def db = ReactiveMongoPlugin.db
  def collection: JSONCollection = db.collection[JSONCollection]("tweets")

  def stop() = {

  }

}
