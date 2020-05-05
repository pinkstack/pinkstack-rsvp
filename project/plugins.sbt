logLevel := Level.Warn

addSbtPlugin("io.spray" % "sbt-revolver" % "0.9.1")
addSbtPlugin("com.cavorite" % "sbt-avro" % "2.0.0")
addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.7.1")

libraryDependencies += "org.apache.avro" % "avro-compiler" % "1.9.2"
