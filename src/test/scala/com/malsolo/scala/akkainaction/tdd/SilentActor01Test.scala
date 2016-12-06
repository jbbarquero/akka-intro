package com.malsolo.scala.akkainaction.tdd

import akka.actor.{Actor, ActorSystem}
import akka.testkit.TestKit
import org.scalatest.{MustMatchers, WordSpecLike}

class SilentActor01Test extends TestKit(ActorSystem("testsystem"))
  with WordSpecLike
  with MustMatchers
  with StopSystemAfterAll {

  "A Silent Actor" must {
    "change state when it receives a message, single threaded" in {
      fail("not implemented yet")
    }
    "change state when it receives a message, multi-threaded" in {
      fail("not implemented yet")
    }
  }
}

class SilentActor extends Actor {
  def receive = {
    case msg =>
  }
}
