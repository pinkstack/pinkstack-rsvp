package com.pinkstack.rsvp.feeder

import cats._
import cats.implicits._
import akka.{Done, NotUsed}
import akka.actor.ActorSystem
import akka.http.scaladsl.model.ws.{Message, TextMessage}
import akka.kafka.ProducerSettings
import akka.kafka.scaladsl.Producer
import akka.stream.{ActorAttributes, Attributes, Supervision}
import akka.stream.scaladsl.{Flow, Keep, Sink}
import com.pinkstack.rsvp.domain.intake.RSVP
import com.sksamuel.avro4s.RecordFormat
import com.typesafe.scalalogging.LazyLogging
import io.circe.{DecodingFailure, Error, parser}
import io.confluent.kafka.serializers.{AbstractKafkaSchemaSerDeConfig, KafkaAvroDeserializerConfig, KafkaAvroSerializer}
import kamon.Kamon
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.{LongSerializer, Serializer}

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

object Collection extends LazyLogging {
  type Key = java.lang.Long
  type Value = org.apache.avro.generic.GenericRecord

  def sink(implicit settings: FeederSettings,
           system: ActorSystem,
           ec: ExecutionContext): Sink[Message, Future[Done]] = {
    val kafkaAvroSerDeConfig = Map[String, Any](
      AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG -> settings.schema_registry_url,
      KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG -> true.toString
    )

    val producerSettings: ProducerSettings[Key, Value] = {
      import scala.jdk.CollectionConverters._

      val kafkaAvroSerializer = new KafkaAvroSerializer()
      kafkaAvroSerializer.configure(kafkaAvroSerDeConfig.asJava, false)

      ProducerSettings(system, new LongSerializer,
        kafkaAvroSerializer.asInstanceOf[Serializer[Value]])
        .withBootstrapServers(settings.bootstap_servers)
    }

    flow.alsoTo(Producer.plainSink(producerSettings)).toMat(Sink.ignore)(Keep.right)
  }

  def flow(implicit settings: FeederSettings,
           system: ActorSystem,
           ec: ExecutionContext): Flow[Message, ProducerRecord[Key, Value], NotUsed] = {
    val parse: String => Either[Error, RSVP] = { s: String =>
      parser.decode[RSVP](s) match {
        case Left(value: Exception) =>
          logger.error(s"Failed parsing => ${s}")
          value.asLeft
        case Right(value) =>
          value.asRight
      }

    }

    val toAvro: RSVP => ProducerRecord[Key, Value] = { rsvp =>
      new ProducerRecord[Key, Value](settings.rsvps_topic, rsvp.rsvp_id, implicitly[RecordFormat[RSVP]].to(rsvp))
    }

    val rsvpsCounter = Kamon.counter("feeder.rsvps.pushed").withoutTags()
    val counterSink = Sink.foreach[ProducerRecord[Key, Value]](v =>
      rsvpsCounter.increment()
    )

    val parseMessages: PartialFunction[Message, Future[Either[Error, RSVP]]] = {
      case TextMessage.Strict(text) => Future.successful(parse(text))
      case TextMessage.Streamed(stream) => stream.limit(100)
        .completionTimeout(5.seconds)
        .runFold("")(_ + _)
        .flatMap(text => Future.successful(parse(text)))
    }

    val supervisionDecider: Supervision.Decider = {
      case decodingFailure: DecodingFailure =>
        logger.error(s"DecodingFailure => ${decodingFailure.getMessage()}")
        Supervision.Resume
    }

    Flow[Message]
      .collect(parseMessages)
      .mapAsync(parallelism = 3)(identity)
      .collect {
        case Right(rsvp: RSVP) => rsvp
        case Left(e: Exception) => throw e
      }
      .log("feeder")
      .addAttributes(Attributes.logLevels(
        onFailure = Attributes.LogLevels.Error,
        onFinish = Attributes.LogLevels.Info)
      )
      .map(toAvro)
      .alsoTo(counterSink)
      .named("collectionFlow")
      .withAttributes(ActorAttributes.supervisionStrategy(supervisionDecider))
  }
}