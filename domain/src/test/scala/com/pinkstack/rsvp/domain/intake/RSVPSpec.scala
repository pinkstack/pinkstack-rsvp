package com.pinkstack.rsvp.domain.intake

import org.scalatest.matchers.should._
import org.scalatest.flatspec._
import io.circe._
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._
import org.scalatest.EitherValues

class RSVPSpec extends AnyFlatSpec with Matchers with EitherValues {
  val event: Event = Event("event", "id", "https://event", Some(1))
  val member: Member = Member(1, None, "Oto")
  val group: Group = Group("group", "SI", None, "si-x", 1, "x", None, None, List.empty)
  val venue: Venue = Venue("venue", 1, 1, 222)
  val rsvp: RSVP = RSVP(1, 1, "public", "yes", 0, member, event, group, Some(venue))

  "RSVP" should "have structure" in {
    rsvp.event shouldEqual event
    rsvp.member shouldEqual member
    rsvp.group shouldEqual group
    rsvp.venue shouldEqual Some(venue)
  }

  it should "encodes/decodes to JSON" in {
    val jsonString: String = rsvp.asJson.noSpaces
    val parsed: Either[Error, RSVP] = decode[RSVP](jsonString)
    parsed.getOrElse {
      throw new Exception("Should not happen.")
    } shouldEqual rsvp
  }
}
