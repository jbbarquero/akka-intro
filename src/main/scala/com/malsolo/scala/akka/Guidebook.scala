package com.malsolo.scala.akka

import java.util.{Currency, Locale}

import akka.actor.Actor
import com.malsolo.scala.akka.Guidebook.Inquiry
import com.malsolo.scala.akka.Tourist.Guidance

object Guidebook {
  case class Inquiry(code: String)
}

class Guidebook extends Actor {
  def describe(locale: Locale) = {
    val currency = Currency.getInstance(locale)
    s"""In ${locale.getDisplayCountry}, ${locale.getDisplayLanguage} is spoken and the currency is the ${currency.getDisplayName} (${currency.getSymbol(locale)} - ${currency.getSymbol} - ${currency.getNumericCode})"""
  }

  override def receive = {
    case Inquiry(code) =>
      println(s"Actor ${self.path.name} responding to inquiry about $code")
      Locale.getAvailableLocales.filter(_.getCountry == code).
        foreach { locale =>
          sender ! Guidance(code, describe(locale))
        }
  }
}
