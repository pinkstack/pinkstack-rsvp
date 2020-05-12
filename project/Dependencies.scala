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
    val cats = "2.1.1"
    val kafkaAvroSerializer = "5.4.1"
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

  lazy val avro: Seq[ModuleID] = Seq(
    "io.confluent" % "kafka-avro-serializer" % V.kafkaAvroSerializer,
    "org.apache.avro" % "avro" % V.avro
  )

  lazy val cats: Seq[ModuleID] = Seq(
    "org.typelevel" %% "cats-core" % V.cats
  )

  lazy val tests: Seq[ModuleID] = Seq(
    "org.scalactic" %% "scalactic" % "3.1.1",
    "org.scalatest" %% "scalatest" % "3.1.1" % "test"
  )
}