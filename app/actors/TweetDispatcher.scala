package actors

import akka.actor._
import twitter4j._

class TweetDispatcher extends Actor {
  
  def receive = {
    case tweet : Status => 
  }

}