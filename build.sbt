name := "Forum"

version := "1.0"

scalaVersion := "2.12.2"

libraryDependencies ++= Seq(
  "org.slf4j" % "slf4j-simple" % "latest.integration",
  "com.typesafe.akka" %% "akka-slf4j" % "latest.integration",
  "com.typesafe.slick" %% "slick" % "latest.integration",
  "com.typesafe.slick" %% "slick-hikaricp" % "latest.integration",
  "com.typesafe.akka" %% "akka-http" % "latest.integration",
  "io.spray" %% "spray-json" % "latest.integration",
  "org.postgresql" % "postgresql" % "latest.integration",
  "org.scalactic" %% "scalactic" % "latest.integration",
  "org.scalatest" %% "scalatest" % "latest.integration" % "test"
)

flywayUrl := "jdbc:h2:file:./target/foobar"

flywayUser := "SA"

