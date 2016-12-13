package com.malsolo.scala.akkainaction.futures

import java.time.{Duration, ZonedDateTime}

case class TicketInfo(ticketNr: String,
                      userLocation: Location,
                      event: Option[Event] = None,
                      travelAdvice: Option[TravelAdvice] = None,
                      weather: Option[Weather] = None,
                      suggestions: Seq[Event] = Seq())

case class Event(name: String, location: Location, time: ZonedDateTime)

case class Location(lat: Double, long: Double)

case class Route(route: String, timeToLeave: ZonedDateTime, origin: Location, destination: Location, duration: Duration)

case class PublicTransportAdvice(advice: String, timeToLeave: ZonedDateTime, origin: Location, destination: Location, duration: Duration)

case class TravelAdvice(route: Option[Route] = None, publicTransportAdvice: Option[PublicTransportAdvice] = None)

case class Weather(temperature: Int, precipitation: Boolean)

case class Artist(name: String, calendarUri: String)

