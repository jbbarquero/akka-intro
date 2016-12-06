package com.malsolo.scala.akkareactive

import java.util.concurrent.TimeUnit._

import akka.actor.{Actor, ActorLogging, ActorRef, Props, Stash}
import com.malsolo.scala.akkareactive.RareBooks.{Close, Open, Report}
import com.malsolo.scala.akkareactive.RareBooksProtocol.Msg

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.FiniteDuration

object RareBooks {
  case object Close
  case object Open
  case object Report

  def props: Props = Props(new RareBooks)
}

class RareBooks extends Actor with ActorLogging with Stash {

  private val openDuration: FiniteDuration =  FiniteDuration(8, HOURS)
  private val closeDuration: FiniteDuration = FiniteDuration(8, HOURS)
  private val findBookDuration: FiniteDuration = FiniteDuration(8, HOURS)

  private val librarian = createLibrarian()

  var requestsToday: Int = 0
  var totalRequests: Int = 0

  context.system.scheduler.scheduleOnce(openDuration, self, Close)

  override def receive: Receive = open

  private def open: Receive = {
    case m: Msg =>
      librarian forward m
      requestsToday += 1
    case Close =>
      context.system.scheduler.scheduleOnce(closeDuration, self, Open)
      log.info("Closing down for day")
      context.become(close)
      self ! Report
  }

  private def close: Receive = {
    case Open =>
      context.system.scheduler.scheduleOnce(openDuration, self, Close)
      unstashAll()
      log.info("Time to open up!")
      context.become(open)
    case Report =>
      totalRequests += requestsToday
      log.info(s"$requestsToday ... requests processed = $totalRequests")
      requestsToday = 0
    case _ =>
      stash()
  }

  protected def createLibrarian(): ActorRef = context.actorOf(Librarian.props(findBookDuration), "librarian")

}
