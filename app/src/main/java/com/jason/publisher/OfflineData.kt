package com.jason.publisher

import com.jason.publisher.model.BusBearing
import com.jason.publisher.model.BusBearingCustomer
import com.jason.publisher.model.BusItem
import com.jason.publisher.model.BusStop
import com.jason.publisher.model.BusStopInfo
import org.json.JSONArray
import org.json.JSONObject
import org.osmdroid.util.GeoPoint

/**
 * Object that provides offline data for bus routes, stops, configurations, and bearings.
 */
object OfflineData {

    /**
     * Retrieves a list of GeoPoint objects representing the bus routes offline.
     * The routes are hardcoded as JSON strings.
     *
     * @return List of GeoPoint representing the bus routes.
     */
    fun getRoutesOffline() : List<GeoPoint> {
        val geoPoint = mutableListOf<GeoPoint>()
        val jsonString = """
           {"1":[{"latitude":-36.780120000000004,"longitude":174.99216},{"latitude":-36.77995,"longitude":174.99204},{"latitude":-36.77994,"longitude":174.99218000000002},{"latitude":-36.7806,"longitude":174.99309000000002},{"latitude":-36.78078,"longitude":174.99341},{"latitude":-36.78085,"longitude":174.99462000000003},{"latitude":-36.78096,"longitude":174.99515000000002},{"latitude":-36.78132,"longitude":174.99560000000002},{"latitude":-36.78154,"longitude":174.99597000000003},{"latitude":-36.781620000000004,"longitude":174.9966},{"latitude":-36.781420000000004,"longitude":174.99847000000003},{"latitude":-36.78161,"longitude":175.00001},{"latitude":-36.782120000000006,"longitude":175.00159000000002},{"latitude":-36.7824,"longitude":175.00398},{"latitude":-36.782500000000006,"longitude":175.00510000000003},{"latitude":-36.78226,"longitude":175.00563000000002},{"latitude":-36.781780000000005,"longitude":175.00613},{"latitude":-36.78146,"longitude":175.00645},{"latitude":-36.78141,"longitude":175.00683},{"latitude":-36.781650000000006,"longitude":175.00761000000003},{"latitude":-36.781620000000004,"longitude":175.00861},{"latitude":-36.78181,"longitude":175.0089},{"latitude":-36.78181,"longitude":175.00894000000002},{"latitude":-36.78184,"longitude":175.00898},{"latitude":-36.7819,"longitude":175.00899},{"latitude":-36.78226,"longitude":175.00933},{"latitude":-36.783210000000004,"longitude":175.0105},{"latitude":-36.784090000000006,"longitude":175.01248},{"latitude":-36.78477,"longitude":175.01371},{"latitude":-36.784740000000006,"longitude":175.01499},{"latitude":-36.78473,"longitude":175.01614},{"latitude":-36.784890000000004,"longitude":175.01694},{"latitude":-36.785250000000005,"longitude":175.01775},{"latitude":-36.785340000000005,"longitude":175.01833000000002},{"latitude":-36.785410000000006,"longitude":175.01883},{"latitude":-36.78587,"longitude":175.0197},{"latitude":-36.78678,"longitude":175.02146000000002},{"latitude":-36.78719,"longitude":175.02182000000002},{"latitude":-36.787760000000006,"longitude":175.02273000000002},{"latitude":-36.78804,"longitude":175.02312},{"latitude":-36.78851,"longitude":175.02303},{"latitude":-36.789060000000006,"longitude":175.0233},{"latitude":-36.789280000000005,"longitude":175.02348},{"latitude":-36.78967,"longitude":175.02364},{"latitude":-36.790400000000005,"longitude":175.02359},{"latitude":-36.79168,"longitude":175.02315000000002},{"latitude":-36.79234,"longitude":175.02298000000002},{"latitude":-36.79307,"longitude":175.02445},{"latitude":-36.792950000000005,"longitude":175.0251},{"latitude":-36.792860000000005,"longitude":175.02558000000002},{"latitude":-36.79361,"longitude":175.02614000000003},{"latitude":-36.793980000000005,"longitude":175.02629000000002},{"latitude":-36.79473,"longitude":175.02637000000001},{"latitude":-36.7952,"longitude":175.02664000000001},{"latitude":-36.79529,"longitude":175.02727000000002},{"latitude":-36.79493,"longitude":175.02888000000002},{"latitude":-36.79496,"longitude":175.02946},{"latitude":-36.795570000000005,"longitude":175.03118},{"latitude":-36.79623,"longitude":175.03201},{"latitude":-36.796380000000006,"longitude":175.03221000000002},{"latitude":-36.79643,"longitude":175.03224},{"latitude":-36.79677,"longitude":175.03245},{"latitude":-36.797940000000004,"longitude":175.0329},{"latitude":-36.79845,"longitude":175.03338000000002},{"latitude":-36.79876,"longitude":175.03408000000002},{"latitude":-36.79901,"longitude":175.03525000000002},{"latitude":-36.79887,"longitude":175.03585},{"latitude":-36.79833,"longitude":175.03727},{"latitude":-36.797610000000006,"longitude":175.03935},{"latitude":-36.797880000000006,"longitude":175.04172000000003},{"latitude":-36.79621,"longitude":175.0431},{"latitude":-36.79554,"longitude":175.04376000000002},{"latitude":-36.79552,"longitude":175.04405000000003},{"latitude":-36.796020000000006,"longitude":175.04777},{"latitude":-36.79581,"longitude":175.04904000000002},{"latitude":-36.79572,"longitude":175.05026},{"latitude":-36.795500000000004,"longitude":175.05131},{"latitude":-36.79553000000001,"longitude":175.05172000000002},{"latitude":-36.79567,"longitude":175.05211000000003},{"latitude":-36.796440000000004,"longitude":175.05366},{"latitude":-36.79704,"longitude":175.05516},{"latitude":-36.79746,"longitude":175.05569000000003},{"latitude":-36.797900000000006,"longitude":175.05640000000002},{"latitude":-36.798390000000005,"longitude":175.05677},{"latitude":-36.79871,"longitude":175.05684000000002},{"latitude":-36.79952,"longitude":175.05691000000002},{"latitude":-36.800940000000004,"longitude":175.05803},{"latitude":-36.80144,"longitude":175.05841},{"latitude":-36.80216,"longitude":175.05936000000003},{"latitude":-36.80261,"longitude":175.06026000000003},{"latitude":-36.802640000000004,"longitude":175.06078000000002},{"latitude":-36.80238000000001,"longitude":175.06136},{"latitude":-36.801570000000005,"longitude":175.06209},{"latitude":-36.80124,"longitude":175.06264000000002},{"latitude":-36.80143,"longitude":175.06561000000002},{"latitude":-36.801550000000006,"longitude":175.06758000000002},{"latitude":-36.80113,"longitude":175.06958},{"latitude":-36.80046,"longitude":175.07219},{"latitude":-36.799820000000004,"longitude":175.07363},{"latitude":-36.797380000000004,"longitude":175.07801},{"latitude":-36.7971,"longitude":175.07816000000003},{"latitude":-36.79655,"longitude":175.07809},{"latitude":-36.79621,"longitude":175.07788000000002},{"latitude":-36.79549,"longitude":175.07728},{"latitude":-36.79437,"longitude":175.07744000000002},{"latitude":-36.793670000000006,"longitude":175.07768000000002},{"latitude":-36.7924,"longitude":175.07961},{"latitude":-36.79065000000001,"longitude":175.08126000000001},{"latitude":-36.790440000000004,"longitude":175.08136000000002},{"latitude":-36.79023,"longitude":175.08139000000003},{"latitude":-36.790290000000006,"longitude":175.08159},{"latitude":-36.790560000000006,"longitude":175.08192000000003},{"latitude":-36.79092,"longitude":175.08254000000002},{"latitude":-36.79092,"longitude":175.08292},{"latitude":-36.790420000000005,"longitude":175.08294},{"latitude":-36.788970000000006,"longitude":175.08308000000002},{"latitude":-36.7884,"longitude":175.08313},{"latitude":-36.78804,"longitude":175.08317000000002},{"latitude":-36.788030000000006,"longitude":175.08296},{"latitude":-36.787980000000005,"longitude":175.08197},{"latitude":-36.78795,"longitude":175.08121000000003}],"2":[{"latitude":-36.787954,"longitude":175.08121},{"latitude":-36.787957,"longitude":175.081251},{"latitude":-36.788201,"longitude":175.081243},{"latitude":-36.7882,"longitude":175.08118000000002},{"latitude":-36.788470000000004,"longitude":175.08114},{"latitude":-36.788740000000004,"longitude":175.08110000000002},{"latitude":-36.78913,"longitude":175.08118000000002},{"latitude":-36.78981,"longitude":175.08139000000003},{"latitude":-36.79048,"longitude":175.08135000000001},{"latitude":-36.79155,"longitude":175.08057000000002},{"latitude":-36.792680000000004,"longitude":175.07917},{"latitude":-36.79374,"longitude":175.07761000000002},{"latitude":-36.79531,"longitude":175.07727000000003},{"latitude":-36.79558,"longitude":175.0773},{"latitude":-36.79628,"longitude":175.07795000000002},{"latitude":-36.796600000000005,"longitude":175.07811},{"latitude":-36.797140000000006,"longitude":175.07815000000002},{"latitude":-36.7974,"longitude":175.07799000000003},{"latitude":-36.79878,"longitude":175.07553000000001},{"latitude":-36.800070000000005,"longitude":175.0732},{"latitude":-36.800740000000005,"longitude":175.07107000000002},{"latitude":-36.80124,"longitude":175.06917},{"latitude":-36.80154,"longitude":175.06785000000002},{"latitude":-36.801460000000006,"longitude":175.06603},{"latitude":-36.80142,"longitude":175.06544000000002},{"latitude":-36.80124,"longitude":175.06306},{"latitude":-36.801350000000006,"longitude":175.06232000000003},{"latitude":-36.80207,"longitude":175.06168000000002},{"latitude":-36.80256,"longitude":175.06111},{"latitude":-36.80265,"longitude":175.06051000000002},{"latitude":-36.80263,"longitude":175.06036},{"latitude":-36.803140000000006,"longitude":175.06057},{"latitude":-36.80449,"longitude":175.06119},{"latitude":-36.80507,"longitude":175.06117},{"latitude":-36.80552,"longitude":175.06076000000002},{"latitude":-36.80599,"longitude":175.06052000000003},{"latitude":-36.80684,"longitude":175.06132000000002},{"latitude":-36.80736,"longitude":175.06183000000001},{"latitude":-36.80792,"longitude":175.062},{"latitude":-36.808620000000005,"longitude":175.06211000000002},{"latitude":-36.808960000000006,"longitude":175.06199},{"latitude":-36.809070000000006,"longitude":175.0618},{"latitude":-36.80946,"longitude":175.06217},{"latitude":-36.80979,"longitude":175.06314},{"latitude":-36.810480000000005,"longitude":175.06426000000002},{"latitude":-36.810660000000006,"longitude":175.06491000000003},{"latitude":-36.810810000000004,"longitude":175.06638},{"latitude":-36.8111,"longitude":175.06793000000002},{"latitude":-36.811580000000006,"longitude":175.06910000000002},{"latitude":-36.811930000000004,"longitude":175.07010000000002},{"latitude":-36.81253,"longitude":175.07070000000002},{"latitude":-36.813250000000004,"longitude":175.07199000000003},{"latitude":-36.81481,"longitude":175.07362},{"latitude":-36.81561,"longitude":175.07510000000002},{"latitude":-36.815850000000005,"longitude":175.07616000000002},{"latitude":-36.81589,"longitude":175.07676},{"latitude":-36.81613,"longitude":175.07814000000002},{"latitude":-36.81618,"longitude":175.07858000000002},{"latitude":-36.815920000000006,"longitude":175.08016},{"latitude":-36.81582,"longitude":175.08089},{"latitude":-36.815490000000004,"longitude":175.08162000000002},{"latitude":-36.81486,"longitude":175.08235000000002},{"latitude":-36.814550000000004,"longitude":175.08243000000002},{"latitude":-36.81486,"longitude":175.08235000000002},{"latitude":-36.815490000000004,"longitude":175.08162000000002},{"latitude":-36.81582,"longitude":175.08089},{"latitude":-36.815920000000006,"longitude":175.08016},{"latitude":-36.81618,"longitude":175.07858000000002},{"latitude":-36.81613,"longitude":175.07814000000002},{"latitude":-36.81589,"longitude":175.07676},{"latitude":-36.815850000000005,"longitude":175.07616000000002},{"latitude":-36.81561,"longitude":175.07510000000002},{"latitude":-36.81481,"longitude":175.07362},{"latitude":-36.813250000000004,"longitude":175.07199000000003},{"latitude":-36.81253,"longitude":175.07070000000002},{"latitude":-36.811930000000004,"longitude":175.07010000000002},{"latitude":-36.811580000000006,"longitude":175.06910000000002},{"latitude":-36.8111,"longitude":175.06793000000002},{"latitude":-36.810810000000004,"longitude":175.06638},{"latitude":-36.810660000000006,"longitude":175.06491000000003},{"latitude":-36.810480000000005,"longitude":175.06426000000002},{"latitude":-36.80979,"longitude":175.06314},{"latitude":-36.80946,"longitude":175.06217},{"latitude":-36.809070000000006,"longitude":175.0618},{"latitude":-36.808960000000006,"longitude":175.06199},{"latitude":-36.808620000000005,"longitude":175.06211000000002},{"latitude":-36.80792,"longitude":175.062},{"latitude":-36.80736,"longitude":175.06183000000001},{"latitude":-36.80684,"longitude":175.06132000000002},{"latitude":-36.80599,"longitude":175.06052000000003},{"latitude":-36.80552,"longitude":175.06076000000002},{"latitude":-36.80507,"longitude":175.06117},{"latitude":-36.80449,"longitude":175.06119},{"latitude":-36.803140000000006,"longitude":175.06057},{"latitude":-36.80261,"longitude":175.06026000000003},{"latitude":-36.80216,"longitude":175.05936000000003},{"latitude":-36.80144,"longitude":175.05841},{"latitude":-36.800940000000004,"longitude":175.05803},{"latitude":-36.79952,"longitude":175.05691000000002},{"latitude":-36.79871,"longitude":175.05684000000002},{"latitude":-36.798390000000005,"longitude":175.05677},{"latitude":-36.797900000000006,"longitude":175.05640000000002},{"latitude":-36.79746,"longitude":175.05569000000003},{"latitude":-36.79711,"longitude":175.05527},{"latitude":-36.796940000000006,"longitude":175.05491},{"latitude":-36.796440000000004,"longitude":175.05366},{"latitude":-36.79567,"longitude":175.05211000000003},{"latitude":-36.795500000000004,"longitude":175.05146000000002},{"latitude":-36.79572,"longitude":175.05026},{"latitude":-36.79581,"longitude":175.04904000000002},{"latitude":-36.796020000000006,"longitude":175.04777},{"latitude":-36.79552,"longitude":175.04405000000003},{"latitude":-36.79554,"longitude":175.04376000000002},{"latitude":-36.79621,"longitude":175.0431},{"latitude":-36.797880000000006,"longitude":175.04172000000003},{"latitude":-36.797610000000006,"longitude":175.03935},{"latitude":-36.79833,"longitude":175.03727},{"latitude":-36.79887,"longitude":175.03585},{"latitude":-36.79901,"longitude":175.03525000000002},{"latitude":-36.79876,"longitude":175.03408000000002},{"latitude":-36.79845,"longitude":175.03338000000002},{"latitude":-36.797940000000004,"longitude":175.0329},{"latitude":-36.79677,"longitude":175.03245},{"latitude":-36.7965,"longitude":175.03206},{"latitude":-36.79639,"longitude":175.03202000000002},{"latitude":-36.796220000000005,"longitude":175.03198},{"latitude":-36.79563,"longitude":175.03128},{"latitude":-36.7954,"longitude":175.03044000000003},{"latitude":-36.79491,"longitude":175.02913},{"latitude":-36.79524,"longitude":175.02747000000002},{"latitude":-36.795280000000005,"longitude":175.0268},{"latitude":-36.794850000000004,"longitude":175.02643},{"latitude":-36.794320000000006,"longitude":175.02627},{"latitude":-36.79381,"longitude":175.02625},{"latitude":-36.79309000000001,"longitude":175.02575000000002},{"latitude":-36.792860000000005,"longitude":175.02558000000002},{"latitude":-36.7929,"longitude":175.02535},{"latitude":-36.793000000000006,"longitude":175.02488000000002},{"latitude":-36.79307,"longitude":175.02452000000002},{"latitude":-36.79294,"longitude":175.02411},{"latitude":-36.79224,"longitude":175.02294},{"latitude":-36.790510000000005,"longitude":175.02358},{"latitude":-36.78983,"longitude":175.02366},{"latitude":-36.789370000000005,"longitude":175.02355},{"latitude":-36.78867,"longitude":175.02300000000002},{"latitude":-36.788050000000005,"longitude":175.02312},{"latitude":-36.787310000000005,"longitude":175.02201000000002},{"latitude":-36.7866,"longitude":175.02123},{"latitude":-36.78548,"longitude":175.01899},{"latitude":-36.78529,"longitude":175.01787000000002},{"latitude":-36.78477,"longitude":175.01642},{"latitude":-36.78479,"longitude":175.01419},{"latitude":-36.784710000000004,"longitude":175.01353},{"latitude":-36.78372,"longitude":175.01175},{"latitude":-36.78332,"longitude":175.01069},{"latitude":-36.782900000000005,"longitude":175.01003},{"latitude":-36.78193,"longitude":175.00897},{"latitude":-36.781940000000006,"longitude":175.00895000000003},{"latitude":-36.78195,"longitude":175.00891000000001},{"latitude":-36.78192,"longitude":175.00884000000002},{"latitude":-36.781850000000006,"longitude":175.00883000000002},{"latitude":-36.781670000000005,"longitude":175.00858000000002},{"latitude":-36.781650000000006,"longitude":175.00773},{"latitude":-36.78143,"longitude":175.00696000000002},{"latitude":-36.78143,"longitude":175.00655},{"latitude":-36.78166,"longitude":175.00622},{"latitude":-36.78291,"longitude":175.00537000000003},{"latitude":-36.783570000000005,"longitude":175.00475},{"latitude":-36.783820000000006,"longitude":175.00449},{"latitude":-36.78445,"longitude":175.00344},{"latitude":-36.78524,"longitude":175.0029},{"latitude":-36.78605,"longitude":175.00249000000002},{"latitude":-36.78656,"longitude":175.00263},{"latitude":-36.78679,"longitude":175.00253},{"latitude":-36.78737,"longitude":175.00137},{"latitude":-36.78759,"longitude":175.00129},{"latitude":-36.788270000000004,"longitude":175.00184000000002},{"latitude":-36.788880000000006,"longitude":175.00242},{"latitude":-36.78947,"longitude":175.00281},{"latitude":-36.790000000000006,"longitude":175.00289},{"latitude":-36.79026,"longitude":175.00269},{"latitude":-36.790310000000005,"longitude":175.00245},{"latitude":-36.790060000000004,"longitude":175.00159000000002},{"latitude":-36.790440000000004,"longitude":174.99984},{"latitude":-36.79092,"longitude":174.99886},{"latitude":-36.79102,"longitude":174.99848},{"latitude":-36.791140000000006,"longitude":174.99858},{"latitude":-36.7918,"longitude":174.99929},{"latitude":-36.79193,"longitude":174.99949},{"latitude":-36.79198,"longitude":174.99972000000002},{"latitude":-36.79247,"longitude":174.99988000000002},{"latitude":-36.7927,"longitude":175.00011},{"latitude":-36.79258,"longitude":174.99990000000003},{"latitude":-36.792210000000004,"longitude":174.99986},{"latitude":-36.79195,"longitude":174.99964000000003},{"latitude":-36.79187,"longitude":174.99938},{"latitude":-36.79102,"longitude":174.99848},{"latitude":-36.790560000000006,"longitude":174.99949},{"latitude":-36.7903,"longitude":175.0004},{"latitude":-36.790060000000004,"longitude":175.00145},{"latitude":-36.790110000000006,"longitude":175.00178000000002},{"latitude":-36.7903,"longitude":175.00256000000002},{"latitude":-36.79019,"longitude":175.00279},{"latitude":-36.7899,"longitude":175.00289},{"latitude":-36.789260000000006,"longitude":175.00272},{"latitude":-36.787980000000005,"longitude":175.00154},{"latitude":-36.787620000000004,"longitude":175.00130000000001},{"latitude":-36.78741,"longitude":175.00133000000002},{"latitude":-36.786660000000005,"longitude":175.00261},{"latitude":-36.78647,"longitude":175.00262},{"latitude":-36.78582,"longitude":175.00252},{"latitude":-36.7849,"longitude":175.00322000000003},{"latitude":-36.784310000000005,"longitude":175.00361},{"latitude":-36.78403,"longitude":175.00427000000002},{"latitude":-36.78372,"longitude":175.00462000000002},{"latitude":-36.78349,"longitude":175.00481000000002},{"latitude":-36.78248,"longitude":175.00563000000002},{"latitude":-36.78237,"longitude":175.00544000000002},{"latitude":-36.78253,"longitude":175.00486},{"latitude":-36.782300000000006,"longitude":175.00317},{"latitude":-36.78219,"longitude":175.00194000000002},{"latitude":-36.781890000000004,"longitude":175.00101},{"latitude":-36.781510000000004,"longitude":174.99954000000002},{"latitude":-36.78141,"longitude":174.99885},{"latitude":-36.781490000000005,"longitude":174.99807},{"latitude":-36.781600000000005,"longitude":174.99623000000003},{"latitude":-36.781400000000005,"longitude":174.99570000000003},{"latitude":-36.78119,"longitude":174.99545},{"latitude":-36.78088,"longitude":174.99496000000002},{"latitude":-36.780840000000005,"longitude":174.99382000000003},{"latitude":-36.7807,"longitude":174.99325000000002},{"latitude":-36.780390000000004,"longitude":174.99272000000002},{"latitude":-36.780120000000004,"longitude":174.99216}]}
        """.trimIndent()
        val jsonObject = JSONObject(jsonString)
        val jsonArray = jsonObject.getJSONArray("1")
        val jsonArray2 = jsonObject.getJSONArray( "2")

        for (i in 0 until jsonArray.length()) {
            val item = jsonArray.getJSONObject(i)
            val latitude = item.getDouble("latitude")
            val longitude = item.getDouble("longitude")
            geoPoint.add(GeoPoint(latitude, longitude))
        }
        for (i in 0 until jsonArray2.length()) {
            val item = jsonArray2.getJSONObject(i)
            val latitude = item.getDouble("latitude")
            val longitude = item.getDouble("longitude")
            geoPoint.add(GeoPoint(latitude, longitude))
        }
        return geoPoint
    }

    /**
     * Retrieves a list of GeoPoint objects representing bus stops offline.
     * The stops are hardcoded as a JSON string.
     *
     * @return List of GeoPoint representing the bus stops.
     */
    fun getBusStopOffline() : List<GeoPoint> {
        val jsonString = """
           {"1":[{"latitude":-36.78012,"longitude":174.99216},{"latitude":-36.78139,"longitude":175.007},{"latitude":-36.78338,"longitude":175.01086},{"latitude":-36.79883,"longitude":175.03447},{"latitude":-36.79589,"longitude":175.04737},{"latitude":-36.80141,"longitude":175.06579},{"latitude":-36.80106,"longitude":175.06972},{"latitude":-36.79887,"longitude":175.07527},{"latitude":-36.78842,"longitude":175.08309},{"latitude":-36.8011,"longitude":175.06984},{"latitude":-36.80149,"longitude":175.06619},{"latitude":-36.81456,"longitude":175.08249},{"latitude":-36.80916,"longitude":175.06174},{"latitude":-36.79601,"longitude":175.04829},{"latitude":-36.79689,"longitude":175.03242},{"latitude":-36.78365,"longitude":175.01139},{"latitude":-36.79159,"longitude":174.99938},{"latitude":-36.78724,"longitude":175.00125},{"latitude":-36.78012,"longitude":174.99216}]}
        """.trimIndent()
        val geoPoint = mutableListOf<GeoPoint>()

        val jsonObject = JSONObject(jsonString)
        val jsonArray = jsonObject.getJSONArray("1")

        for (i in 0 until jsonArray.length()) {
            val item = jsonArray.getJSONObject(i)
            val latitude = item.getDouble("latitude")
            val longitude = item.getDouble("longitude")
            geoPoint.add(GeoPoint(latitude, longitude))
        }
        return geoPoint
    }

    /**
     * Retrieves a list of BusItem objects representing the bus configuration.
     * The configuration is hardcoded as a JSON string.
     *
     * @return List of BusItem representing the bus configuration.
     */
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

    /**
     * Retrieves a list of BusBearing objects representing the bearings of buses.
     * The bearings are hardcoded as a JSON string.
     *
     * @return List of BusBearing representing the bus bearings.
     */
    fun getBearing() : List<BusBearing> {
        val jsonString = """
            [{"bearing":150.51747258053868},{"bearing":264.90383942298206},{"bearing":312.1621240664246},{"bearing":250.0806842995843},{"bearing":250.1316566947198},{"bearing":250.5278032956903},{"bearing":250.96690427907936},{"bearing":250.5897280561044},{"bearing":265.0092775021285},{"bearing":265.3945348821494},{"bearing":265.7576264769906},{"bearing":265.95074937509304},{"bearing":265.32268707210585},{"bearing":265.3613851915603},{"bearing":265.51674536589826},{"bearing":315.83808626883604},{"bearing":315.6921530383453},{"bearing":315.670689621612},{"bearing":315.0155839062817},{"bearing":270.8551874584929},{"bearing":270.2840163031571},{"bearing":270.00001197368016},{"bearing":270.1194970997825},{"bearing":247.39671966480273},{"bearing":247.8953951084304},{"bearing":247.393024881971},{"bearing":247.0277871720307},{"bearing":247.61712067435025},{"bearing":247.3241412276219},{"bearing":260.37828134744143},{"bearing":260.0214980900823},{"bearing":260.0278873389893},{"bearing":260.96543437823067},{"bearing":260.9156533706438},{"bearing":260.4327381808484},{"bearing":247.84686404587893},{"bearing":247.8851957542378},{"bearing":247.0300884098776},{"bearing":247.8755360762467},{"bearing":247.71864343361005},{"bearing":247.53811453783237},{"bearing":202.7657747229931},{"bearing":202.8120940016931},{"bearing":202.1396125442667},{"bearing":202.3912253906884},{"bearing":202.65491653295223},{"bearing":202.804412700577},{"bearing":260.01818913879015},{"bearing":260.8223384486351},{"bearing":190.12360426453114},{"bearing":190.0141719806364},{"bearing":190.1178044525547},{"bearing":190.2964752220224},{"bearing":190.11518873969766},{"bearing":280.3991549802436},{"bearing":280.69588044550915},{"bearing":280.8881894602589},{"bearing":225.7994388466382},{"bearing":225.124947443217},{"bearing":225.3375223775585},{"bearing":225.68346281631136},{"bearing":225.88212719506066},{"bearing":225.9967366577183},{"bearing":225.9452489039822},{"bearing":225.941369377467},{"bearing":292.754256024937},{"bearing":292.5969494886398},{"bearing":292.62248796021873},{"bearing":270.0978866567492},{"bearing":315.4933280262193},{"bearing":315.26737386851784},{"bearing":315.07774095258145},{"bearing":270.5292811342495},{"bearing":270.33317950718225},{"bearing":270.7369170774734},{"bearing":270.33755264429493},{"bearing":270.22099931152},{"bearing":270.14596814386226},{"bearing":225.8146300760897},{"bearing":225.5435885774},{"bearing":225.70141122373343},{"bearing":225.7371060197024},{"bearing":225.84091650056644},{"bearing":225.0646971361726},{"bearing":202.0414746838112},{"bearing":202.72543135985177},{"bearing":202.67766159305677},{"bearing":202.4266596766422},{"bearing":202.9829280252652},{"bearing":202.1213086402688},{"bearing":202.7578307037749},{"bearing":315.8154967984522},{"bearing":315.15489066656977},{"bearing":315.56881494450784},{"bearing":270.35093365316476},{"bearing":270.3049016862588},{"bearing":270.2253746431676},{"bearing":292.9680042759357},{"bearing":292.1750422651625},{"bearing":292.2183461548711},{"bearing":292.1807437678995},{"bearing":337.68327164080233},{"bearing":337.2841506284799},{"bearing":337.5261870056053},{"bearing":350.3527711500038},{"bearing":350.5901930196057},{"bearing":350.0554453765866},{"bearing":315.87426597095185},{"bearing":315.52653614484257},{"bearing":315.53650453422983},{"bearing":270.61409248709145},{"bearing":270.9443953982858},{"bearing":270.0001137902833},{"bearing":270.8347432622061},{"bearing":0.42149126512047},{"bearing":0.01844002728058},{"bearing":0.0850171465935},{"bearing":0.4027465431566},{"bearing":90.60821304541774},{"bearing":90.82155727114743},{"bearing":90.91884219905114},{"bearing":90.2203765240971},{"bearing":180.5041001290254},{"bearing":180.13544020814567},{"bearing":180.7662118430034},{"bearing":180.76618821644584},{"bearing":180.671008652173},{"bearing":180.10850161364857},{"bearing":180.7372725814315},{"bearing":180.27525989053152},{"bearing":135.77406077862352},{"bearing":135.68466246376354},{"bearing":135.83823775929625},{"bearing":180.9154808317913},{"bearing":180.36659586009984},{"bearing":170.1796595237955},{"bearing":175.07815000000002},{"bearing":175.07815000000002},{"bearing":135.98565465790494},{"bearing":135.33843711429387},{"bearing":135.55270334282545},{"bearing":135.80626200779662},{"bearing":135.15372311976665},{"bearing":90.1416069726343},{"bearing":90.83951083816466},{"bearing":90.39504362121495},{"bearing":90.48287392999826},{"bearing":135.44099023827744},{"bearing":135.96670677158016},{"bearing":135.3892917282967},{"bearing":135.45411068742703},{"bearing":135.7526493551743},{"bearing":190.8104499624585},{"bearing":190.58151170010217},{"bearing":190.11080797915872},{"bearing":170.2372923710435},{"bearing":170.99978841329175},{"bearing":200.8590273441368},{"bearing":200.33893181601013},{"bearing":200.829008190173},{"bearing":190.77912232828257},{"bearing":190.12893767973236},{"bearing":190.7806674847536},{"bearing":247.0220319527234},{"bearing":247.5781764241615},{"bearing":247.0799514513747},{"bearing":247.26376429624827},{"bearing":247.1539081591596},{"bearing":247.13222267870026},{"bearing":247.6136853121001},{"bearing":247.31929395154367},{"bearing":247.88284184307287},{"bearing":247.08772557276075},{"bearing":247.027375377685},{"bearing":247.79220006411987},{"bearing":247.76047248294657},{"bearing":247.25680856207157},{"bearing":247.07904801599807},{"bearing":247.38503131140664},{"bearing":247.29026488200236},{"bearing":247.54819986294177},{"bearing":247.8506894232289},{"bearing":247.67313543003144},{"bearing":67.6730874917155},{"bearing":67.85025198118791},{"bearing":67.54776241620999},{"bearing":67.2898274328566},{"bearing":67.38408449997966},{"bearing":67.07878434651126},{"bearing":67.25598160050022},{"bearing":67.76011293559736},{"bearing":67.7915648660537},{"bearing":67.0264885082261},{"bearing":67.08674884479154},{"bearing":67.88206887030952},{"bearing":67.3189344347344},{"bearing":80.61308612426588},{"bearing":80.13152163555206},{"bearing":80.15297943537567},{"bearing":80.26288351113288},{"bearing":80.07956199013033},{"bearing":80.57750535907829},{"bearing":80.0214507693884},{"bearing":80.7804457979296},{"bearing":80.1290515187517},{"bearing":80.77919422586697},{"bearing":10.82894228484273},{"bearing":10.33882996386032},{"bearing":10.85872179144516},{"bearing":10.85872179144516},{"bearing":10.23743615601876},{"bearing":350.1110536091697},{"bearing":350.58152368191975},{"bearing":10.810078534712},{"bearing":10.90458239918917},{"bearing":10.98238887392421},{"bearing":22.4260905804508},{"bearing":22.67743395767528},{"bearing":22.7247604498226},{"bearing":22.04143275302908},{"bearing":22.06465520605374},{"bearing":22.84069487126592},{"bearing":45.73668073593075},{"bearing":45.14184184099673},{"bearing":45.528413504806},{"bearing":45.54275003199774},{"bearing":45.81370167507563},{"bearing":45.08713553423239},{"bearing":45.1043413802749},{"bearing":84.73618634074877},{"bearing":78.33241881974213},{"bearing":99.52705298630934},{"bearing":85.07756725300986},{"bearing":38.266978550964325},{"bearing":33.49250143061488},{"bearing":98.09646704554466},{"bearing":66.62124205002686},{"bearing":64.59609890283856},{"bearing":45.75389661966224},{"bearing":45.94066853797952},{"bearing":45.94482960113532},{"bearing":45.99644913843596},{"bearing":45.8818576510457},{"bearing":45.845140734758},{"bearing":45.765032470939},{"bearing":45.32961462349803},{"bearing":45.4664584783677},{"bearing":80.87680805090449},{"bearing":80.03691635100756},{"bearing":80.05761135899434},{"bearing":100.73605275749945},{"bearing":100.43126784470581},{"bearing":100.4095951260115},{"bearing":45.2012877696244},{"bearing":45.92093291904376},{"bearing":45.3786928389578},{"bearing":45.74712570037592},{"bearing":45.12069466742388},{"bearing":80.35194396896426},{"bearing":80.6006343256616},{"bearing":80.7633482813142},{"bearing":80.50251891771723},{"bearing":10.3823025742155},{"bearing":10.1587841017719},{"bearing":10.82024415309348},{"bearing":10.81098243184294},{"bearing":45.77496078603923},{"bearing":45.65759124168983},{"bearing":45.97649573835736},{"bearing":45.95925210856164},{"bearing":45.12139446445688},{"bearing":45.3577439485154},{"bearing":98.60601149858417},{"bearing":124.77738237877428},{"bearing":115.227897774992},{"bearing":128.4687987639044},{"bearing":138.80639520196368},{"bearing":58.02420624688715},{"bearing":72.66470446088304},{"bearing":118.15116068776942},{"bearing":173.47275630562942},{"bearing":131.95432326166576},{"bearing":91.68248934022678},{"bearing":109.63264664787427},{"bearing":89.99987725349592},{"bearing":48.96989091684708},{"bearing":135.57348403376},{"bearing":135.9564012621862},{"bearing":135.79211877629467},{"bearing":135.16060958962274},{"bearing":135.69805929740951},{"bearing":135.06674442221865},{"bearing":135.6009354927337},{"bearing":135.19832421089538},{"bearing":135.0221369084937},{"bearing":100.23673142529071},{"bearing":100.06691844120064},{"bearing":190.71218185959486},{"bearing":190.104633908355},{"bearing":190.10747721934206},{"bearing":190.63411325260432},{"bearing":190.4178867506785},{"bearing":190.95031992248806},{"bearing":80.82871150113895},{"bearing":80.54928077029165},{"bearing":80.80894024848979},{"bearing":80.2827130600253},{"bearing":80.2556833936919},{"bearing":225.0653197994334},{"bearing":225.18771229763644},{"bearing":225.34590400706537},{"bearing":225.31201210988576},{"bearing":45.51025227711523},{"bearing":45.05199196625935},{"bearing":45.87789654492207},{"bearing":45.01787092343032},{"bearing":45.70418867549859},{"bearing":45.37260934975302},{"bearing":280.3652916677259},{"bearing":280.07068178521536},{"bearing":280.7136240810428},{"bearing":280.9184302151336},{"bearing":280.1542514936852},{"bearing":280.43752874280744},{"bearing":45.99081422307455},{"bearing":45.56179173961587},{"bearing":45.90190496439396},{"bearing":45.52677428601703},{"bearing":45.81009617935925},{"bearing":315.41365708494493},{"bearing":45.97586020922023},{"bearing":45.35702612122728},{"bearing":315.89701264428686},{"bearing":315.0896172620172},{"bearing":315.12131074869484},{"bearing":315.489288430752},{"bearing":315.0339049883577},{"bearing":315.8615052883179},{"bearing":315.9943915218138},{"bearing":99.64330454965575},{"bearing":96.37092290964912},{"bearing":111.93747750461209},{"bearing":107.88740858139339},{"bearing":100.25649003445321},{"bearing":82.70235588955977},{"bearing":85.73069215792195},{"bearing":115.22747418323348},{"bearing":136.36397218424327},{"bearing":128.30500650708527},{"bearing":92.50810514856741},{"bearing":107.0485185910385},{"bearing":126.13970537534976},{"bearing":121.04663509316472}]
        """.trimIndent()

        val bearingBus = ArrayList<BusBearing>()
        val jsonArray = JSONArray(jsonString)

        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            val bearing = jsonObject.getDouble("bearing")
            bearingBus.add(BusBearing(bearing))
        }
        return bearingBus
    }

    /**
     * Retrieves a list of BusBearingCustomer objects representing the bearings of customer buses.
     * The bearings are hardcoded as a JSON string.
     *
     * @return List of BusBearingCustomer representing the customer bus bearings.
     */
    fun getBearingCustomer() : List<BusBearingCustomer> {
        val jsonString = """
          [{"bearing":330.51747258053865},{"bearing":84.90383942298206},{"bearing":132.1621240664246},{"bearing":125.08068429958428},{"bearing":94.13165669471982},{"bearing":104.52780329569032},{"bearing":134.96690427907936},{"bearing":126.58972805610438},{"bearing":99.0092775021285},{"bearing":82.3945348821494},{"bearing":98.7576264769906},{"bearing":111.95074937509304},{"bearing":98.32268707210585},{"bearing":96.36138519156032},{"bearing":60.51674536589826},{"bearing":39.83808626883604},{"bearing":38.69215303834528},{"bearing":80.67068962161198},{"bearing":111.01558390628168},{"bearing":87.85518745849288},{"bearing":129.2840163031571},{"bearing":90.00001197368016},{"bearing":133.1194970997825},{"bearing":172.39671966480273},{"bearing":142.89539510843042},{"bearing":135.39302488197097},{"bearing":119.02778717203068},{"bearing":124.61712067435025},{"bearing":88.32414122762191},{"bearing":89.37828134744143},{"bearing":104.02149809008228},{"bearing":119.02788733898927},{"bearing":100.96543437823067},{"bearing":99.91565337064378},{"bearing":123.43273818084839},{"bearing":122.84686404587893},{"bearing":144.88519575423783},{"bearing":128.03008840987758},{"bearing":131.8755360762467},{"bearing":188.71864343361005},{"bearing":158.53811453783237},{"bearing":146.76577472299311},{"bearing":161.81209400169314},{"bearing":183.1396125442667},{"bearing":195.3912253906884},{"bearing":191.65491653295223},{"bearing":121.80441270057702},{"bearing":77.01818913879015},{"bearing":76.8223384486351},{"bearing":149.12360426453114},{"bearing":162.01417198063632},{"bearing":175.11780445255476},{"bearing":155.29647522202242},{"bearing":100.11518873969766},{"bearing":74.39915498024368},{"bearing":93.69588044550915},{"bearing":113.8881894602589},{"bearing":134.79943884663822},{"bearing":133.12494744321702},{"bearing":154.33752237755857},{"bearing":153.68346281631136},{"bearing":162.88212719506066},{"bearing":142.99673665771832},{"bearing":118.94524890398219},{"bearing":104.94136937746703},{"bearing":73.75425602493698},{"bearing":64.59694948863978},{"bearing":66.62248796021873},{"bearing":98.09788665674921},{"bearing":33.493328026219274},{"bearing":38.267373868517836},{"bearing":85.07774095258145},{"bearing":99.52928113424952},{"bearing":78.33317950718225},{"bearing":84.73691707747338},{"bearing":75.33755264429493},{"bearing":95.22099931152002},{"bearing":114.14596814386226},{"bearing":121.81463007608971},{"bearing":116.54358857739999},{"bearing":134.70141122373343},{"bearing":127.73710601970242},{"bearing":148.84091650056644},{"bearing":170.06469713617253},{"bearing":176.0414746838112},{"bearing":147.72543135985177},{"bearing":148.67766159305677},{"bearing":133.4266596766422},{"bearing":121.98292802526521},{"bearing":94.12130864026881},{"bearing":60.757830703774914},{"bearing":35.81549679845216},{"bearing":53.15489066656977},{"bearing":94.56881494450784},{"bearing":94.35093365316476},{"bearing":75.30490168625886},{"bearing":72.22537464316758},{"bearing":60.9680042759357},{"bearing":55.17504226516246},{"bearing":23.218346154871085},{"bearing":354.1807437678995},{"bearing":333.68327164080233},{"bearing":326.2841506284799},{"bearing":6.526187005605323},{"bearing":15.352771150003775},{"bearing":50.59019301960575},{"bearing":37.05544537658659},{"bearing":20.874265970951853},{"bearing":6.5265361448425665},{"bearing":110.53650453422983},{"bearing":135.61409248709145},{"bearing":125.94439539828579},{"bearing":90.00011379028331},{"bearing":1.834743262206075},{"bearing":4.421491265120437},{"bearing":4.018440027280576},{"bearing":5.085017146593486},{"bearing":273.4027465431566},{"bearing":273.60821304541776},{"bearing":272.8215572711474},{"bearing":180.00000029188422},{"bearing":95.2203765240971},{"bearing":181.5041001290254},{"bearing":271.13544020814567},{"bearing":186.7662118430034},{"bearing":186.76618821644584},{"bearing":170.671008652173},{"bearing":166.1085016136485},{"bearing":182.7372725814315},{"bearing":210.27525989053152},{"bearing":224.77406077862352},{"bearing":229.68466246376354},{"bearing":189.83823775929625},{"bearing":174.91548083179123},{"bearing":143.36659586009984},{"bearing":158.17965952379552},{"bearing":176.60544100396055},{"bearing":206.2328927501124},{"bearing":234.98565465790494},{"bearing":235.33843711429387},{"bearing":248.55270334282545},{"bearing":251.80626200779665},{"bearing":254.15372311976665},{"bearing":273.1416069726343},{"bearing":274.83951083816464},{"bearing":275.39504362121494},{"bearing":259.4828739299983},{"bearing":215.44099023827744},{"bearing":222.96670677158016},{"bearing":259.3892917282967},{"bearing":279.45411068742703},{"bearing":161.7526493551743},{"bearing":159.8104499624585},{"bearing":181.58151170010217},{"bearing":216.11080797915872},{"bearing":202.2372923710435},{"bearing":142.99978841329175},{"bearing":141.8590273441368},{"bearing":166.33893181601013},{"bearing":172.829008190173},{"bearing":195.77912232828257},{"bearing":234.12893767973236},{"bearing":142.78066748475362},{"bearing":113.02203195272341},{"bearing":127.57817642416148},{"bearing":109.07995145137471},{"bearing":97.26376429624827},{"bearing":103.15390815915958},{"bearing":117.13222267870026},{"bearing":113.61368531210007},{"bearing":141.31929395154367},{"bearing":124.88284184307287},{"bearing":140.08772557276075},{"bearing":124.02737537768502},{"bearing":105.79220006411987},{"bearing":94.76047248294657},{"bearing":102.25680856207157},{"bearing":98.07904801599807},{"bearing":78.38503131140664},{"bearing":80.29026488200236},{"bearing":60.54819986294177},{"bearing":42.850689423228914},{"bearing":11.673135430031436},{"bearing":191.6730874917155},{"bearing":222.8502519811879},{"bearing":240.54776241621},{"bearing":260.2898274328566},{"bearing":258.3840844999797},{"bearing":278.07878434651127},{"bearing":282.2559816005002},{"bearing":274.76011293559736},{"bearing":285.7915648660537},{"bearing":304.02648850822607},{"bearing":320.08674884479154},{"bearing":304.8820688703095},{"bearing":321.3189344347344},{"bearing":293.61308612426586},{"bearing":297.1315216355521},{"bearing":283.15297943537564},{"bearing":277.2628835111329},{"bearing":289.07956199013034},{"bearing":307.5775053590783},{"bearing":293.02145076938837},{"bearing":322.7804457979296},{"bearing":54.12905151875168},{"bearing":15.779194225866945},{"bearing":352.8289422848427},{"bearing":346.3388299638603},{"bearing":321.8587217914452},{"bearing":322.99930912271407},{"bearing":22.23743615601876},{"bearing":36.11105360916969},{"bearing":1.581523681919748},{"bearing":339.810078534712},{"bearing":334.90458239918917},{"bearing":301.9823888739242},{"bearing":313.4260905804508},{"bearing":328.67743395767525},{"bearing":327.7247604498226},{"bearing":356.0414327530291},{"bearing":350.0646552060537},{"bearing":328.84069487126595},{"bearing":307.73668073593075},{"bearing":316.14184184099673},{"bearing":300.528413504806},{"bearing":296.54275003199774},{"bearing":301.8137016750756},{"bearing":288.0871355342324},{"bearing":257.1043413802749},{"bearing":264.73618634074876},{"bearing":258.33241881974214},{"bearing":279.52705298630934},{"bearing":265.0775672530099},{"bearing":218.26697855096432},{"bearing":213.49250143061488},{"bearing":278.09646704554467},{"bearing":246.62124205002686},{"bearing":244.59609890283855},{"bearing":253.75389661966224},{"bearing":284.9406685379795},{"bearing":298.9448296011353},{"bearing":322.99644913843593},{"bearing":342.8818576510457},{"bearing":310.845140734758},{"bearing":343.765032470939},{"bearing":349.329614623498},{"bearing":316.4664584783677},{"bearing":288.8768080509045},{"bearing":295.03691635100756},{"bearing":256.05761135899434},{"bearing":265.73605275749946},{"bearing":325.4312678447058},{"bearing":346.4095951260115},{"bearing":358.2012877696244},{"bearing":330.92093291904376},{"bearing":329.37869283895776},{"bearing":257.74712570037593},{"bearing":255.1206946674239},{"bearing":256.3519439689643},{"bearing":291.6006343256616},{"bearing":306.7633482813142},{"bearing":16.502518917717225},{"bearing":5.382302574215487},{"bearing":349.1587841017719},{"bearing":327.8202441530935},{"bearing":8.810982431842945},{"bearing":309.77496078603923},{"bearing":318.65759124168983},{"bearing":301.97649573835736},{"bearing":281.95925210856166},{"bearing":294.1213944644569},{"bearing":269.3577439485154},{"bearing":278.60601149858417},{"bearing":304.7773823787743},{"bearing":295.227897774992},{"bearing":308.46879876390443},{"bearing":318.8063952019637},{"bearing":238.02420624688716},{"bearing":252.66470446088306},{"bearing":298.1511606877694},{"bearing":353.47275630562945},{"bearing":311.95432326166576},{"bearing":271.68248934022677},{"bearing":289.63264664787425},{"bearing":269.9998772534959},{"bearing":228.96989091684708},{"bearing":208.57348403376},{"bearing":216.9564012621862},{"bearing":219.79211877629467},{"bearing":233.16060958962277},{"bearing":208.69805929740951},{"bearing":202.06674442221865},{"bearing":167.6009354927337},{"bearing":199.19832421089538},{"bearing":238.02213690849374},{"bearing":196.2367314252907},{"bearing":147.06691844120064},{"bearing":142.71218185959486},{"bearing":152.10463390835503},{"bearing":173.10747721934206},{"bearing":211.63411325260432},{"bearing":255.41788675067846},{"bearing":289.95031992248806},{"bearing":254.82871150113897},{"bearing":238.54928077029166},{"bearing":251.8089402484898},{"bearing":146.2827130600253},{"bearing":139.25568339369192},{"bearing":129.06531979943338},{"bearing":105.18771229763644},{"bearing":165.34590400706543},{"bearing":141.31201210988576},{"bearing":305.5102522771152},{"bearing":355.0519919662594},{"bearing":325.87789654492207},{"bearing":291.01787092343034},{"bearing":319.7041886754986},{"bearing":60.37260934975302},{"bearing":70.3652916677259},{"bearing":74.07068178521536},{"bearing":100.71362408104278},{"bearing":106.91843021513358},{"bearing":59.154251493685194},{"bearing":15.437528742807444},{"bearing":347.99081422307455},{"bearing":323.56179173961584},{"bearing":331.90190496439396},{"bearing":6.52677428601703},{"bearing":53.81009617935922},{"bearing":2.41365708494493},{"bearing":352.97586020922023},{"bearing":31.35702612122725},{"bearing":27.89701264428686},{"bearing":62.08961726201716},{"bearing":42.12131074869484},{"bearing":33.489288430752026},{"bearing":33.033904988357676},{"bearing":305.8615052883179},{"bearing":250.9943915218138},{"bearing":279.64330454965574},{"bearing":276.37092290964915},{"bearing":291.9374775046121},{"bearing":287.8874085813934},{"bearing":280.25649003445324},{"bearing":262.7023558895598},{"bearing":265.73069215792196},{"bearing":295.2274741832335},{"bearing":316.36397218424327},{"bearing":308.30500650708524},{"bearing":272.50810514856744},{"bearing":287.0485185910385},{"bearing":306.13970537534976},{"bearing":301.0466350931647},{"bearing":301.0466350931647}]
        """.trimIndent()

        val bearingCustomerBus = ArrayList<BusBearingCustomer>()
        val jsonArray = JSONArray(jsonString)

        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            val bearing = jsonObject.getDouble("bearing")
            bearingCustomerBus.add(BusBearingCustomer(bearing))
        }
        return bearingCustomerBus
    }

    /**
     * Retrieves a list of BusStopInfo objects representing bus stops with additional information.
     * The stops are hardcoded as a JSON string.
     *
     * @return List of BusStopInfo representing the bus stops with additional information.
     */
    fun getBusStopOfflineWithName() : List<BusStopInfo> {
        val jsonString = """
           {
  "1": [
    {
      "latitude": -36.78125,
      "longitude": 175.007,
      "bus-stop-name": "1",
      "distance-to-next-stop": 436
    },
    {
      "latitude": -36.78316,
      "longitude": 175.01086,
      "bus-stop-name": "2",
      "distance-to-next-stop": 3731
    },
    {
      "latitude": -36.79871,
      "longitude": 175.03447,
      "bus-stop-name": "3",
      "distance-to-next-stop": 1348
    },
    {
      "latitude": -36.79569,
      "longitude": 175.04737,
      "bus-stop-name": "4",
      "distance-to-next-stop": 2115
    },
    {
      "latitude": -36.80121,
      "longitude": 175.06579,
      "bus-stop-name": "5",
      "distance-to-next-stop": 374
    },
    {
      "latitude": -36.80069,
      "longitude": 175.06972,
      "bus-stop-name": "6",
      "distance-to-next-stop": 559
    },
    {
      "latitude": -36.79867,
      "longitude": 175.07527,
      "bus-stop-name": "7",
      "distance-to-next-stop": 1688
    },
    {
      "latitude": -36.78842,
      "longitude": 175.08302,
      "bus-stop-name": "8",
      "distance-to-next-stop": 2262
    },
    {
      "latitude": -36.8014,
      "longitude": 175.06984,
      "bus-stop-name": "9",
      "distance-to-next-stop": 361
    },
    {
      "latitude": -36.80169,
      "longitude": 175.06619,
      "bus-stop-name": "10",
      "distance-to-next-stop": 3595
    },
    {
      "latitude": -36.81456,
      "longitude": 175.08249,
      "bus-stop-name": "11",
      "distance-to-next-stop": 2187
    },
    {
      "latitude": -36.80916,
      "longitude": 175.06174,
      "bus-stop-name": "12",
      "distance-to-next-stop": 2319
    },
    {
      "latitude": -36.79621,
      "longitude": 175.04829,
      "bus-stop-name": "13",
      "distance-to-next-stop": 1789
    },
    {
      "latitude": -36.79699,
      "longitude": 175.03212,
      "bus-stop-name": "14",
      "distance-to-next-stop": 2858
    },
    {
      "latitude": -36.78385,
      "longitude": 175.01139,
      "bus-stop-name": "15",
      "distance-to-next-stop": 2286
    },
    {
      "latitude": -36.79159,
      "longitude": 174.99938,
      "bus-stop-name": "16",
      "distance-to-next-stop": 886
    },
    {
      "latitude": -36.78724,
      "longitude": 175.00125,
      "bus-stop-name": "17",
      "distance-to-next-stop": 2002
    },
    {
      "latitude": -36.78012,
      "longitude": 174.99216,
      "bus-stop-name": "S/E",
      "distance-to-next-stop": 1447
    }
  ]
}
        """.trimIndent()
        val busStops = mutableListOf<BusStopInfo>()

        val jsonObject = JSONObject(jsonString)
        val jsonArray = jsonObject.getJSONArray("1")

        for (i in 0 until jsonArray.length()) {
            val item = jsonArray.getJSONObject(i)
            val busStopName = item.getString("bus-stop-name")
            val latitude = item.getDouble("latitude")
            val longitude = item.getDouble("longitude")
            val distanceToNextStop = item.getDouble("distance-to-next-stop")
            busStops.add(BusStopInfo(busStopName, latitude, longitude, distanceToNextStop))
        }
        return busStops
    }
}



