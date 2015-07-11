package com.cleawing.docker.api


import com.cleawing.finagle.http.{TLSSupport, Client => HttpClient}
import com.twitter.finagle.{ChannelClosedException, ChannelWriteException}
import scala.concurrent.{ExecutionContext, Future}
import com.cleawing.docker.{Client => DockerClient}

import org.json4s.jackson.JsonMethods.parse
import com.cleawing.docker.api.Data.Implicits._

import scala.util.Try
import scalaz.{\/, \/-, -\/}

private[docker] class RemoteClient(val host: String,
                   val port: Int,
                   val tlsOn: Boolean,
                   val tlsSupport: Option[TLSSupport] = None)(implicit val ec: ExecutionContext) extends HttpClient with DockerClient {

  def ping() : Future[Try[Data.Error \/ Data.Pong]] = {
    simpleGet("/_ping").map {
      case Right(success: HttpClient.Success) => Try(\/-(Data.Pong(success.body)))
      case Right(error: HttpClient.Error) => Try(-\/(Data.UnexpectedError(error.body)))
      case Left(failure : HttpClient.Failure) => Try(-\/(processFailure(failure)))
    }
  }

  def version() : Future[Try[Data.Error \/ Data.Version]] = {
    simpleGet("/version").map(r => Try(processResponse[Data.Version](r)))
  }

  def info() : Future[Try[Data.Error \/ Data.Info]] = {
    simpleGet("/info").map(r => Try(processResponse[Data.Info](r)))
  }

  def processResponse[T: Manifest](either: Either[HttpClient.Failure, HttpClient.Response]) : Data.Error \/ T = {
    either match {
      case Right(success: HttpClient.Success) => \/-(parse(success.body).extract[T])
      case Right(error: HttpClient.Error) => -\/(Data.UnexpectedError(error.body))
      case Left(ex : HttpClient.Failure) => -\/(processFailure(ex))
    }
  }

  private def processFailure(ex: HttpClient.Failure) : Data.Failure = {
    ex.cause match {
      case _: ChannelWriteException | _: ChannelClosedException => Data.ConnectionFailed(ex.cause)
      case t: Throwable => throw t
    }
  }
}
