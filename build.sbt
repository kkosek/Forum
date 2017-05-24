name := "Forum"

version := "1.0"

scalaVersion := "2.12.2"

libraryDependencies ++= Seq(
  "org.slf4j" % "slf4j-simple" % "latest.integration",
  "com.typesafe.akka" %% "akka-slf4j" % "latest.integration",
  "com.typesafe.slick" %% "slick" % "latest.integration",
  "com.typesafe.slick" %% "slick-hikaricp" % "latest.integration",
  "com.typesafe.akka" % "akka-actor_2.12" % "latest.integration" ,
  "com.typesafe.akka" %% "akka-agent" % "latest.integration",
  "com.typesafe.akka" %% "akka-camel" % "latest.integration",
  "com.typesafe.akka" %% "akka-cluster" % "latest.integration",
  "com.typesafe.akka" %% "akka-cluster-metrics" % "latest.integration",
  "com.typesafe.akka" %% "akka-cluster-sharding" % "latest.integration",
  "com.typesafe.akka" %% "akka-cluster-tools" % "latest.integration",
  "com.typesafe.akka" %% "akka-distributed-data" % "latest.integration",
  "com.typesafe.akka" %% "akka-multi-node-testkit" % "latest.integration",
  "com.typesafe.akka" %% "akka-osgi" % "latest.integration",
  "com.typesafe.akka" %% "akka-persistence" % "latest.integration",
  "com.typesafe.akka" %% "akka-persistence-query" % "latest.integration",
  "com.typesafe.akka" %% "akka-persistence-tck" % "latest.integration",
  "com.typesafe.akka" %% "akka-remote" % "latest.integration",
  "com.typesafe.akka" %% "akka-typed" % "latest.integration",
  "com.typesafe.akka" %% "akka-contrib" % "latest.integration",
  "com.typesafe.akka" %% "akka-http-core" % "latest.integration",
  "com.typesafe.akka" %% "akka-http" % "latest.integration",
  "com.typesafe.akka" %% "akka-http-testkit" % "latest.integration",
  "com.typesafe.akka" %% "akka-http-spray-json" % "latest.integration",
  "com.typesafe.akka" %% "akka-http-jackson" % "latest.integration",
  "com.typesafe.akka" %% "akka-http-xml" % "latest.integration",
  "io.spray" %% "spray-json" % "latest.integration",
  "org.postgresql" % "postgresql" % "latest.integration"
)



