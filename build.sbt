name := "machinepark-monitor"

version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.4.10",
  "com.typesafe.akka" %% "akka-http-experimental" % "2.4.10",
  "com.typesafe.akka" %% "akka-http-spray-json-experimental" % "2.4.10",
  "com.typesafe.akka" %% "akka-stream" % "2.4.10",
  "com.typesafe.akka" %% "akka-slf4j" % "2.4.10",
  "ch.qos.logback" % "logback-classic" % "1.1.3",

  "org.scalatest" %% "scalatest" % "3.0.0" % "test"
)