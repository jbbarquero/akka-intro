package com.malsolo.scala.akkainaction.futures

import java.time.ZonedDateTime

import scala.concurrent.Future
import scala.util.control.NonFatal

trait TicketInfoService extends WebServiceCalls {

  import scala.concurrent.ExecutionContext.Implicits.global

  type Recovery[T] = PartialFunction[Throwable, T]

  def withNone[T]: Recovery[Option[T]] = { case NonFatal(e) => None }

  def withEmptySeq[T]: Recovery[Seq[T]] = { case NonFatal(e) => Seq() }

  def withPrevious(previous: TicketInfo): Recovery[TicketInfo] = { case NonFatal(e) => previous }

  def getTicketInfo(ticketNr: String, location: Location): Future[TicketInfo] = {
    val emptyTicketInfo = TicketInfo(ticketNr, location)
    val eventInfo = getEvent(ticketNr, location).recover(withPrevious(emptyTicketInfo))

    eventInfo.flatMap {
      info =>

        val infoWithWeather = getWeather(info)

        val infoWithTravelAdvice = info.event.map {
          event =>
            getTravelAdvice(info, event)
        }.getOrElse(eventInfo)

        val suggestedEvents = info.event.map {
          event =>
            getSuggestions(event)
        }.getOrElse(Future.successful(Seq()))

        val ticketInfos = Seq(infoWithTravelAdvice, infoWithWeather)

        val infoWithTravelAndWeather: Future[TicketInfo] = Future.fold(ticketInfos)(info) {
          (acc, elem) =>
            val (travelAdvice, weather) = (elem.travelAdvice, elem.weather)

            acc.copy(travelAdvice = travelAdvice.orElse(acc.travelAdvice), weather = weather.orElse(acc.weather))
        }

        for(
          info <- infoWithTravelAndWeather;
          suggestions <- suggestedEvents
        ) yield info.copy(suggestions = suggestions)
    }
  }

  def getWeather(ticketInfo: TicketInfo): Future[TicketInfo] = {
    Future.firstCompletedOf(Seq(
      callWeatherServiceX(ticketInfo).recover(withNone),
      callWeatherServiceY(ticketInfo).recover(withNone)
    )).map {
      weatherResponse =>
        ticketInfo.copy(weather = weatherResponse)
    }

//    val fastestSuccessfulREsponse = Future.find(List(
//      callWeatherServiceX(ticketInfo).recover(withNone),
//      callWeatherServiceY(ticketInfo).recover(withNone)
//    )) (maybeWeather => !maybeWeather.isEmpty).map(_.flatten)
  }

  def getTravelAdvice(ticketInfo: TicketInfo, event: Event): Future[TicketInfo] = {
    val futureRoute = callTrafficService(ticketInfo.userLocation, event.location, event.time).recover(withNone)
    val futurePublicTransport = callPublicTransportService(ticketInfo.userLocation, event.location, event.time).recover(withNone)

    futureRoute.zip(futurePublicTransport).map {
      case(route, publicTransportAdvice) =>
        val travelAdvice: TravelAdvice(route, publicTransportAdvice)
        ticketInfo.copy(travelAdvice = Some(travelAdvice))
    }

    for (
      (route, publicTransportAdvice) <- futureRoute.zip(futurePublicTransport);
      travelAdvice = TravelAdvice(route, publicTransportAdvice)
    ) yield ticketInfo.copy(travelAdvice = Some(travelAdvice))

  }

  def getPlannedEventsWithTraverse(event: Event, artists: Seq[Artist]): Future[Seq[Event]] = {
    Future.traverse(artists) {artist => callArtistCalendarService(artist, event.location)}
  }

  def getPlannedEvents(event: Event, artists: Seq[Artist]): Future[Seq[Event]] = {
    val events = artists.map( artist => callArtistCalendarService(artist, event.location))
    Future.sequence(events)
  }

  def getSuggestions(event: Event): Future[Seq[Event]] = {
    val futureArtists = callSimilarArtistsService(event).recover(withEmptySeq)

    for(
      artists <- futureArtists;
      events <- getPlannedEvents(event, artists).recover(withEmptySeq)
    ) yield events
  }

//  def getTicketInfo(ticketNr: String, location: Location): Future[TicketInfo] = {
//    val futureEvent: Future[TicketInfo] = getEvent(ticketNr, location)
//    val futureEventWithTrafficInfo: Future[TicketInfo] = futureEvent.flatMap {
//      ticketInfo =>
//        getTraffic(ticketInfo).recover {
//          case _: Exception => ticketInfo
//        }
//    }.recover {
//      case e => TicketInfo(ticketNr, location)
//    }
//    futureEventWithTrafficInfo
//  }

//  def getTraffic(ticketInfo: TicketInfo): Future[TicketInfo] = {
//    ticketInfo.event.map(
//      event =>
//        callTrafficService(ticketInfo.userLocation, event.location).map(
//
//        )
//
//    )
//  }


}

trait WebServiceCalls {

  def getEvent(ticketNr: String, location: Location): Future[TicketInfo]

  def callWeatherServiceX(ticketInfo: TicketInfo): Future[Option[Weather]]

  def callWeatherServiceY(ticketInfo: TicketInfo): Future[Option[Weather]]

  def callTrafficService(origin: Location, destination: Location, time: ZonedDateTime): Future[Option[Route]]

  def callPublicTransportService(origin: Location, destination: Location, time: ZonedDateTime): Future[Option[PublicTransportAdvice]]

  def callSimilarArtistsService(event: Event): Future[Seq[Artist]]

  def callArtistCalendarService(artist: Artist, location: Location): Future[Event]


  //  def callTrafficService(origin: Location, destintion: Location, time: ZonedDateTime): Future[Option[Route]]


}
