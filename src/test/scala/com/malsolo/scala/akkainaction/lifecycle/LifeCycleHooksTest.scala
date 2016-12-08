package com.malsolo.scala.akkainaction.lifecycle

import akka.actor.{ActorSystem, Props}
import akka.testkit.TestKit
import org.scalatest.{BeforeAndAfterAll, WordSpecLike}

class LifeCycleHooksTest extends TestKit(ActorSystem("LifeCycleTest")) with WordSpecLike with BeforeAndAfterAll {

  override def afterAll(): Unit = system.terminate()

  "An actor in its Lifecycle " must {
    "log when triggering the lifecycle events" in {
      println("ActorRef...")
      val testActorRef = system.actorOf(Props[LifeCycleHooks], "LifeCycleHooks")
      println("Send restart...")
      testActorRef ! "restart"
      println("Tell msg...")
      testActorRef.tell("msg", testActor)
      println("Expect msg...")
      expectMsg("msg")
      println("Before stop...")
      system.stop(testActorRef)
      Thread.sleep(1000)
    }
  }

}
