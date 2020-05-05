package com.pinkstack.rsvp.feeder

import io.circe._
import io.circe.config.parser
import io.circe.generic.auto._

trait Settings

final case class FeederSettings(rsvps_ws_url: String,
                                rsvps_topic: String,
                                bootstap_servers: String,
                                schema_registry_url: String) extends Settings

case class AppSettings(feederSettings: FeederSettings)

object Configuration {
  def loadFeedAppSettings: Either[Error, FeederSettings] =
    parser.decodePath[FeederSettings]("feeder-app")
}
