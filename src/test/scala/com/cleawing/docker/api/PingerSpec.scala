package com.cleawing.docker.api

import com.cleawing.docker.Client
import org.scalatest._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Millis, Seconds, Span}

class PingerSpec extends FeatureSpec with GivenWhenThen with ShouldMatchers with ScalaFutures with EitherValues {
  import scala.concurrent.ExecutionContext.Implicits.global
  implicit val defaultPatience = PatienceConfig(timeout = Span(2, Seconds), interval = Span(100, Millis))

  val api = Client()

  feature("Pinger") {
    scenario("Success") {
      Given("API connection from config")
      When("ping()")
      whenReady(api.ping()) { res =>
        Then("Data.Pong(OK)")
        res.right.value shouldBe a [Data.Pong]
        res.right.value.msg shouldBe "OK"
      }
    }

    scenario("Connection failed") {
      Given("API connection with missed host and port")
      val missedApi = Client("127.0.0.1", 22375)
      When("ping()")
      Then("Data.ConnectionFailed")
      whenReady(missedApi.ping()) { _.left.value shouldBe a [Data.ConnectionFailed] }
    }

    scenario("Pickup TLS-port without tls = on") {
      Given("API connection with TLS-port")
      val missedApi = Client(2376)
      When("ping()")
      Then("Data.ConnectionFailed")
      whenReady(missedApi.ping()) { _.left.value shouldBe a [Data.ConnectionFailed] }
    }

    scenario("Establish TLS-connection") {
      Given("API connection with tls = on")
      val securedApi = Client(tlsOn = true)
      When("ping()")
      whenReady(securedApi.ping()) { res =>
        Then("Data.Pong(OK)")
        res.right.value shouldBe a [Data.Pong]
        res.right.value.msg shouldBe "OK"
      }
    }
  }
}
