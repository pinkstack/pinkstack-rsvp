import Dependencies._
import Settings._

lazy val feeder = (project in file("feeder"))
  .settings(sharedSettings: _*)
  .settings(dockerSettings: _*)
  .settings(
    name := "rsvp-feeder",
    Compile / mainClass := Some("com.pinkstack.rsvp.feeder.FeederApp"),
    publish / aggregate := false,
    libraryDependencies ++=
      akka ++ akkaHttp ++ configLibs ++ logging ++ circe
        ++ avro ++ cats
  )
  .dependsOn(domain)
  .enablePlugins(JavaServerAppPackaging, DockerPlugin)

lazy val domain = (project in file("domain"))
  .settings(sharedSettings: _*)
  .settings(
    name := "rsvp-domain",
    libraryDependencies ++=
      avro ++ cats ++ circe
  )

addCommandAlias("dockerize", ";compile;docker:publishLocal")
