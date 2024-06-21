package com.jason.publisher

import com.jason.publisher.model.BusItem

object Utils {
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