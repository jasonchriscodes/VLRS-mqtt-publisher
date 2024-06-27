package com.jason.publisher

import com.jason.publisher.model.BusItem

/**
 * Utility object containing methods for bus-related operations.
 */
object Utils {

    /**
     * Finds the bus name by its aid.
     *
     * @param aid The aid of the bus.
     * @return The name of the bus, or null if not found.
     */
    fun findBusNameByAid(aid: String?): String? {
        if (aid == null) {
            return null
        }
        val configList = OfflineData.getConfig()
        val busItem = configList.find { it.aid == aid }
        return busItem?.bus ?: run {
            null
        }
    }

    /**
     * Retrieves the access token for a bus given its aid.
     *
     * @param listBustItem The list of BusItem objects.
     * @param aid The aid of the bus.
     * @return The access token for the bus, or null if not found.
     */
    fun getAccessToken(listBustItem: List<BusItem>, aid: String?): String? {
        var token: String? = null
        if (aid == null) {
            return null
        }
        for (config in listBustItem) {
            if (config.aid == aid) {
                token = config.accessToken
                break
            }
        }
        return token
    }
}