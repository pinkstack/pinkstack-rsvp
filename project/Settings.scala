import sbt._
import sbt.Keys._
import sbt.nio.Keys._
import sbt.{Def, Resolver, _}
import com.typesafe.sbt.SbtNativePackager.autoImport._
import com.typesafe.sbt.packager.docker.Cmd
import com.typesafe.sbt.packager.docker.DockerPlugin.autoImport._

object Settings {
  lazy val sharedSettings = Seq(
    Compile / packageDoc / mappings := Seq(),
    Global / onChangedBuildSource := ReloadOnSourceChanges,

    organization := "com.pinkstack.rsvp",
    scalaVersion := "2.13.1",

    Test / fork := true,
    Test / javaOptions ++= Seq("-Xmx1g"),
    scalacOptions ++= Seq(
      "-deprecation",
      "-encoding",
      "UTF-8",
      "-feature",
      "-explaintypes",
      "-unchecked",
      "-Xlint:-unused,_",
      "-language:implicitConversions",
      "-language:higherKinds",
      "-language:postfixOps",
      "-Yrangepos"
    ),
    resolvers ++= Seq(
      Resolver.bintrayRepo("ovotech", "maven"),
      "Confluent Maven Repository" at "https://packages.confluent.io/maven/"
    )
  )

  lazy val dockerSettings = Seq(
    dockerUsername := Some("pinkstack"),
    dockerUpdateLatest := true,
    dockerAliases ++= Seq(dockerAlias.value.withTag(Option("local"))),
    dockerBaseImage := "azul/zulu-openjdk-alpine:11",
    dockerExposedPorts ++= Seq(9095, 5266),
    dockerCommands := dockerCommands.value.flatMap {
      case add@Cmd("RUN", args@_*) if args.contains("id") =>
        List(
          Cmd("RUN", "apk add --no-cache bash jq curl udev"),
          Cmd("ENV", "SBT_VERSION", sbtVersion.value),
          Cmd("ENV", "SCALA_VERSION", scalaVersion.value),
          add
        )
      case other => List(other)
    }
  )
}
