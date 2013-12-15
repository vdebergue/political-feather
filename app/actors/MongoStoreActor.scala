package actors

import akka.actor._
import play.modules.reactivemongo.json.collection.JSONCollection
import play.modules.reactivemongo.ReactiveMongoPlugin
import play.api.libs.json._
import play.api.Play.current
import models.Tweet
import models.JsonFormat._

class MongoStoreActor extends Actor {

  def db = ReactiveMongoPlugin.db
  def collection: JSONCollection = db.collection[JSONCollection]("tweets")

  // Import an Execution context
  import context.dispatcher
  
  def receive = {
    case tweet : Tweet => collection.insert(tweet)
  }  
}