package com.cleawing.docker.api

import com.cleawing.docker.Client
import com.typesafe.config.ConfigFactory
import org.scalatest._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Millis, Seconds, Span}

class PingerSpec extends FeatureSpec
  with GivenWhenThen with ShouldMatchers
  with ScalaFutures with TryValues with Inside {

  import org.typelevel.scalatest.DisjunctionValues._
  import scala.concurrent.ExecutionContext.Implicits.global
  implicit val defaultPatience = PatienceConfig(timeout = Span(2, Seconds), interval = Span(100, Millis))

  val api = Client()
  val config = ConfigFactory.load()

  feature("Pinger") {
    scenario("Success") {
      Given("API connection from config")
      When("ping()")
      Then("Data.Pong(OK)")
      whenReady(api.ping())(res => inside (res.success.value.value) {
        case Data.Pong(msg) => msg shouldBe "OK"
      })
    }

    scenario("Connection failed") {
      Given("API connection with missed host and port")
      val missedApi = Client("127.0.0.1", 22375)
      When("ping()")
      Then("Data.ConnectionFailed")
      whenReady(missedApi.ping()) { _.success.value.leftValue shouldBe a [Data.ConnectionFailed] }
    }

    scenario("Pickup TLS-port without tls = on") {
      Given("API connection with TLS-port")
      val missedApi = Client(config.getInt("docker.tlsPort"))
      When("ping()")
      Then("Data.ConnectionFailed")
      whenReady(missedApi.ping()) { _.success.value.leftValue shouldBe a [Data.ConnectionFailed] }
    }

    scenario("Establish TLS-connection") {
      Given("API connection with tls = on")
      val securedApi = Client(tlsOn = true)
      When("ping()")
      Then("Data.Pong(OK)")
      whenReady(securedApi.ping())(res => inside (res.success.value.value) {
        case Data.Pong(msg) => msg shouldBe "OK"
      })
    }
  }
}
