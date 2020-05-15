package com.pinkstack.rsvp.feeder

import akka.actor.ActorSystem
import akka.http.scaladsl._
import akka.http.scaladsl.model.ws._
import akka.stream.scaladsl._
import com.typesafe.scalalogging.LazyLogging
import io.circe.Error
import kamon.Kamon

import scala.concurrent.ExecutionContext
import scala.util.Success

object FeederApp extends App with LazyLogging {
  Kamon.init()

  implicit val system: ActorSystem = ActorSystem("FeederApp")
  implicit val ec: ExecutionContext = system.dispatcher

  val p: Either[Error, Unit] = for {
    appSettings <- Configuration.loadFeedAppSettings
    outFlow = {
      val flow = Flow.fromSinkAndSourceMat(Collection.sink(appSettings, system, ec), Source.maybe[Message])(Keep.left)
      val (upgradeResponse, _) = Http().singleWebSocketRequest(WebSocketRequest(appSettings.rsvps_ws_url), flow)
      upgradeResponse.onComplete {
        case Success(_) =>
          logger.info("Connected and running producer,...")
        case _ =>
          logger.error("Problem establishing connection.")
          system.terminate()
      }
    }
  } yield outFlow

  p match {
    case Left(e) =>
      logger.error(e.getMessage)
      system.terminate()
    case _ => logger.info("Booted.")
  }
}
