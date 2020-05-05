package com.pinkstack.rsvp.domain

import com.pinkstack.rsvp.domain.collected.RSVPDetected
import com.pinkstack.rsvp.domain.intake.RSVP

import scala.jdk.CollectionConverters._

object IntakeToCollected {
  val toCollected: RSVP => RSVPDetected = { r: RSVP =>

    import com.pinkstack.rsvp.domain.collected._
    val groupTopics = r.group.group_topics.map { tr =>
      new GroupTopicsRecord(tr.urlkey, tr.topic_name)
    }.asJava

    new RSVPDetected(
      new Venue(r.venue.venue_name, r.venue.lon, r.venue.lat, r.venue.venue_id.asInstanceOf[Int]),
      r.visibility,
      r.response,
      r.guests,
      new Member(r.member.member_id, r.member.photo.getOrElse(""), r.member.member_name),
      r.rsvp_id,
      r.mtime,
      new Event(r.event.event_name, r.event.event_id, r.event.time, r.event.event_url),
      new Group(
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
}
