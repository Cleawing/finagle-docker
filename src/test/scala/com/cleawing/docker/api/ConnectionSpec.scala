package com.cleawing.docker.api

import com.cleawing.docker.DockerClient
import com.typesafe.config.ConfigFactory
import org.scalatest._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Millis, Seconds, Span}

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class ConnectionSpec extends FeatureSpec with GivenWhenThen with ShouldMatchers
  with ScalaFutures with Inside with BeforeAndAfterAll {

  import org.typelevel.scalatest.DisjunctionValues._
  import scala.concurrent.ExecutionContext.Implicits.global
  implicit val defaultPatience = PatienceConfig(timeout = Span(2, Seconds), interval = Span(100, Millis))

  val api = DockerClient()
  val config = ConfigFactory.load()

  override def afterAll() = Await.result(api.close(), Duration.Inf)

  feature("Connection") {
    scenario("Success") {
      Given("API connection from config")
      When("ping()")
      Then("Data.Pong(OK)")
      whenReady(api.ping())(res => inside (res.value) {
        case Data.Pong(msg) => msg shouldBe "OK"
      })
    }

    scenario("Connection failed") {
      Given("API connection with missed host and port")
      val missedApi = DockerClient("127.0.0.1", 22375)
      When("ping()")
      Then("Data.ConnectionFailed")
      whenReady(missedApi.ping()) { res =>
        res.leftValue shouldBe a [Data.ConnectionFailed]
        Await.result(missedApi.close(), Duration.Inf)
      }
    }

    scenario("Pickup TLS-port without tls = on") {
      Given("API connection with TLS-port")
      val missedApi = DockerClient(config.getInt("docker.tlsPort"))
      When("ping()")
      Then("Data.ConnectionFailed")
      whenReady(missedApi.ping()) { res =>
        res.leftValue shouldBe a [Data.ConnectionFailed]
        Await.result(missedApi.close(), Duration.Inf)
      }
    }

    scenario("Establish TLS-connection") {
      Given("API connection with tlsOn = true")
      val securedApi = DockerClient(tlsOn = true)
      When("ping()")
      Then("Data.Pong(OK)")
      whenReady(securedApi.ping())(res => inside (res.value) {
        case Data.Pong(msg) =>
          msg shouldBe "OK"
          Await.result(securedApi.close(), Duration.Inf)
      })
    }
  }
}
