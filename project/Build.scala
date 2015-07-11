import org.stormenroute.mecha._
import sbt._
import sbt.Keys._

object Build extends MechaRepoBuild {
  lazy val buildSettings = Defaults.coreDefaultSettings ++
    MechaRepoPlugin.defaultSettings ++ Seq(
    name := "finagle-docker",
    scalaVersion := "2.11.7",
    version := "0.1",
    organization := "com.cleawing",
    libraryDependencies ++= superRepoDependencies("finagle-docker") ++
      Seq(Dependencies.scalaz, Dependencies.json4s, Dependencies.jackson,
        Dependencies.scalaTest, Dependencies.scalazScalaTest)
  )

  def repoName = "finagle-docker"

  lazy val finagleServices: Project = Project(
    "finagle-docker",
    file("."),
    settings = buildSettings
  ) dependsOnSuperRepo
}
