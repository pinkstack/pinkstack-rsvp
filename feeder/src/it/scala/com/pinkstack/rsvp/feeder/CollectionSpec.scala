package com.pinkstack.rsvp.feeder

import akka.NotUsed
import akka.kafka.scaladsl.{Consumer, Producer}
import akka.kafka.testkit.KafkaTestkitTestcontainersSettings
import akka.kafka.testkit.scaladsl.{ScalatestKafkaSpec, TestcontainersKafkaPerClassLike}
import akka.parboiled2.Repeated
import akka.stream.scaladsl._
import akka.stream.testkit.scaladsl.StreamTestKit.assertAllStagesStopped
import org.apache.kafka.common.config.TopicConfig
import org.scalatest.concurrent.{Eventually, ScalaFutures}
import akka.http.scaladsl.model.ws.{Message, TextMessage}
import akka.kafka.Subscriptions
import com.pinkstack.rsvp.domain.intake.{Event, Group, Member, RSVP, Venue}
import io.circe._
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should._
import org.scalatest.flatspec._
import org.scalatest.wordspec.AnyWordSpecLike

import scala.concurrent.Future
import scala.concurrent.duration._

abstract class SpecBase(kafkaPort: Int)
  extends ScalatestKafkaSpec(kafkaPort)
    with AnyWordSpecLike
    with Matchers
    with ScalaFutures
    with Eventually
    with Repeated {

  protected def this() = this(kafkaPort = -1)
}

class CollectionSpec extends SpecBase with TestcontainersKafkaPerClassLike with ScalaFutures with Matchers {
  override implicit val patienceConfig: PatienceConfig = PatienceConfig(45.seconds, 1.second)

  override val testcontainersSettings: KafkaTestkitTestcontainersSettings =
    KafkaTestkitTestcontainersSettings(system)
      .withNumBrokers(1)
      .withInternalTopicsReplicationFactor(1)
      .withConfigureKafka { brokerContainers =>
        brokerContainers.foreach(_.withEnv("KAFKA_AUTO_CREATE_TOPICS_ENABLE", "true"))
      }

  def buildRSVP: RSVP =
    RSVP(1, 1, "public", "yes", 0, Member(1, None, "Oto"),
      Event("event", "id", "https://event", Some(1)),
      Group("group", "SI", None, "si-x", 1, "x", None, None, List.empty),
      Some(Venue("venue", 1, 1, 222)))

  "Collection" should {
    // TODO: This is leaking.
    "emit messages" in /* assertAllStagesStopped */ {
      waitUntilCluster()(_.nodes().get().size == testcontainersSettings.numBrokers)

      val topic = createTopic(0, 1, 1, Map(
        TopicConfig.MIN_IN_SYNC_REPLICAS_CONFIG -> "1"
      ))

      def adjustedSettings =
        Configuration.loadFeedAppSettings.map { s =>
          s.copy(rsvps_topic = topic, bootstap_servers = producerDefaults.properties("bootstrap.servers"))
        }

      // Consumer
      val groupId = "G1"
      val consumerConfig = consumerDefaults
        .withGroupId(groupId)
        .withProperty(ConsumerConfig.METADATA_MAX_AGE_CONFIG, "100")

      val totalMessages = 1
      val consumerMatValue: Future[Long] =
        Consumer.plainSource(consumerConfig, Subscriptions.topics(topic))
          .scan(0L)((c, _) => c + 1)
          .takeWhile(count => count < totalMessages, inclusive = true)
          .runWith(Sink.last)

      waitUntilConsumerSummary(groupId) {
        case singleConsumer :: Nil => singleConsumer.assignment.topicPartitions.size == 1
      }

      // Producer
      val _ = for {
        feederSettings <- adjustedSettings
        app = {
          val r = Source.single(TextMessage.Strict(buildRSVP.asJson.noSpaces))
            .runWith(Collection.sink(feederSettings, system, ec))
          r.futureValue
        }
      } yield app

      assert(consumerMatValue.futureValue >= totalMessages)
    }
  }
}
