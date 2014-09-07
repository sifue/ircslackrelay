import AssemblyKeys._

assemblySettings

name := "ircslackrelay"

version := "1.0.3"

scalaVersion := "2.11.2"

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies ++= List(
  "org.slf4j" % "slf4j-nop" % "1.6.4",
  "com.typesafe" % "config" % "1.2.1",
  "com.sorcix" % "sirc" % "1.1.5",
  "com.typesafe.akka" %% "akka-actor" % "2.3.6",
  "com.typesafe.akka" %% "akka-slf4j" % "2.3.6",
  "net.databinder.dispatch" %% "dispatch-core" % "0.11.2"
)

mainClass in assembly := Some("org.soichiro.ircslackrelay.Main")
