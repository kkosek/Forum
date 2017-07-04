name := "Forum"

version := "1.0"

scalaVersion := "2.12.2"

libraryDependencies ++= Seq(
  "org.slf4j" % "slf4j-simple" % "latest.integration",
  "com.typesafe.akka" %% "akka-slf4j" % "latest.integration",
  "com.typesafe.slick" %% "slick" % "latest.integration",
  "com.typesafe.slick" %% "slick-hikaricp" % "latest.integration",
  "com.typesafe.akka" % "akka-actor_2.12" % "latest.integration" ,
  "com.typesafe.akka" %% "akka-http" % "latest.integration",
  "com.typesafe.akka" %% "akka-http-spray-json" % "latest.integration",
  "org.postgresql" % "postgresql" % "latest.integration",
  "org.scalactic" %% "scalactic" % "latest.integration",
  "org.scalatest" %% "scalatest" % "latest.integration" % "test",
  "com.h2database" % "h2" % "latest.integration"
)

flywayUrl := "jdbc:postgresql://localhost:5432/forumdb"

flywayUser := "postgres"

flywayPassword := "secret"