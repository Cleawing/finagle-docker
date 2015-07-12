package com.cleawing.docker.api

import com.cleawing.finagle.http.{TLSSupport, Client => HttpClient}
import com.twitter.finagle.{ChannelClosedException, ChannelWriteException}
import scala.concurrent.{ExecutionContext, Future}
import com.cleawing.docker.DockerClient

import org.json4s.jackson.JsonMethods.parse
import com.cleawing.docker.api.Data.Implicits._

import scalaz.{\/, \/-, -\/}

private[docker] class DockerClientImpl(
  val host: String,
  val port: Int,
  val tlsOn: Boolean,
  val tlsSupport: Option[TLSSupport] = None)(implicit val ec: ExecutionContext) extends HttpClient with DockerClient {

  def ping() : Future[Data.Error \/ Data.Pong] = {
    simpleGet("/_ping").map {
      case \/-(success: HttpClient.Success) => \/-(Data.Pong(success.body))
      case \/-(error: HttpClient.Error) => -\/(Data.UnexpectedError(error.body))
      case -\/(failure : HttpClient.Failure) => -\/(processFailure(failure))
    }
  }

  def version() : Future[Data.Error \/ Data.Version] = {
    simpleGet("/version").map(processResponse[Data.Version])
  }

  def info() : Future[Data.Error \/ Data.Info] = {
    simpleGet("/info").map(processResponse[Data.Info])
  }

  def processResponse[T: Manifest](either: HttpClient.Failure \/ HttpClient.Response) : Data.Error \/ T = {
    either match {
      case \/-(success: HttpClient.Success) => \/-(parse(success.body).extract[T])
      case \/-(error: HttpClient.Error) => -\/(Data.UnexpectedError(error.body))
      case -\/(ex : HttpClient.Failure) => -\/(processFailure(ex))
    }
  }

  private def processFailure(ex: HttpClient.Failure) : Data.Failure = {
    ex.cause match {
      case _: ChannelWriteException | _: ChannelClosedException => Data.ConnectionFailed(ex.cause)
      case t: Throwable => Data.UnexpectedFailure(ex.cause)
    }
  }
}
