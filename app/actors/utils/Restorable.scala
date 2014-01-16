package actors.utils

import akka.actor.{Props, ActorRef, Actor}
import scala.pickling._
import binary._
import java.io.{FileNotFoundException, FileInputStream}

trait Restorable { this : Actor =>

  def restore() : Unit

  val saver : ActorRef = context.actorOf(Props[SaverActor], "saver")

  def file : String

  def getFromFile() : Option[Array[Byte]] = {
    try{
      val in = new FileInputStream(file)
      Some(Stream.continually(in.read).takeWhile(-1 !=).map(_.toByte).toArray)
    } catch {
      case t: FileNotFoundException => None
    }
  }

  // Send to the saver Actor the state
  def save(state: Serializable) = {
    saver ! SaverActor.ToSave(file, state)
  }
}
