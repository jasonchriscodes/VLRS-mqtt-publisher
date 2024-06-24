package com.jason.publisher

import com.jason.publisher.model.BusItem
import org.json.JSONObject
import org.osmdroid.util.GeoPoint

object OnlineData {

    fun getConfig() : List<BusItem> {
        val jsonString = """
            {"busConfig":[{"aid":"8d34bdc9a5c78c42","bus":"Bus A","accessToken":"z0MQXzmMsNZwiD9Pwn6J"},{"aid":"2b039058a1a5f8a3","bus":"Bus B","accessToken":"YiSbp8zzJyt3htZ7ECI0"},{"aid":"02372ba208415152","bus":"Bus C","accessToken":"kTmTKRd11CPX7RhXTVZY"}]}
        """.trimIndent()

        val configurationBus = mutableListOf<BusItem>()

        val jsonObject = JSONObject(jsonString)
        val jsonArray = jsonObject.getJSONArray("busConfig")

        for (i in 0 until jsonArray.length()) {
            val item = jsonArray.getJSONObject(i)
            val aid = item.getString("aid")
            val bus = item.getString("bus")
            val accessToken = item.getString("accessToken")
            configurationBus.add(BusItem(aid,bus,accessToken))
        }
        return configurationBus
    }
}