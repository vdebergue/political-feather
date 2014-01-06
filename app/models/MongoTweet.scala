package models

import org.joda.time.DateTime
import play.api.libs.json._
import play.api.libs.functional.syntax._

case class MongoTweet(id: Long, createdAt: DateTime, text: String, source: String, user: User, hashtags: Seq[String]) extends Tweet

case class MongoUser(id: Long, name: String, screenName: String, profileImageUrl: String) extends User

object JsonFormat {

  implicit val userReads : Reads[User] = (
    (__ \ "id").read[Long] and
    (__ \ "name").read[String] and
    (__ \ "screen_name").read[String] and
    (__ \ "profile_image_url").read[String]
   )(MongoUser.apply _)

  implicit val userWrites: Writes[User] = new Writes[User] {
    def writes(o: User): JsValue = Json.obj(
      "id" -> o.id,
      "name" -> o.name,
      "screenName" -> o.screenName,
      "profileImageUrl" -> o.profileImageUrl
    )
  }

  implicit  val tweetReads : Reads[Tweet] = (
    (__ \ "id").read[Long] and
      (__ \ "created_at").read[DateTime] and
      (__ \ "text").read[String] and
      (__ \ "source").read[String] and
      (__ \ "user").read[User] and
      (__ \ "hashtags").read[Seq[String]]
    )(MongoTweet.apply _)

  implicit val tweetWrites = new Writes[Tweet] {
    def writes(s : Tweet) : JsValue = {
      val u = s.user
      Json.obj(
        "id" -> s.id,
        "created_at" -> s.createdAt.getMillis(),
        "text" -> s.text,
        "source" -> s.source,
        "user" -> Json.obj(
          "id" -> u.id,
          "name" -> u.name,
          "screen_name" -> u.screenName,
          "profile_image_url" -> u.profileImageUrl
        ),
        "hashtags" -> s.hashtags
      )
    }
  }
}
