import org.stormenroute.mecha._
import sbt._
import sbt.Keys._

object Build extends MechaRepoBuild {
  lazy val buildSettings = Defaults.coreDefaultSettings ++
    MechaRepoPlugin.defaultSettings ++ Seq(
    name := "finch-docker",
    scalaVersion := "2.11.7",
    version := "0.1",
    organization := "com.cleawing",
    libraryDependencies ++= superRepoDependencies("finch-docker") ++
      Seq(Dependencies.scalaTest)
  )

  def repoName = "finch-docker"

  lazy val finagleServices: Project = Project(
    "finch-docker",
    file("."),
    settings = buildSettings
  ) dependsOnSuperRepo
}
