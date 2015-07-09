import sbt._

object Dependencies {
  object Versions {
    val scalaTest = "2.2.5"
  }

  lazy val scalaTest  = "org.scalatest" %%  "scalatest" % Versions.scalaTest
}
