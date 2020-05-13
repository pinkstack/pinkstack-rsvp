import Dependencies._
import Settings._
import scala.sys.process._


lazy val feeder = (project in file("feeder"))
  .settings(sharedSettings: _*)
  .settings(dockerSettings: _*)
  .settings(
    name := "rsvp-feeder",
    Compile / mainClass := Some("com.pinkstack.rsvp.feeder.FeederApp"),
    publish / aggregate := false,
    libraryDependencies ++= akka ++ akkaHttp ++ configLibs ++ logging ++ kamon ++ circe
      ++ avro ++ avroKafka ++ cats ++ tests,
    javaAgents ++= javaAgentsLibs
  )
  .dependsOn(domain)
  .enablePlugins(JavaServerAppPackaging, DockerPlugin, JavaAgent)

lazy val domain = (project in file("domain"))
  .settings(sharedSettings: _*)
  .settings(
    name := "rsvp-domain",
    libraryDependencies ++= avro ++ cats ++ circe ++ tests
  )

// Commands and tasks
addCommandAlias("dockerize", ";test;compile;docker:publishLocal")

lazy val tagPushFeederApp: TaskKey[Unit] = taskKey[Unit]("Tags FeederApp image and pushes it to Docker Hub")
tagPushFeederApp := {
  "./bin/tag-push-feeder-app.sh" !
}