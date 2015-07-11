import sbt._

object Dependencies {
  object Versions {
    val scalaz = "7.1.3"
    val scalazScalaTest = "0.2.3"
    val scalaTest = "2.2.5"
  }

  lazy val scalaz  = "org.scalaz" %% "scalaz-core" % Versions.scalaz
  lazy val scalazScalaTest  = "org.typelevel" %% "scalaz-scalatest" % Versions.scalazScalaTest
  lazy val scalaTest  = "org.scalatest" %%  "scalatest" % Versions.scalaTest
}
