package models

import twitter4j.Status
import org.joda.time.DateTime

class TweetAdapter(status: Status) extends Tweet{
  def id: Long = status.getId()
  def createdAt: DateTime = new DateTime(status.getCreatedAt())
  def text: String = status.getText()
  def source: String = status.getSource()
  def user: User = new UserAdapter(status.getUser())
  def hashtags: Seq[String] = status.getHashtagEntities.toSeq.map(_.getText())
}

class UserAdapter(user : twitter4j.User) extends User {
  def id: Long = user.getId()
  def name: String = user.getName()
  def screenName: String = user.getScreenName()
  def profileImageUrl: String = user.getProfileImageURL()
}