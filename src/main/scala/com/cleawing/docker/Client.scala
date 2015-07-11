package com.cleawing.docker

import com.cleawing.docker.api.{RemoteClient, Data}
import com.cleawing.finagle.http.TLSSupport
import com.typesafe.config.ConfigFactory

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try
import scalaz.\/

trait Client {
  implicit val ec: ExecutionContext

  def close() : Future[Unit]

  def ping() : Future[Client.Response]
  def version() : Future[Client.Response]
  def info() : Future[Client.Response]
}

object Client {
  type Response = Try[Data.Error \/ Data.Response]
  val config = ConfigFactory.load()

  def apply()(implicit ec: ExecutionContext) : Client = {
    val port = if (config.getBoolean("docker.tls")) config.getInt("docker.tlsPort") else config.getInt("docker.port")
    apply(config.getString("docker.host"), port, config.getBoolean("docker.tls"))
  }

  def apply(tlsOn: Boolean)(implicit ec: ExecutionContext) : Client = {
    val port = if (tlsOn) config.getInt("docker.tlsPort") else config.getInt("docker.port")
    apply(config.getString("docker.host"), port, tlsOn)
  }

  def apply(tlsOn: Boolean, tls: Option[TLSSupport])(implicit ec: ExecutionContext) : Client = {
    val port = if (tlsOn) config.getInt("docker.tlsPort") else config.getInt("docker.port")
    apply(config.getString("docker.host"), port, tlsOn, tls)
  }

  def apply(host: String)(implicit ec: ExecutionContext) : Client = {
    val port = if (config.getBoolean("docker.tls")) config.getInt("docker.tlsPort") else config.getInt("docker.port")
    apply(host, port, config.getBoolean("docker.tls"))
  }

  def apply(host: String, tlsOn: Boolean)(implicit ec: ExecutionContext) : Client = {
    val port = if (tlsOn) config.getInt("docker.tlsPort") else config.getInt("docker.port")
    apply(host, port, tlsOn)
  }

  def apply(port: Int)(implicit ec: ExecutionContext) : Client = {
    apply(config.getString("docker.host"), port, config.getBoolean("docker.tls"))
  }

  def apply(port: Int, tlsOn: Boolean)(implicit ec: ExecutionContext) : Client = {
    apply(config.getString("docker.host"), port, tlsOn)
  }

  def apply(host: String, port: Int)(implicit ec: ExecutionContext) : Client = {
    new RemoteClient(host, port, config.getBoolean("docker.tls"))
  }

  def apply(host: String, port: Int, tlsOn: Boolean)(implicit ec: ExecutionContext) : Client = {
    apply(host, port, tlsOn, if (tlsOn) Some(TLSSupport(config.getString("docker.cert_path"))) else None)
  }

  def apply(host: String, port: Int, tls_on: Boolean, tls: Option[TLSSupport])(implicit ec: ExecutionContext) : Client = {
    new RemoteClient(host, port, tls_on, tls)
  }
}