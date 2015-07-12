import sbt._

object Dependencies {
  object Versions {
    val scalazScalaTest = "0.2.3"
    val scalaTest = "2.2.5"
  }

  lazy val scalazScalaTest  = "org.typelevel" %% "scalaz-scalatest" % Versions.scalazScalaTest % "test"
  lazy val scalaTest  = "org.scalatest" %%  "scalatest" % Versions.scalaTest % "test"
}
