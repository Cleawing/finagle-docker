package com.cleawing.docker

import com.cleawing.docker.api.{DockerClientImpl, Data}
import com.cleawing.finagle.http.TLSSupport
import com.typesafe.config.ConfigFactory

import scala.concurrent.{ExecutionContext, Future}
import scalaz.\/

trait DockerClient {
  implicit val ec: ExecutionContext

  def close() : Future[Unit]

  def ping() : Future[DockerClient.Response]
  def version() : Future[DockerClient.Response]
  def info() : Future[DockerClient.Response]
}

object DockerClient {
  type Response = Data.Error \/ Data.Response
  val config = ConfigFactory.load()

  def apply()(implicit ec: ExecutionContext) : DockerClient = {
    val port = if (config.getBoolean("docker.tls")) config.getInt("docker.tlsPort") else config.getInt("docker.port")
    apply(config.getString("docker.host"), port, config.getBoolean("docker.tls"))
  }

  def apply(tlsOn: Boolean)(implicit ec: ExecutionContext) : DockerClient = {
    val port = if (tlsOn) config.getInt("docker.tlsPort") else config.getInt("docker.port")
    apply(config.getString("docker.host"), port, tlsOn)
  }

  def apply(tlsOn: Boolean, tls: Option[TLSSupport])(implicit ec: ExecutionContext) : DockerClient = {
    val port = if (tlsOn) config.getInt("docker.tlsPort") else config.getInt("docker.port")
    apply(config.getString("docker.host"), port, tlsOn, tls)
  }

  def apply(host: String)(implicit ec: ExecutionContext) : DockerClient = {
    val port = if (config.getBoolean("docker.tls")) config.getInt("docker.tlsPort") else config.getInt("docker.port")
    apply(host, port, config.getBoolean("docker.tls"))
  }

  def apply(host: String, tlsOn: Boolean)(implicit ec: ExecutionContext) : DockerClient = {
    val port = if (tlsOn) config.getInt("docker.tlsPort") else config.getInt("docker.port")
    apply(host, port, tlsOn)
  }

  def apply(port: Int)(implicit ec: ExecutionContext) : DockerClient = {
    apply(config.getString("docker.host"), port, config.getBoolean("docker.tls"))
  }

  def apply(port: Int, tlsOn: Boolean)(implicit ec: ExecutionContext) : DockerClient = {
    apply(config.getString("docker.host"), port, tlsOn)
  }

  def apply(host: String, port: Int)(implicit ec: ExecutionContext) : DockerClient = {
    new DockerClientImpl(host, port, config.getBoolean("docker.tls"))
  }

  def apply(host: String, port: Int, tlsOn: Boolean)(implicit ec: ExecutionContext) : DockerClient = {
    apply(host, port, tlsOn, if (tlsOn) Some(TLSSupport(config.getString("docker.cert_path"))) else None)
  }

  def apply(host: String, port: Int, tls_on: Boolean, tls: Option[TLSSupport])(implicit ec: ExecutionContext) : DockerClient = {
    new DockerClientImpl(host, port, tls_on, tls)
  }
}