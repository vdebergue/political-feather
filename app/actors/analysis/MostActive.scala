package actors.analysis

import akka.actor.Actor
import models.{User, Tweet}

class MostActive extends Actor {

  val usersCount = collection.mutable.Map[User, Int]()

  def receive = {
    case tweet : Tweet =>
      val userCountOption = usersCount.get(tweet.user)
      userCountOption match {
        case Some(count) => usersCount.update(tweet.user, count + 1)
        case None => usersCount += (tweet.user -> 1)
      }

    println("MA: " + mostActive)
  }

  def mostActive : (User, Int) = {
    usersCount.maxBy(_._2)
  }

}
