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

  // Conversion to RSVPDetected
  /*
  implicit def rsvpToRSVPDetected(r: RSVP): com.rsvpbox.models.RSVPDetected = {
    import com.rsvpbox.models._

    val groupTopics = r.group.group_topics.map { tr =>
      new com.rsvpbox.models.GroupTopicsRecord(tr.urlkey, tr.topic_name)
    }.asJava

    new RSVPDetected(
      new com.rsvpbox.models.Venue(r.venue.venue_name, r.venue.lon, r.venue.lat, r.venue.venue_id.asInstanceOf[Int]),
      r.visibility,
      r.response,
      r.guests,
      new com.rsvpbox.models.Member(r.member.member_id, r.member.photo.getOrElse(""), r.member.member_name),
      r.rsvp_id,
      r.mtime,
      new com.rsvpbox.models.Event(r.event.event_name, r.event.event_id, r.event.time, r.event.event_url),
      new com.rsvpbox.models.Group(
        groupTopics,
        r.group.group_city,
        r.group.group_country,
        r.group.group_id,
        r.group.group_name,
        r.group.group_lon.getOrElse(-1.0).asInstanceOf[Float],
        r.group.group_urlname,
        r.group.group_state.getOrElse("Unknown"),
        r.group.group_lat.getOrElse(-1.0).asInstanceOf[Float]
      )
    )
  }
   */
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