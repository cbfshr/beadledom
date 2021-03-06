package com.cerner.beadledom.metadata

import java.net.InetAddress
import java.time.Instant
import org.scalatest.mock.MockitoSugar
import org.scalatest.{FunSpec, ShouldMatchers}

/**
 * Spec tests for {@link ServiceMetadata}.
 */
class ServiceMetadataSpec extends FunSpec with ShouldMatchers with MockitoSugar {
  describe("ServiceMetadata") {
    describe(".create") {
      it("builds a default object correctly") {
        val lowerTimeBound = Instant.now().minusMillis(1L)
        val mockBuildInfo = mock[BuildInfo]
        val metadata = ServiceMetadata.create(mockBuildInfo)
        val upperTimeBound = Instant.now().plusMillis(1L)
        metadata.getBuildInfo should be(mockBuildInfo)
        metadata.getHostName.get() should be(InetAddress.getLocalHost.getHostName)
        metadata.getStartupTime.isBefore(upperTimeBound) should be(true)
        metadata.getStartupTime.isAfter(lowerTimeBound) should be(true)
      }
    }
  }

  describe("ServiceMetadata.Builder") {
    it("builds an object correctly") {
      val mockBuildInfo = mock[BuildInfo]
      val metadata = ServiceMetadata.builder()
          .setBuildInfo(mockBuildInfo)
          .setHostName("flake001")
          .setStartupTime(Instant.parse("2001-02-03T04:05:06Z"))
          .build()
      metadata.getBuildInfo should be(mockBuildInfo)
      metadata.getHostName.get() should be("flake001")
      metadata.getStartupTime should be(Instant.parse("2001-02-03T04:05:06Z"))
    }
  }
}
