package com.pinkstack.rsvp.domain.intake

import io.circe.generic.semiauto._
import io.circe.{Decoder, Encoder}

final case class RSVP(rsvp_id: Int,
                      mtime: Long,
                      visibility: String,
                      response: String,
                      guests: Int,
                      member: Member,
                      event: Event,
                      group: Group,
                      venue: Venue)

object RSVP {
  implicit val decoder: Decoder[RSVP] = deriveDecoder[RSVP]
  implicit val encoder: Encoder[RSVP] = deriveEncoder[RSVP]
}

final case class Member(member_id: Int, photo: Option[String], member_name: String)

object Member {
  implicit val decoder: Decoder[Member] = deriveDecoder[Member]
  implicit val encoder: Encoder[Member] = deriveEncoder[Member]
}

final case class Event(event_name: String, event_id: String, time: Long, event_url: String)

object Event {
  implicit val decoder: Decoder[Event] = deriveDecoder[Event]
  implicit val encoder: Encoder[Event] = deriveEncoder[Event]
}

final case class Group(group_city: String,
                       group_country: String,
                       group_state: Option[String],
                       group_urlname: String,
                       group_id: Int,
                       group_name: String,
                       group_lon: Option[Float],
                       group_lat: Option[Float],
                       group_topics: List[GroupTopicsRecord])

object Group {
  implicit val decoder: Decoder[Group] = deriveDecoder[Group]
  implicit val encoder: Encoder[Group] = deriveEncoder[Group]
}

final case class GroupTopicsRecord(urlkey: String, topic_name: String)

object GroupTopicsRecord {
  implicit val decoder: Decoder[GroupTopicsRecord] = deriveDecoder[GroupTopicsRecord]
  implicit val encoder: Encoder[GroupTopicsRecord] = deriveEncoder[GroupTopicsRecord]
}

final case class Venue(venue_name: String, lon: Float, lat: Float, venue_id: Int)

object Venue {
  implicit val decoder: Decoder[Venue] = deriveDecoder[Venue]
  implicit val encoder: Encoder[Venue] = deriveEncoder[Venue]
}