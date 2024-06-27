package com.jason.publisher

import android.content.ContentResolver
import android.provider.Settings
import com.jason.publisher.model.BusItem
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkAll
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.whenever

/**
 * Unit tests for the MainActivity class.
 */
class MainActivityTest {

    /**
     * Sets up the test environment before each test.
     */
    @Before
    fun setUp() {
        mockkObject(OfflineData)
        mockkObject(OnlineData)
    }

    /**
     * Cleans up the test environment after each test.
     */
    @After
    fun tearDown() {
        unmockkAll()
    }

    /**
     * Tests the getAccessToken method when the configuration is found.
     */
    @Test
    fun testGetAccessToken_ConfigFound() {
        val mockConfigList = listOf(
            BusItem(aid = "8d34bdc9a5c78c42", bus = "Bus A", accessToken = "z0MQXzmMsNZwiD9Pwn6J"),
            BusItem(aid = "2b039058a1a5f8a3", bus = "Bus B", accessToken = "YiSbp8zzJyt3htZ7ECI0")
        )
        every { OnlineData.getConfig() } returns mockConfigList
        val result = Utils.getAccessToken(mockConfigList, "8d34bdc9a5c78c42")
        assertEquals("z0MQXzmMsNZwiD9Pwn6J", result)
    }

    /**
     * Tests the getAccessToken method when the configuration is not found.
     */
    @Test
    fun testGetAccessToken_ConfigNotFound() {
        val mockConfigList = listOf<BusItem>()
        every { OnlineData.getConfig() } returns mockConfigList
        val result = Utils.getAccessToken(mockConfigList, "8d34bdc9a5c78c42")
        assertNull(result)
    }

    /**
     * Tests the findBusNameByAid method when the aid is found.
     */
    @Test
    fun findBusNameByAid_aidFound() {
        val aid = "8d34bdc9a5c78c42"
        val bus = "Bus A"
        val mockConfigList = listOf(
            BusItem(aid = "8d34bdc9a5c78c42", bus = "Bus A", accessToken = "z0MQXzmMsNZwiD9Pwn6J"),
            BusItem(aid = "2b039058a1a5f8a3", bus = "Bus B", accessToken = "YiSbp8zzJyt3htZ7ECI0")
        )
        every { OfflineData.getConfig() } returns mockConfigList
        val result = Utils.findBusNameByAid(aid)
        assertEquals(bus, result)
    }

    /**
     * Tests the findBusNameByAid method when the aid is null.
     */
    @Test
    fun findBusNameByAid_isNull() {
        val aid: String? = null
        val result = Utils.findBusNameByAid(aid)
        assertNull(result)
    }

    /**
     * Tests the findBusNameByAid method when the aid is not found.
     */
    @Test
    fun findBusNameByAid_aidNotFound() {
        val aid = "Test"
        val mockConfigList = listOf<BusItem>()

        // Mock the class that contains the getConfig method
        val mockUtils = mock(OfflineData::class.java)
        `when`(mockUtils.getConfig()).thenReturn(mockConfigList)

        // Use the mocked instance to call the method
        val result = Utils.findBusNameByAid(aid)
        assertNull(result)
    }


    /**
     * Normalizes the given bearing value to ensure it falls within the range of 0 to 359 degrees.
     * If the input bearing is negative, it will be adjusted to a positive value within the range.
     *
     * @param bearing The input bearing value to normalize.
     * @return The normalized bearing value within the range of 0 to 359 degrees.
     */
    private fun normalizeBearing(bearing: Float): Float {
        var normalizedBearing = bearing % 360
        if (normalizedBearing < 0) {
            normalizedBearing += 360
        }
        return normalizedBearing
    }

    /**
     * Tests the bearingToDirection method with various bearing values.
     */
    @Test
    fun testBearingToDirection() {
        // North
        assertEquals("North", Helper.bearingToDirection(normalizeBearing(0f)))
        assertEquals("North", Helper.bearingToDirection(normalizeBearing(360f)))
        assertEquals("North", Helper.bearingToDirection(normalizeBearing(15f)))
        assertEquals("North", Helper.bearingToDirection(normalizeBearing(337.5f)))

        // Northeast
        assertEquals("Northeast", Helper.bearingToDirection(normalizeBearing(22.5f)))
        assertEquals("Northeast", Helper.bearingToDirection(normalizeBearing(45f)))
        assertEquals("Northeast", Helper.bearingToDirection(normalizeBearing(67.4f)))

        // East
        assertEquals("East", Helper.bearingToDirection(normalizeBearing(67.5f)))
        assertEquals("East", Helper.bearingToDirection(normalizeBearing(90f)))
        assertEquals("East", Helper.bearingToDirection(normalizeBearing(112.4f)))

        // Southeast
        assertEquals("Southeast", Helper.bearingToDirection(normalizeBearing(112.5f)))
        assertEquals("Southeast", Helper.bearingToDirection(normalizeBearing(135f)))
        assertEquals("Southeast", Helper.bearingToDirection(normalizeBearing(157.4f)))

        // South
        assertEquals("South", Helper.bearingToDirection(normalizeBearing(157.5f)))
        assertEquals("South", Helper.bearingToDirection(normalizeBearing(180f)))
        assertEquals("South", Helper.bearingToDirection(normalizeBearing(202.4f)))

        // Southwest
        assertEquals("Southwest", Helper.bearingToDirection(normalizeBearing(202.5f)))
        assertEquals("Southwest", Helper.bearingToDirection(normalizeBearing(225f)))
        assertEquals("Southwest", Helper.bearingToDirection(normalizeBearing(247.4f)))

        // West
        assertEquals("West", Helper.bearingToDirection(normalizeBearing(247.5f)))
        assertEquals("West", Helper.bearingToDirection(normalizeBearing(270f)))
        assertEquals("West", Helper.bearingToDirection(normalizeBearing(292.4f)))

        // Northwest
        assertEquals("Northwest", Helper.bearingToDirection(normalizeBearing(292.5f)))
        assertEquals("Northwest", Helper.bearingToDirection(normalizeBearing(315f)))
        assertEquals("Northwest", Helper.bearingToDirection(normalizeBearing(337.4f)))

        // Normalize Invalid Bearings
        assertEquals("North", Helper.bearingToDirection(normalizeBearing(-1f))) // -1 normalized to 359
        assertEquals("Northeast", Helper.bearingToDirection(normalizeBearing(400f))) // 400 normalized to 40
    }

}