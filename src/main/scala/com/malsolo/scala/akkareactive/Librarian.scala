package com.malsolo.scala.akkareactive

import akka.actor.Actor.Receive
import akka.actor.{Actor, ActorLogging, ActorRef, Props, Stash}

import scala.concurrent.duration.FiniteDuration

object Librarian {

  import Catalog._
  import RareBooksProtocol._

  final case class Done(e: Either[BookNotFound, BookFound], customer: ActorRef)

  def props(findBookDuration: FiniteDuration): Props = Props(new Librarian(findBookDuration))

  private def optToEither[T](v: T, f: T => Option[List[BookCard]]): Either[BookNotFound, BookFound] = f(v) match {
    case b: Some[List[BookCard]] =>
      Right(BookFound(b.get))
    case _ =>
      Left(BookNotFound(""))
  }

  def findBookByIsbn = ??? //(isbn: String) = Catalog.books.get(isbn)

  private def findByIsbn(fb: FindBookByIsbn) = optToEither[String](fb.isbn, findBookByIsbn)

  def findBookByTopic = ???

  private def findByTopic(fb: FindBookByTopic) = optToEither[Set[Topic]](fb.topic, findBookByTopic)

}

class Librarian(findBookDuration: FiniteDuration) extends Actor with ActorLogging with Stash {

  import context.dispatcher
  import Librarian._
  import RareBooksProtocol._

  override def receive: Receive = ready

  private def ready: Receive = {
    case m: Msg => m match {
      case c: Complain =>
        sender ! Credit()
        log.info(s"Credit issued to customer $sender()")
      case f: FindBookByTopic =>
        research(Done(findByTopic(f), sender()))
    }

  }

  private def busy: Receive = {
    case Done(e, s) =>
      process(e, s)
      unstashAll()
      context.become(ready)
    case _ =>
      stash()
  }

  private def research(d: Done): Unit = {
    context.system.scheduler.scheduleOnce(findBookDuration, self, d)
    context.become(busy)
  }

  private def process(r: Either[BookNotFound, BookFound], s: ActorRef): Unit = {
    r fold(
      f => {
        sender ! f
        log.info(f.toString)
      },
      s => sender ! s)
  }


}
