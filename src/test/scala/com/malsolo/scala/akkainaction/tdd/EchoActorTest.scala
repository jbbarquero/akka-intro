package com.malsolo.scala.akkainaction.tdd

import akka.actor.{Actor, ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit}
import akka.util.Timeout
import org.scalatest.WordSpecLike

import scala.concurrent.Await
import scala.util.{Failure, Success}

class EchoActorTest extends TestKit(ActorSystem("testsystem"))
  with WordSpecLike
  with ImplicitSender
  with StopSystemAfterAll {

  "An EchoActor" must {
    "Reply with the same message it receives" in {
      import akka.pattern.ask

      import scala.concurrent.duration._

      implicit val timeout = Timeout(3 seconds)
      implicit val dispatcher = system.dispatcher
      val echo = system.actorOf(Props[EchoActor], "echo1")
      val future = echo ? "some message"
      future.onComplete{
        case Failure(_) =>
          println("Failure")
        case Success(msg) =>
          println("Success")
      }

      Await.ready(future, timeout.duration)

    }

    "Reply with the same message it receives without ask" in {
      val echo = system.actorOf(Props[EchoActor], "echo2")
      echo ! "some message"
      expectMsg("some message")
    }
  }



}

class EchoActor extends Actor {
  def receive = {
    case msg =>
      sender() ! msg
  }
}