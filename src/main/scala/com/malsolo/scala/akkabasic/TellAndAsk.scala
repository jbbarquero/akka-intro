package com.malsolo.scala.akkabasic

import akka.actor.{Actor, ActorSystem, Props}

import scala.concurrent.duration._
import akka.pattern.ask
import akka.util.Timeout

import scala.util.{Failure, Success}

import scala.concurrent.ExecutionContext.Implicits.global

object TellAndAsk extends App {
  val system = ActorSystem("TellAndAsk")
  val tellProps = Props[TellActor]
  val tellActor = system.actorOf(tellProps, "tell")
  val askProps = Props[AskActor]
  val askActor = system.actorOf(askProps, "ask")

  Thread.sleep(1000)

  tellActor ! "Start"

  Thread.sleep(2000)

  askActor ! "Start"


}

class TellActor extends Actor {

  val recipient = context.actorOf(Props[ReceiveActor])

  def receive = {
    case "Start" =>
      recipient ! "Hello"
    case reply =>
      println(reply)
  }

}

class AskActor extends Actor {

  val recipient = context.actorOf(Props[ReceiveActor])

  def receive = {
    case "Start" =>
      implicit val timeout: Timeout = 3 seconds
      val replyFuture = recipient ? "Hello"
      replyFuture.onComplete {
        case Success(reply) => println(reply)
        case Failure(e) => println("Error: " + e.getMessage)
      }

  }
}

class ReceiveActor extends Actor {
  def receive = {
    case "Hello" =>
      sender ! "And Hello to you"
  }
}
