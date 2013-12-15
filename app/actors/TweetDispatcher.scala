package actors

import akka.actor._
import twitter4j._

class TweetDispatcher extends Actor with ActorLogging {

  var received = 0
  
  val mongoStore = context.actorOf(Props[MongoStoreActor], name = "mongoStore")
  
  def receive = {
    case tweet : Status => 
      received += 1
      mongoStore ! tweet
      log.info(s"Received $received tweets so far ...")
  }

}