package actors

import akka.actor._
import twitter4j._
import play.modules.reactivemongo.json.collection.JSONCollection
import play.modules.reactivemongo.ReactiveMongoPlugin
import reactivemongo.api._
import play.api.libs.json._
import play.api.Play.current

class MongoStoreActor extends Actor {

  implicit val statusWrites = new Writes[Status] {
    def writes(s : Status) : JsValue = {
      val u = s.getUser()

      Json.obj(
        "id" -> s.getId(),
        "created_at" -> s.getCreatedAt(),
        "text" -> s.getText(),
        "source" -> s.getSource(),
        "user" -> Json.obj(
          "id" -> u.getId(),
          "name" -> u.getName(),
          "screen_name" -> u.getScreenName(),
          "profile_image_url" -> u.getProfileImageURL()
        ),
        "hashtags" -> s.getHashtagEntities().map(_.getText())
      )
    }
  }

  def db = ReactiveMongoPlugin.db
  def collection: JSONCollection = db.collection[JSONCollection]("tweets")

  import context.dispatcher
  
  def receive = {
    case tweet : Status => collection.insert(tweet)
  }  
}