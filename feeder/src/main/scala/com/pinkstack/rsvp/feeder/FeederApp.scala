package com.pinkstack.rsvp.feeder

import akka.NotUsed
import akka.actor.ActorSystem
import akka.http.scaladsl._
import akka.http.scaladsl.model.ws._
import akka.kafka.ProducerSettings
import akka.kafka.scaladsl.Producer
import akka.stream.Attributes
import akka.stream.scaladsl._
import com.pinkstack.rsvp.domain.intake.RSVP
import com.sksamuel.avro4s.RecordFormat
import com.typesafe.scalalogging.LazyLogging
import io.circe.{Error, _}
import io.confluent.kafka.serializers.{AbstractKafkaSchemaSerDeConfig, KafkaAvroDeserializerConfig, KafkaAvroSerializer}

import org.apache.avro.generic.GenericRecord
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization._

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Success

object FeederApp extends App with LazyLogging {
  type Key = java.lang.Long

  implicit val system: ActorSystem = ActorSystem("FeederApp")
  implicit val ec: ExecutionContext = system.dispatcher

  def appSink(settings: FeederSettings): Sink[Message, NotUsed] = {
    val kafkaAvroSerDeConfig = Map[String, Any](
      AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG -> settings.schema_registry_url,
      KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG -> true.toString,
    )

    val producerSettings: ProducerSettings[Key, GenericRecord] = {
      import scala.jdk.CollectionConverters._

      val kafkaAvroSerializer = new KafkaAvroSerializer()
      kafkaAvroSerializer.configure(kafkaAvroSerDeConfig.asJava, false)

      ProducerSettings(system, new LongSerializer,
        kafkaAvroSerializer.asInstanceOf[Serializer[GenericRecord]])
        .withBootstrapServers(settings.bootstap_servers)
    }

    var parse: String => Either[Error, RSVP] = parser.decode[RSVP](_)

    val toAvro: RSVP => ProducerRecord[Key, GenericRecord] = { rsvp =>
      new ProducerRecord[Key, GenericRecord](
        settings.rsvps_topic,
        rsvp.rsvp_id,
        implicitly[RecordFormat[RSVP]].to(rsvp))
    }

    Flow[Message]
      .collect {
        case TextMessage.Strict(text) => Future.successful(parse(text))
        case TextMessage.Streamed(stream) => stream.limit(100)
          .completionTimeout(5.seconds)
          .runFold("")(_ + _)
          .flatMap(text => Future.successful(parse(text)))
      }
      .mapAsync(parallelism = 3)(identity)
      .collect { case Right(rsvp: RSVP) => rsvp }
      .log("feeder")
      .addAttributes(Attributes.logLevels(
        // onElement = Attributes.LogLevels.Info,
        onFailure = Attributes.LogLevels.Error,
        onFinish = Attributes.LogLevels.Info)
      )
      .map(toAvro)
      .to(Producer.plainSink(producerSettings))
  }

  val p: Either[Error, Unit] = for {
    appSettings <- Configuration.loadFeedAppSettings
    outFlow = {
      val flow = Flow.fromSinkAndSourceMat(appSink(appSettings), Source.maybe[Message])(Keep.left)
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
