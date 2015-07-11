import sbt._

object Dependencies {
  object Versions {
    val scalaz = "7.1.3"
    val json4s = "3.3.0.RC2"
    val jackson = "2.5.4"
    val scalazScalaTest = "0.2.3"
    val scalaTest = "2.2.5"
  }

  lazy val scalaz  = "org.scalaz" %% "scalaz-core" % Versions.scalaz
  lazy val json4s = "org.json4s" %% "json4s-jackson" % Versions.json4s
  lazy val jackson = "com.fasterxml.jackson.core" % "jackson-databind" % Versions.jackson
  lazy val scalazScalaTest  = "org.typelevel" %% "scalaz-scalatest" % Versions.scalazScalaTest % "test"
  lazy val scalaTest  = "org.scalatest" %%  "scalatest" % Versions.scalaTest % "test"
}
