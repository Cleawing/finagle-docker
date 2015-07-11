package com.cleawing.docker

import com.cleawing.docker.api.Data
import org.scalatest._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Millis, Seconds, Span}

class ClientSpec extends FunSpec with ShouldMatchers with ScalaFutures with TryValues with EitherValues {
  import org.typelevel.scalatest.DisjunctionValues._

  import scala.concurrent.ExecutionContext.Implicits.global
  implicit val defaultPatience = PatienceConfig(timeout = Span(2, Seconds), interval = Span(100, Millis))

  val api = Client()

  describe("Misc") {
    it("version() should return Data.Version") {
      whenReady(api.version()) { _.success.value.value shouldBe a [Data.Version]}
    }

    it("info() should return Data.Info") {
      whenReady(api.info()) {_.success.value.value shouldBe a [Data.Info]}
    }
  }

//  describe("Images") {
//    it("should return Data.Images") {
//      whenReady(api.images()) {_.right.value shouldBe a [Data.Images]}
//    }
//
//    ignore("should return Data.ImageHistory") {
//      whenReady(api.images()) {_.right.value shouldBe a [Data.ImageHistory]}
//    }
//  }
//
//  describe("Containers") {
//    it("should return Data.Containers") {
//      whenReady(api.containers()) {_.right.value shouldBe a [Data.Containers]}
//    }
//  }

}
