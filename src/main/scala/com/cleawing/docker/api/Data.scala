package com.cleawing.docker.api

import org.json4s.NoTypeHints
import org.json4s.jackson.Serialization

object Data {
  object Implicits {
    implicit val formats = Serialization.formats(NoTypeHints)
  }

  sealed trait Response

  case class Pong(msg: String) extends Response

  case class Version(
    Version: String,
    Os: String,
    KernelVersion: String,
    GoVersion: String,
    GitCommit: String,
    Arch: String,
    ApiVersion: String
  ) extends Response

  case class Info
  (
    ID: String,
    Containers: Int,
    Images: Int,
    Driver: String,
    DriverStatus: Seq[Seq[String]],
    MemoryLimit: Boolean,
    SwapLimit: Boolean,
    CpuCfsPeriod: Boolean,
    CpuCfsQuota: Boolean,
    IPv4Forwarding: Boolean,
    Debug: Boolean,
    NFd: Int,
    OomKillDisable: Boolean,
    NGoroutines: Int,
    SystemTime: String,
    ExecutionDriver: String,
    LoggingDriver: String,
    NEventsListener: Int,
    KernelVersion: String,
    OperatingSystem: String,
    IndexServerAddress: String,
    RegistryConfig: Internals.RegistryConfig,
    InitSha1: String,
    InitPath: String,
    NCPU: Int,
    MemTotal: Int,
    DockerRootDir: String,
    HttpProxy: String,
    HttpsProxy: String,
    NoProxy: String,
    Name: String,
    labels: Seq[String],
    ExperimentalBuild: Boolean
  ) extends Response

  sealed trait Error
  // Errors
  case class UnexpectedError(msg: String) extends Error
  case class NotFound(msg: String) extends Error
  case class BadParameter(msg: String) extends Error
  case class ServerError(msg: String) extends Error

  // Failures
  sealed trait Failure extends Error { val cause: Throwable }
  case class ConnectionFailed(cause: Throwable) extends Failure
  case class UnexpectedFailure(cause: Throwable) extends Failure

  object Internals {
    case class RegistryConfig
    (
      InsecureRegistryCIDRs: Seq[String],
      IndexConfigs: Map[String, IndexConfig]
    )

    case class IndexConfig
    (
      Name: String,
      Mirrors: Option[Seq[String]],
      Secure: Boolean,
      Official: Boolean
    )
  }
}
