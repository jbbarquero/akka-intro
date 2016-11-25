package com.malsolo.scala.akka

import akka.actor.{Actor, ActorRef}
import com.malsolo.scala.akka.Guidebook.Inquiry
import com.malsolo.scala.akka.Tourist.{Guidance, Start}

object Tourist {
  case class Guidance(code: String, description: String)

  case class Start(codes: Seq[String])
}

class Tourist(guidebook: ActorRef) extends Actor {

  override def receive = {
    case Start(codes) =>
      codes.foreach(guidebook ! Inquiry(_))
    case Guidance(code, description) =>
      println(s"$code: $description")
    case unexpected =>
      println(s":(")
  }

}
