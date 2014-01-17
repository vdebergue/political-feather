package actors.utils

import akka.actor.{Props, ActorRef, Actor}
import scala.pickling._
import binary._
import java.io.{FileNotFoundException, FileInputStream}

trait Restorable {

  def restore() : Unit
  def save() : Unit

  def file : String

  def getFromFile() : Option[Array[Byte]] = {
    try{
      val in = new FileInputStream(file)
      Some(Stream.continually(in.read).takeWhile(-1 !=).map(_.toByte).toArray)
    } catch {
      case t: FileNotFoundException => None
    }
  }
}
