package com.digilayn.laynfleet.core.data

import org.junit.Assert.assertEquals
import org.junit.Test

class FirestorePathsTest {
    @Test fun riderConfigUsesItsOwnAppId() {
        assertEquals(
            "kasilayn/LaynFleet/appConfig/com.digilayn.laynrider",
            FirestorePaths.appConfig("com.digilayn.laynrider"),
        )
    }

    @Test fun tripsStayInsideOperatorBoundary() {
        assertEquals(
            "kasilayn/LaynFleet/operators/op-1/trips",
            FirestorePaths.trips("op-1"),
        )
    }
}
