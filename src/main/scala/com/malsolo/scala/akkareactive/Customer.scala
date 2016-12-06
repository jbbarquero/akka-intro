package com.malsolo.scala.akkareactive

import akka.actor.Actor.Receive
import akka.actor.{Actor, ActorLogging, ActorRef, Props}

import scala.util.Random

object Customer {

  import RareBooksProtocol._

  def props(rareBooks: ActorRef, odds: Int, tolerance: Int): Props = Props(new Customer(rareBooks, odds, tolerance))

  case class CustomerModel(odds: Int, tolerance: Int, found: Int, notFound: Int)

  private case class State(model: CustomerModel, timeInMillis: Long) {
    def update(m: Msg): State = m match {
      case BookFound(b, d) =>
        copy(model.copy(found = model.found + b.size), timeInMillis = d)
      case BookNotFound(_, d) =>
        copy(model.copy(notFound = model.notFound + 1), timeInMillis = d)
      case Credit(d) =>
        copy(model.copy(notFound = 0), timeInMillis = d)
    }
  }

}

class Customer(rareBooks: ActorRef, odds: Int, tolerance: Int) extends Actor with ActorLogging {

  import Customer._
  import RareBooksProtocol._

  private var state = State(CustomerModel(odds, tolerance, 0, 0), -1l)

  requestBookInfo()

  override def receive: Receive = ???

  private def requestBookInfo(): Unit = rareBooks ! FindBookByTopic(Set(pickTopic))

  private def pickTopic: Topic =
    if (Random.nextInt(100) < state.model.odds)
      viableTopics(Random.nextInt(viableTopics.size))
    else
      Unknown




}
