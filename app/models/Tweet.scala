package models

import org.joda.time.DateTime

trait Tweet {
  def id : Long
  def createdAt : DateTime
  def text: String
  def source: String
  def user : User

  /**
   * The hashtags contained in the text without the #
   * @return
   */
  def hashtags : Seq[String]

  override def toString() : String = {
    s"[$createdAt] ${user.screenName} : $text"
  }
}

trait User {
  def id: Long
  def name : String
  def screenName : String
  def profileImageUrl : String

  override def toString() = {
    "@" + screenName
  }

}
