package com.malsolo.scala.akkainaction.lifecycle

import java.io.File

import akka.actor.{Actor, Props}

class LogProcessing {

  object DbWriter {
    def props(databaseUrl: String) = Props(new DbWriter(databaseUrl))

    def name(databaseUrl: String) = s"""db-writer-${databaseUrl.split("/").last}"""

    case class Line(time: Long, message: String, messageType: String)
  }

  class DbWriter(databaseUrl: String) extends Actor {
    val con = new DbCon(databaseUrl)

    import DbWriter._

    def receive = {
      case Line(time, message, messageType) =>
        con.write(Map('time -> time, 'message -> message, 'type -> messageType))
    }

    override def postStop(): Unit = con.close()
  }

  class DbCon(url: String) {
    def write(map: Map[Symbol, Any]): Unit = {
      map.foreach{case (symbol, any) => println("Writting {}: {}", symbol, any)}
    }

    def close(): Unit = {
      println("Close")
    }
  }

  @SerialVersionUID(1L)
  class DiskError(msg: String) extends Error(msg) with Serializable

  @SerialVersionUID(1L)
  class CorruptedFileException(msg: String, val file: File) extends Exception(msg) with Serializable

  @SerialVersionUID(1L)
  class DbBrokenConnectionException(msg: String) extends Exception(msg) with Serializable

  @SerialVersionUID(1L)
  class DbNodeException(msg: String) extends Exception(msg) with Serializable



}
