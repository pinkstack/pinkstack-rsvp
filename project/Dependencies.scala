import sbt._

object Dependencies {

  object V {
    val akka = "2.6.4"
    val akkaHttp = "10.1.11"
    val akkaStreamKafka = "2.0.2"
    val typesafeConfig = "1.4.0"
    val circe = "0.12.3"
    val circeConfig = "0.7.0"
    val avro = "1.9.2"
    val avro4s = "3.1.0"
    val cats = "2.1.1"
    val kafkaAvroSerializer = "5.4.1"
    val kafkaStreamsAvroSerde = "5.5.0"
    val scalaLogging = "3.9.2"
    val logbackClassic = "1.2.3"
  }

  lazy val akka: Seq[ModuleID] = Seq(
    "com.typesafe.akka" %% "akka-actor" % V.akka,
    "com.typesafe.akka" %% "akka-stream" % V.akka,
    "com.typesafe.akka" %% "akka-stream-kafka" % V.akkaStreamKafka,
    "com.typesafe.akka" %% "akka-stream-testkit" % V.akka % Test
  )

  lazy val akkaHttp: Seq[ModuleID] = Seq(
    "com.typesafe.akka" %% "akka-http" % V.akkaHttp
  )

  lazy val kamon: Seq[ModuleID] = Seq(
    "io.kamon" %% "kamon-bundle" % "2.1.0",
    "io.kamon" %% "kamon-akka" % "2.1.0",
    "io.kamon" %% "kamon-prometheus" % "2.1.0",
    "io.kamon" %% "kamon-status-page" % "2.1.0",
    "io.kamon" %% "kamon-apm-reporter" % "2.1.0"
  )

  lazy val configLibs: Seq[ModuleID] = Seq(
    "com.typesafe" % "config" % V.typesafeConfig
  )

  lazy val logging: Seq[ModuleID] = Seq(
    "com.typesafe.akka" %% "akka-slf4j" % V.akka,
    "com.typesafe.scala-logging" %% "scala-logging" % V.scalaLogging,
    "ch.qos.logback" % "logback-classic" % V.logbackClassic
  )

  lazy val circe: Seq[ModuleID] = Seq(
    "io.circe" %% "circe-core" % V.circe,
    "io.circe" %% "circe-generic" % V.circe,
    "io.circe" %% "circe-parser" % V.circe,
    "io.circe" %% "circe-config" % V.circeConfig
  )

  lazy val avroKafka: Seq[ModuleID] = Seq(
    "io.confluent" % "kafka-avro-serializer" % V.kafkaAvroSerializer,
    "io.confluent" % "kafka-streams-avro-serde" % V.kafkaStreamsAvroSerde
  )

  lazy val avro: Seq[ModuleID] = Seq(
    "com.sksamuel.avro4s" %% "avro4s-core" % V.avro4s
  )

  lazy val cats: Seq[ModuleID] = Seq(
    "org.typelevel" %% "cats-core" % V.cats
  )

  lazy val tests: Seq[ModuleID] = Seq(
    "org.scalactic" %% "scalactic" % "3.1.1",
    "org.scalatest" %% "scalatest" % "3.1.1" % "test"
  )

  lazy val javaAgentsLibs: Seq[ModuleID] = Seq(
    "io.kamon" % "kanela-agent" % "1.0.5"
  )
}