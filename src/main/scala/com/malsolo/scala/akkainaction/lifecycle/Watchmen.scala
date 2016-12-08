package com.malsolo.scala.akkainaction.lifecycle

import akka.actor.{Actor, ActorLogging, ActorRef, Terminated}

class Watchmen(actor: ActorRef) extends Actor with ActorLogging {

  context.watch(actor)

  def receive = {
    case Terminated(actor) =>
      log.warning("Actor {} terminated", actor)
  }
}
