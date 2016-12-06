package com.malsolo.scala.akkareactive

import scala.compat.Platform

object RareBooksProtocol {

  sealed trait Topic

  case object Africa extends Topic
  case object Asia extends Topic
  case object Gilgamesh extends Topic
  case object Greece extends Topic
  case object Persia extends Topic
  case object Philosophy extends Topic
  case object Royalty extends Topic

  case object Unknown extends Topic

  val viableTopics = List(Africa, Asia, Gilgamesh, Greece, Persia, Philosophy, Royalty)


  sealed trait Card {
    def title: String

    def description: String

    def topic: Set[Topic]
  }

  //final case class Topic(name: String)

  final case class BookCard(
                             isbn: String,
                             author: String,
                             title: String,
                             description: String,
                             dateOfOrigin: String,
                             topic: Set[Topic],
                             publisher: String,
                             language: String,
                             pages: Int
                           ) extends Card

  trait Msg {
    def dateInMillis: Long
  }

  final case class FindBookByIsbn(isbn: String, dateInMillis: Long = Platform.currentTime) extends Msg {
    require(isbn.nonEmpty, "Isbn required.")
  }

  final case class FindBookByTopic(topic: Set[Topic], dateInMillis: Long = Platform.currentTime) extends Msg {
    require(topic.nonEmpty, "Topic required.")
  }

  case class BookNotFound(msg: String, dateInMillis: Long = Platform.currentTime) extends Msg

  case class BookFound(books: List[BookCard], dateInMillis: Long = Platform.currentTime) extends Msg

  case class Complain(dateInMillis: Long = Platform.currentTime) extends Msg

  case class Credit(dateInMillis: Long = Platform.currentTime) extends Msg

}

