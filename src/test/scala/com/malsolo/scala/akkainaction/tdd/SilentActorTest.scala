package com.malsolo.scala.akkainaction.tdd

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.testkit.{TestActorRef, TestKit}
import org.scalatest.{MustMatchers, WordSpecLike}

class SilentActorTest extends TestKit(ActorSystem("testsystem"))
  with WordSpecLike
  with MustMatchers
  with StopSystemAfterAll {

  "A Silent Actor" must {
    "change state when it receives a message, single threaded" in {

      import SilentActor._

      val silentActor = TestActorRef[SilentActor]
      silentActor ! SilentMessage("whisper")
      silentActor.underlyingActor.state must (contain("whisper"))
    }

    "change state when it receives a message, multi-threaded" in {

      import SilentActor._

      val silentActor = system.actorOf(Props[SilentActor], "s3")
      silentActor ! SilentMessage("whisper in time")
      silentActor ! SilentMessage("Careless whisper")
      silentActor ! GetState(testActor)

      expectMsg(Vector("whisper in time", "Careless whisper"))

    }


  }
}

object SilentActor {
  case class SilentMessage(msg: String)
  case class GetState(receiver: ActorRef)
}

class SilentActor extends Actor {
  import SilentActor._

  var internalState = Vector[String]()

  def receive = {
    case SilentMessage(msg) =>
      internalState = internalState :+ msg
    case GetState(receiver) =>
      receiver ! internalState
  }

  def state = internalState
}
