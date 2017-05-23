name := "ForumSBT"

version := "1.0"

scalaVersion := "2.12.2"

libraryDependencies ++= Seq(
  "com.typesafe.akka" % "akka-actor_2.12" % "2.5.1",
  "com.typesafe.akka" %% "akka-agent" % "2.5.1",
  "com.typesafe.akka" %% "akka-camel" % "2.5.1",
  "com.typesafe.akka" %% "akka-cluster" % "2.5.1",
  "com.typesafe.akka" %% "akka-cluster-metrics" % "2.5.1",
  "com.typesafe.akka" %% "akka-cluster-sharding" % "2.5.1",
  "com.typesafe.akka" %% "akka-cluster-tools" % "2.5.1",
  "com.typesafe.akka" %% "akka-distributed-data" % "2.5.1",
  "com.typesafe.akka" %% "akka-multi-node-testkit" % "2.5.1",
  "com.typesafe.akka" %% "akka-osgi" % "2.5.1",
  "com.typesafe.akka" %% "akka-persistence" % "2.5.1",
  "com.typesafe.akka" %% "akka-persistence-query" % "2.5.1",
  "com.typesafe.akka" %% "akka-persistence-tck" % "2.5.1",
  "com.typesafe.akka" %% "akka-remote" % "2.5.1",
  "com.typesafe.akka" %% "akka-slf4j" % "2.5.1",
  "com.typesafe.akka" % "akka-stream_2.12" % "2.5.1",
  "com.typesafe.akka" % "akka-stream-testkit_2.12" % "2.5.1",
  "com.typesafe.akka" % "akka-testkit_2.12" % "2.5.1",
  "com.typesafe.akka" %% "akka-typed" % "2.5.1",
  "com.typesafe.akka" %% "akka-contrib" % "2.5.1",
  "com.typesafe.akka" % "akka-http-core_2.12" % "10.0.6",
  "com.typesafe.akka" % "akka-http_2.12" % "10.0.6",
  "com.typesafe.akka" % "akka-http-testkit_2.12" % "10.0.6",
  "com.typesafe.akka" % "akka-http-spray-json_2.12" % "10.0.6",
  "com.typesafe.akka" % "akka-http-jackson_2.12" % "10.0.6",
  "com.typesafe.akka" % "akka-http-xml_2.12" % "10.0.6",
  "io.spray" % "spray-json_2.12" % "1.3.3",
  "org.postgresql" % "postgresql" % "9.4-1200-jdbc41",
  "com.typesafe.slick" % "slick_2.12" % "3.2.0" exclude("org.slf4j","slf4j-log4j12"),
  "com.typesafe.slick" % "slick-hikaricp_2.12" % "3.2.0" exclude("org.slf4j","slf4j-log4j12"),
  "org.slf4j" % "slf4j-nop" % "1.7.23" exclude("org.slf4j","slf4j-log4j12")
)

