package actors.utils

import akka.actor.Actor
import actors.utils.SaverActor.ToSave
import java.io.{File, FileOutputStream, FileWriter}
import scala.pickling._
import binary._
import scala.pickling.io.TextFileOutput

class SaverActor extends Actor {

  def store(fileName: String, state: Serializable): Unit = {
    val out = new FileOutputStream(fileName)
    val bytes = state.pickle.value
    out.write(bytes)
    out.close()
  }

  def receive = {
    case ToSave(fileName, state) =>
      store(fileName, state)
  }
}

object SaverActor {
  case class ToSave(fileName: String, state: Serializable)
}