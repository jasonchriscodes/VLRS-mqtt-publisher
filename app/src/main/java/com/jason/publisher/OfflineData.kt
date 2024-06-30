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
        val jsonString1 = """
           [{"latitude":-36.780120000000004,"longitude":174.99216},{"latitude":-36.77995,"longitude":174.99204},{"latitude":-36.77994,"longitude":174.99218000000002},{"latitude":-36.7806,"longitude":174.99309000000002},{"latitude":-36.78078,"longitude":174.99341},{"latitude":-36.78085,"longitude":174.99462000000003},{"latitude":-36.78096,"longitude":174.99515000000002},{"latitude":-36.78132,"longitude":174.99560000000002},{"latitude":-36.78154,"longitude":174.99597000000003},{"latitude":-36.781620000000004,"longitude":174.9966},{"latitude":-36.781420000000004,"longitude":174.99847000000003},{"latitude":-36.78161,"longitude":175.00001},{"latitude":-36.782120000000006,"longitude":175.00159000000002},{"latitude":-36.7824,"longitude":175.00398},{"latitude":-36.782500000000006,"longitude":175.00510000000003},{"latitude":-36.78226,"longitude":175.00563000000002},{"latitude":-36.781780000000005,"longitude":175.00613},{"latitude":-36.78146,"longitude":175.00645},{"latitude":-36.78141,"longitude":175.00683},{"latitude":-36.781650000000006,"longitude":175.00761000000003},{"latitude":-36.781620000000004,"longitude":175.00861},{"latitude":-36.78181,"longitude":175.0089},{"latitude":-36.78181,"longitude":175.00894000000002},{"latitude":-36.78184,"longitude":175.00898},{"latitude":-36.7819,"longitude":175.00899},{"latitude":-36.78226,"longitude":175.00933},{"latitude":-36.783210000000004,"longitude":175.0105},{"latitude":-36.784090000000006,"longitude":175.01248},{"latitude":-36.78477,"longitude":175.01371},{"latitude":-36.784740000000006,"longitude":175.01499},{"latitude":-36.78473,"longitude":175.01614},{"latitude":-36.784890000000004,"longitude":175.01694},{"latitude":-36.785250000000005,"longitude":175.01775},{"latitude":-36.785340000000005,"longitude":175.01833000000002},{"latitude":-36.785410000000006,"longitude":175.01883},{"latitude":-36.78587,"longitude":175.0197},{"latitude":-36.78678,"longitude":175.02146000000002},{"latitude":-36.78719,"longitude":175.02182000000002},{"latitude":-36.787760000000006,"longitude":175.02273000000002},{"latitude":-36.78804,"longitude":175.02312},{"latitude":-36.78851,"longitude":175.02303},{"latitude":-36.789060000000006,"longitude":175.0233},{"latitude":-36.789280000000005,"longitude":175.02348},{"latitude":-36.78967,"longitude":175.02364},{"latitude":-36.790400000000005,"longitude":175.02359},{"latitude":-36.79168,"longitude":175.02315000000002},{"latitude":-36.79234,"longitude":175.02298000000002},{"latitude":-36.79307,"longitude":175.02445},{"latitude":-36.792950000000005,"longitude":175.0251},{"latitude":-36.792860000000005,"longitude":175.02558000000002},{"latitude":-36.79361,"longitude":175.02614000000003},{"latitude":-36.793980000000005,"longitude":175.02629000000002},{"latitude":-36.79473,"longitude":175.02637000000001},{"latitude":-36.7952,"longitude":175.02664000000001},{"latitude":-36.79529,"longitude":175.02727000000002},{"latitude":-36.79493,"longitude":175.02888000000002},{"latitude":-36.79496,"longitude":175.02946},{"latitude":-36.795570000000005,"longitude":175.03118},{"latitude":-36.79623,"longitude":175.03201},{"latitude":-36.796380000000006,"longitude":175.03221000000002},{"latitude":-36.79643,"longitude":175.03224},{"latitude":-36.79677,"longitude":175.03245},{"latitude":-36.797940000000004,"longitude":175.0329},{"latitude":-36.79845,"longitude":175.03338000000002},{"latitude":-36.79876,"longitude":175.03408000000002},{"latitude":-36.79901,"longitude":175.03525000000002},{"latitude":-36.79887,"longitude":175.03585},{"latitude":-36.79833,"longitude":175.03727},{"latitude":-36.797610000000006,"longitude":175.03935},{"latitude":-36.797880000000006,"longitude":175.04172000000003},{"latitude":-36.79621,"longitude":175.0431},{"latitude":-36.79554,"longitude":175.04376000000002},{"latitude":-36.79552,"longitude":175.04405000000003},{"latitude":-36.796020000000006,"longitude":175.04777},{"latitude":-36.79581,"longitude":175.04904000000002},{"latitude":-36.79572,"longitude":175.05026},{"latitude":-36.795500000000004,"longitude":175.05131},{"latitude":-36.79553000000001,"longitude":175.05172000000002},{"latitude":-36.79567,"longitude":175.05211000000003},{"latitude":-36.796440000000004,"longitude":175.05366},{"latitude":-36.79704,"longitude":175.05516},{"latitude":-36.79746,"longitude":175.05569000000003},{"latitude":-36.797900000000006,"longitude":175.05640000000002},{"latitude":-36.798390000000005,"longitude":175.05677},{"latitude":-36.79871,"longitude":175.05684000000002},{"latitude":-36.79952,"longitude":175.05691000000002},{"latitude":-36.800940000000004,"longitude":175.05803},{"latitude":-36.80144,"longitude":175.05841},{"latitude":-36.80216,"longitude":175.05936000000003},{"latitude":-36.80261,"longitude":175.06026000000003},{"latitude":-36.802640000000004,"longitude":175.06078000000002},{"latitude":-36.80238000000001,"longitude":175.06136},{"latitude":-36.801570000000005,"longitude":175.06209},{"latitude":-36.80124,"longitude":175.06264000000002},{"latitude":-36.80143,"longitude":175.06561000000002},{"latitude":-36.801550000000006,"longitude":175.06758000000002},{"latitude":-36.80113,"longitude":175.06958},{"latitude":-36.80046,"longitude":175.07219},{"latitude":-36.799820000000004,"longitude":175.07363},{"latitude":-36.797380000000004,"longitude":175.07801},{"latitude":-36.7971,"longitude":175.07816000000003},{"latitude":-36.79655,"longitude":175.07809},{"latitude":-36.79621,"longitude":175.07788000000002},{"latitude":-36.79549,"longitude":175.07728},{"latitude":-36.79437,"longitude":175.07744000000002},{"latitude":-36.793670000000006,"longitude":175.07768000000002},{"latitude":-36.7924,"longitude":175.07961},{"latitude":-36.79065000000001,"longitude":175.08126000000001},{"latitude":-36.790440000000004,"longitude":175.08136000000002},{"latitude":-36.79023,"longitude":175.08139000000003},{"latitude":-36.790290000000006,"longitude":175.08159},{"latitude":-36.790560000000006,"longitude":175.08192000000003},{"latitude":-36.79092,"longitude":175.08254000000002},{"latitude":-36.79092,"longitude":175.08292},{"latitude":-36.790420000000005,"longitude":175.08294},{"latitude":-36.788970000000006,"longitude":175.08308000000002},{"latitude":-36.7884,"longitude":175.08313},{"latitude":-36.78804,"longitude":175.08317000000002},{"latitude":-36.788030000000006,"longitude":175.08296},{"latitude":-36.787980000000005,"longitude":175.08197},{"latitude":-36.78795,"longitude":175.08121000000003}, {"latitude":-36.787954,"longitude":175.08121},{"latitude":-36.787957,"longitude":175.081251},{"latitude":-36.788201,"longitude":175.081243},{"latitude":-36.7882,"longitude":175.08118000000002},{"latitude":-36.788470000000004,"longitude":175.08114},{"latitude":-36.788740000000004,"longitude":175.08110000000002},{"latitude":-36.78913,"longitude":175.08118000000002},{"latitude":-36.78981,"longitude":175.08139000000003},{"latitude":-36.79048,"longitude":175.08135000000001},{"latitude":-36.79155,"longitude":175.08057000000002},{"latitude":-36.792680000000004,"longitude":175.07917},{"latitude":-36.79374,"longitude":175.07761000000002},{"latitude":-36.79531,"longitude":175.07727000000003},{"latitude":-36.79558,"longitude":175.0773},{"latitude":-36.79628,"longitude":175.07795000000002},{"latitude":-36.796600000000005,"longitude":175.07811},{"latitude":-36.797140000000006,"longitude":175.07815000000002},{"latitude":-36.7974,"longitude":175.07799000000003},{"latitude":-36.79878,"longitude":175.07553000000001},{"latitude":-36.800070000000005,"longitude":175.0732},{"latitude":-36.800740000000005,"longitude":175.07107000000002},{"latitude":-36.80124,"longitude":175.06917},{"latitude":-36.80154,"longitude":175.06785000000002},{"latitude":-36.801460000000006,"longitude":175.06603},{"latitude":-36.80142,"longitude":175.06544000000002},{"latitude":-36.80124,"longitude":175.06306},{"latitude":-36.801350000000006,"longitude":175.06232000000003},{"latitude":-36.80207,"longitude":175.06168000000002},{"latitude":-36.80256,"longitude":175.06111},{"latitude":-36.80265,"longitude":175.06051000000002},{"latitude":-36.80263,"longitude":175.06036},{"latitude":-36.803140000000006,"longitude":175.06057},{"latitude":-36.80449,"longitude":175.06119},{"latitude":-36.80507,"longitude":175.06117},{"latitude":-36.80552,"longitude":175.06076000000002},{"latitude":-36.80599,"longitude":175.06052000000003},{"latitude":-36.80684,"longitude":175.06132000000002},{"latitude":-36.80736,"longitude":175.06183000000001},{"latitude":-36.80792,"longitude":175.062},{"latitude":-36.808620000000005,"longitude":175.06211000000002},{"latitude":-36.808960000000006,"longitude":175.06199},{"latitude":-36.809070000000006,"longitude":175.0618},{"latitude":-36.80946,"longitude":175.06217},{"latitude":-36.80979,"longitude":175.06314},{"latitude":-36.810480000000005,"longitude":175.06426000000002},{"latitude":-36.810660000000006,"longitude":175.06491000000003},{"latitude":-36.810810000000004,"longitude":175.06638},{"latitude":-36.8111,"longitude":175.06793000000002},{"latitude":-36.811580000000006,"longitude":175.06910000000002},{"latitude":-36.811930000000004,"longitude":175.07010000000002},{"latitude":-36.81253,"longitude":175.07070000000002},{"latitude":-36.813250000000004,"longitude":175.07199000000003},{"latitude":-36.81481,"longitude":175.07362},{"latitude":-36.81561,"longitude":175.07510000000002},{"latitude":-36.815850000000005,"longitude":175.07616000000002},{"latitude":-36.81589,"longitude":175.07676},{"latitude":-36.81613,"longitude":175.07814000000002},{"latitude":-36.81618,"longitude":175.07858000000002},{"latitude":-36.815920000000006,"longitude":175.08016},{"latitude":-36.81582,"longitude":175.08089},{"latitude":-36.815490000000004,"longitude":175.08162000000002},{"latitude":-36.81486,"longitude":175.08235000000002},{"latitude":-36.814550000000004,"longitude":175.08243000000002},{"latitude":-36.81486,"longitude":175.08235000000002},{"latitude":-36.815490000000004,"longitude":175.08162000000002},{"latitude":-36.81582,"longitude":175.08089},{"latitude":-36.815920000000006,"longitude":175.08016},{"latitude":-36.81618,"longitude":175.07858000000002},{"latitude":-36.81613,"longitude":175.07814000000002},{"latitude":-36.81589,"longitude":175.07676},{"latitude":-36.815850000000005,"longitude":175.07616000000002},{"latitude":-36.81561,"longitude":175.07510000000002},{"latitude":-36.81481,"longitude":175.07362},{"latitude":-36.813250000000004,"longitude":175.07199000000003},{"latitude":-36.81253,"longitude":175.07070000000002},{"latitude":-36.811930000000004,"longitude":175.07010000000002},{"latitude":-36.811580000000006,"longitude":175.06910000000002},{"latitude":-36.8111,"longitude":175.06793000000002},{"latitude":-36.810810000000004,"longitude":175.06638},{"latitude":-36.810660000000006,"longitude":175.06491000000003},{"latitude":-36.810480000000005,"longitude":175.06426000000002},{"latitude":-36.80979,"longitude":175.06314},{"latitude":-36.80946,"longitude":175.06217},{"latitude":-36.809070000000006,"longitude":175.0618},{"latitude":-36.808960000000006,"longitude":175.06199},{"latitude":-36.808620000000005,"longitude":175.06211000000002},{"latitude":-36.80792,"longitude":175.062},{"latitude":-36.80736,"longitude":175.06183000000001},{"latitude":-36.80684,"longitude":175.06132000000002},{"latitude":-36.80599,"longitude":175.06052000000003},{"latitude":-36.80552,"longitude":175.06076000000002},{"latitude":-36.80507,"longitude":175.06117},{"latitude":-36.80449,"longitude":175.06119},{"latitude":-36.803140000000006,"longitude":175.06057},{"latitude":-36.80261,"longitude":175.06026000000003},{"latitude":-36.80216,"longitude":175.05936000000003},{"latitude":-36.80144,"longitude":175.05841},{"latitude":-36.800940000000004,"longitude":175.05803},{"latitude":-36.79952,"longitude":175.05691000000002},{"latitude":-36.79871,"longitude":175.05684000000002},{"latitude":-36.798390000000005,"longitude":175.05677},{"latitude":-36.797900000000006,"longitude":175.05640000000002},{"latitude":-36.79746,"longitude":175.05569000000003},{"latitude":-36.79711,"longitude":175.05527},{"latitude":-36.796940000000006,"longitude":175.05491},{"latitude":-36.796440000000004,"longitude":175.05366},{"latitude":-36.79567,"longitude":175.05211000000003},{"latitude":-36.795500000000004,"longitude":175.05146000000002},{"latitude":-36.79572,"longitude":175.05026},{"latitude":-36.79581,"longitude":175.04904000000002},{"latitude":-36.796020000000006,"longitude":175.04777},{"latitude":-36.79552,"longitude":175.04405000000003},{"latitude":-36.79554,"longitude":175.04376000000002},{"latitude":-36.79621,"longitude":175.0431},{"latitude":-36.797880000000006,"longitude":175.04172000000003},{"latitude":-36.797610000000006,"longitude":175.03935},{"latitude":-36.79833,"longitude":175.03727},{"latitude":-36.79887,"longitude":175.03585},{"latitude":-36.79901,"longitude":175.03525000000002},{"latitude":-36.79876,"longitude":175.03408000000002},{"latitude":-36.79845,"longitude":175.03338000000002},{"latitude":-36.797940000000004,"longitude":175.0329},{"latitude":-36.79677,"longitude":175.03245},{"latitude":-36.7965,"longitude":175.03206},{"latitude":-36.79639,"longitude":175.03202000000002},{"latitude":-36.796220000000005,"longitude":175.03198},{"latitude":-36.79563,"longitude":175.03128},{"latitude":-36.7954,"longitude":175.03044000000003},{"latitude":-36.79491,"longitude":175.02913},{"latitude":-36.79524,"longitude":175.02747000000002},{"latitude":-36.795280000000005,"longitude":175.0268},{"latitude":-36.794850000000004,"longitude":175.02643},{"latitude":-36.794320000000006,"longitude":175.02627},{"latitude":-36.79381,"longitude":175.02625},{"latitude":-36.79309000000001,"longitude":175.02575000000002},{"latitude":-36.792860000000005,"longitude":175.02558000000002},{"latitude":-36.7929,"longitude":175.02535},{"latitude":-36.793000000000006,"longitude":175.02488000000002},{"latitude":-36.79307,"longitude":175.02452000000002},{"latitude":-36.79294,"longitude":175.02411},{"latitude":-36.79224,"longitude":175.02294},{"latitude":-36.790510000000005,"longitude":175.02358},{"latitude":-36.78983,"longitude":175.02366},{"latitude":-36.789370000000005,"longitude":175.02355},{"latitude":-36.78867,"longitude":175.02300000000002},{"latitude":-36.788050000000005,"longitude":175.02312},{"latitude":-36.787310000000005,"longitude":175.02201000000002},{"latitude":-36.7866,"longitude":175.02123},{"latitude":-36.78548,"longitude":175.01899},{"latitude":-36.78529,"longitude":175.01787000000002},{"latitude":-36.78477,"longitude":175.01642},{"latitude":-36.78479,"longitude":175.01419},{"latitude":-36.784710000000004,"longitude":175.01353},{"latitude":-36.78372,"longitude":175.01175},{"latitude":-36.78332,"longitude":175.01069},{"latitude":-36.782900000000005,"longitude":175.01003},{"latitude":-36.78193,"longitude":175.00897},{"latitude":-36.781940000000006,"longitude":175.00895000000003},{"latitude":-36.78195,"longitude":175.00891000000001},{"latitude":-36.78192,"longitude":175.00884000000002},{"latitude":-36.781850000000006,"longitude":175.00883000000002},{"latitude":-36.781670000000005,"longitude":175.00858000000002},{"latitude":-36.781650000000006,"longitude":175.00773},{"latitude":-36.78143,"longitude":175.00696000000002},{"latitude":-36.78143,"longitude":175.00655},{"latitude":-36.78166,"longitude":175.00622},{"latitude":-36.78291,"longitude":175.00537000000003},{"latitude":-36.783570000000005,"longitude":175.00475},{"latitude":-36.783820000000006,"longitude":175.00449},{"latitude":-36.78445,"longitude":175.00344},{"latitude":-36.78524,"longitude":175.0029},{"latitude":-36.78605,"longitude":175.00249000000002},{"latitude":-36.78656,"longitude":175.00263},{"latitude":-36.78679,"longitude":175.00253},{"latitude":-36.78737,"longitude":175.00137},{"latitude":-36.78759,"longitude":175.00129},{"latitude":-36.788270000000004,"longitude":175.00184000000002},{"latitude":-36.788880000000006,"longitude":175.00242},{"latitude":-36.78947,"longitude":175.00281},{"latitude":-36.790000000000006,"longitude":175.00289},{"latitude":-36.79026,"longitude":175.00269},{"latitude":-36.790310000000005,"longitude":175.00245},{"latitude":-36.790060000000004,"longitude":175.00159000000002},{"latitude":-36.790440000000004,"longitude":174.99984},{"latitude":-36.79092,"longitude":174.99886},{"latitude":-36.79102,"longitude":174.99848},{"latitude":-36.791140000000006,"longitude":174.99858},{"latitude":-36.7918,"longitude":174.99929},{"latitude":-36.79193,"longitude":174.99949},{"latitude":-36.79198,"longitude":174.99972000000002},{"latitude":-36.79247,"longitude":174.99988000000002},{"latitude":-36.7927,"longitude":175.00011},{"latitude":-36.79258,"longitude":174.99990000000003},{"latitude":-36.792210000000004,"longitude":174.99986},{"latitude":-36.79195,"longitude":174.99964000000003},{"latitude":-36.79187,"longitude":174.99938},{"latitude":-36.79102,"longitude":174.99848},{"latitude":-36.790560000000006,"longitude":174.99949},{"latitude":-36.7903,"longitude":175.0004},{"latitude":-36.790060000000004,"longitude":175.00145},{"latitude":-36.790110000000006,"longitude":175.00178000000002},{"latitude":-36.7903,"longitude":175.00256000000002},{"latitude":-36.79019,"longitude":175.00279},{"latitude":-36.7899,"longitude":175.00289},{"latitude":-36.789260000000006,"longitude":175.00272},{"latitude":-36.787980000000005,"longitude":175.00154},{"latitude":-36.787620000000004,"longitude":175.00130000000001},{"latitude":-36.78741,"longitude":175.00133000000002},{"latitude":-36.786660000000005,"longitude":175.00261},{"latitude":-36.78647,"longitude":175.00262},{"latitude":-36.78582,"longitude":175.00252},{"latitude":-36.7849,"longitude":175.00322000000003},{"latitude":-36.784310000000005,"longitude":175.00361},{"latitude":-36.78403,"longitude":175.00427000000002},{"latitude":-36.78372,"longitude":175.00462000000002},{"latitude":-36.78349,"longitude":175.00481000000002},{"latitude":-36.78248,"longitude":175.00563000000002},{"latitude":-36.78237,"longitude":175.00544000000002},{"latitude":-36.78253,"longitude":175.00486},{"latitude":-36.782300000000006,"longitude":175.00317},{"latitude":-36.78219,"longitude":175.00194000000002},{"latitude":-36.781890000000004,"longitude":175.00101},{"latitude":-36.781510000000004,"longitude":174.99954000000002},{"latitude":-36.78141,"longitude":174.99885},{"latitude":-36.781490000000005,"longitude":174.99807},{"latitude":-36.781600000000005,"longitude":174.99623000000003},{"latitude":-36.781400000000005,"longitude":174.99570000000003},{"latitude":-36.78119,"longitude":174.99545},{"latitude":-36.78088,"longitude":174.99496000000002},{"latitude":-36.780840000000005,"longitude":174.99382000000003},{"latitude":-36.7807,"longitude":174.99325000000002},{"latitude":-36.780390000000004,"longitude":174.99272000000002},{"latitude":-36.780120000000004,"longitude":174.99216}]
        """.trimIndent()
        val jsonString2 = """
           [{"latitude":-36.780120000000004,"longitude":174.99216},{"latitude":-36.780036,"longitude":174.992037},{"latitude":-36.77995,"longitude":174.99204},{"latitude":-36.77994,"longitude":174.99218000000002},{"latitude":-36.780186,"longitude":174.992396},{"latitude":-36.7806,"longitude":174.99309000000002},{"latitude":-36.78078,"longitude":174.99341},{"latitude":-36.78085,"longitude":174.99462000000003},{"latitude":-36.78096,"longitude":174.99515000000002},{"latitude":-36.78132,"longitude":174.99560000000002},{"latitude":-36.78154,"longitude":174.99597000000003},{"latitude":-36.781620000000004,"longitude":174.9966},{"latitude":-36.781420000000004,"longitude":174.99847000000003},{"latitude":-36.78161,"longitude":175.00001},{"latitude":-36.782120000000006,"longitude":175.00159000000002},{"latitude":-36.7824,"longitude":175.00398},{"latitude":-36.782500000000006,"longitude":175.00510000000003}, {"latitude":-36.78226,"longitude":175.00563000000002}, {"latitude":-36.782124,"longitude":175.005806}, {"latitude":-36.782138,"longitude":175.005809},{"latitude":-36.782133,"longitude":175.005852},{"latitude":-36.78217,"longitude":175.005806},{"latitude":-36.782245,"longitude":175.005822},{"latitude":-36.782308,"longitude":175.005796},{"latitude":-36.782434,"longitude":175.005699},{"latitude":-36.782495,"longitude":175.005623},{"latitude":-36.782561,"longitude":175.005602},{"latitude":-36.783161,"longitude":175.005215},{"latitude":-36.783491,"longitude":175.004834},{"latitude":-36.783538,"longitude":175.004797},{"latitude":-36.783652,"longitude":175.004759},{"latitude":-36.783681,"longitude":175.004761},{"latitude":-36.783682,"longitude":175.004735}, {"latitude":-36.783681,"longitude":175.004761},{"latitude":-36.783652,"longitude":175.004759},{"latitude":-36.783685,"longitude":175.004644},{"latitude":-36.783762,"longitude":175.004555},{"latitude":-36.783852,"longitude":175.004494},{"latitude":-36.784001,"longitude":175.004344},{"latitude":-36.784059,"longitude":175.004247},{"latitude":-36.784069,"longitude":175.004213},{"latitude":-36.784097,"longitude":175.004067},{"latitude":-36.784162,"longitude":175.003905},{"latitude":-36.784268,"longitude":175.003717},{"latitude":-36.78438,"longitude":175.003538},{"latitude":-36.784413,"longitude":175.003493},{"latitude":-36.784502,"longitude":175.003425},{"latitude":-36.784796,"longitude":175.003319},{"latitude":-36.784875,"longitude":175.003272},{"latitude":-36.784915,"longitude":175.003235},{"latitude":-36.785049,"longitude":175.00309},{"latitude":-36.785158,"longitude":175.002988},{"latitude":-36.785194,"longitude":175.002963},{"latitude":-36.785404,"longitude":175.00285},{"latitude":-36.785417,"longitude":175.002842},{"latitude":-36.785472,"longitude":175.002807},{"latitude":-36.785507,"longitude":175.002783},{"latitude":-36.78582,"longitude":175.002546},{"latitude":-36.785883,"longitude":175.00251},{"latitude":-36.785943,"longitude":175.002502},{"latitude":-36.786064,"longitude":175.002518},{"latitude":-36.786316,"longitude":175.002596},{"latitude":-36.786555,"longitude":175.002647},{"latitude":-36.786679,"longitude":175.002608},{"latitude":-36.78674,"longitude":175.002569},{"latitude":-36.786811,"longitude":175.002513},{"latitude":-36.786883,"longitude":175.002418},{"latitude":-36.78729,"longitude":175.001494},{"latitude":-36.787354,"longitude":175.001389},{"latitude":-36.787454,"longitude":175.001283},{"latitude":-36.787468,"longitude":175.001285},{"latitude":-36.787466,"longitude":175.001305}, {"latitude":-36.787468,"longitude":175.001285},{"latitude":-36.787655,"longitude":175.00131},{"latitude":-36.787799,"longitude":175.001388},{"latitude":-36.787901,"longitude":175.001473},{"latitude":-36.788112,"longitude":175.001675},{"latitude":-36.788555,"longitude":175.002175},{"latitude":-36.789286,"longitude":175.002749},{"latitude":-36.789479,"longitude":175.002839},{"latitude":-36.789716,"longitude":175.002904},{"latitude":-36.789863,"longitude":175.002923},{"latitude":-36.790022,"longitude":175.002916},{"latitude":-36.790098,"longitude":175.002891},{"latitude":-36.790213,"longitude":175.002808},{"latitude":-36.79028,"longitude":175.002712},{"latitude":-36.79032,"longitude":175.002551},{"latitude":-36.790315,"longitude":175.0024},{"latitude":-36.790272,
           "longitude":175.00222},{"latitude":-36.790105,
           "longitude":175.001761},{"latitude":-36.790075,"longitude":175.001587},{"latitude":-36.790085,"longitude":175.001333},{"latitude":-36.790502,"longitude":174.999661},{"latitude":-36.790909,"longitude":174.998903},{"latitude":-36.790955,"longitude":174.998779},{"latitude":-36.791023,"longitude":174.998496},{"latitude":-36.791148,"longitude":174.998541},{"latitude":-36.791287,"longitude":174.998638},{"latitude":-36.79185,"longitude":174.999227},{"latitude":-36.791888,"longitude":174.999337},{"latitude":-36.791951,"longitude":174.999443},{"latitude":-36.791989,"longitude":174.99967},{"latitude":-36.79216,
           "longitude":174.99981},{"latitude":-36.792403,
           "longitude":174.99987},{"latitude":-36.792566,
           "longitude":174.999861},{"latitude":-36.792601,"longitude":174.999875},{"latitude":-36.792596,"longitude":174.999894}, {"latitude":-36.792601,"longitude":174.999875},{"latitude":-36.792566,"longitude":174.999861},{"latitude":-36.792403,"longitude":174.99987},{"latitude":-36.79216,
           "longitude":174.99981},{"latitude":-36.791989,
           "longitude":174.99967},{"latitude":-36.791951,
           "longitude":174.999443},{"latitude":-36.791888,"longitude":174.999337},{"latitude":-36.79185,"longitude":174.999227},{"latitude":-36.791287,"longitude":174.998638},{"latitude":-36.791148,"longitude":174.998541},{"latitude":-36.791023,"longitude":174.998496},{"latitude":-36.790955,"longitude":174.998779},{"latitude":-36.790909,"longitude":174.998903},{"latitude":-36.790502,"longitude":174.999661},{"latitude":-36.790085,"longitude":175.001333},{"latitude":-36.790075,"longitude":175.001587},{"latitude":-36.790105,"longitude":175.001761},{"latitude":-36.790272,"longitude":175.00222},{"latitude":-36.790315,"longitude":175.0024},{"latitude":-36.79032,
           "longitude":175.002551},{"latitude":-36.79028,
           "longitude":175.002712},{"latitude":-36.790213,"longitude":175.002808},{"latitude":-36.790098,"longitude":175.002891},{"latitude":-36.790022,"longitude":175.002916},{"latitude":-36.789863,"longitude":175.002923},{"latitude":-36.789716,"longitude":175.002904},{"latitude":-36.789479,"longitude":175.002839},{"latitude":-36.789286,"longitude":175.002749},{"latitude":-36.788555,"longitude":175.002175},{"latitude":-36.788112,"longitude":175.001675},{"latitude":-36.787901,"longitude":175.001473},{"latitude":-36.787799,"longitude":175.001388},{"latitude":-36.787655,"longitude":175.00131},{"latitude":-36.787468,"longitude":175.001285},{"latitude":-36.787466,"longitude":175.001305}, {"latitude":-36.787468,"longitude":175.001285},{"latitude":-36.787454,"longitude":175.001283},{"latitude":-36.787354,"longitude":175.001389},{"latitude":-36.78729,"longitude":175.001494},{"latitude":-36.786883,"longitude":175.002418},{"latitude":-36.786811,"longitude":175.002513},{"latitude":-36.78674,"longitude":175.002569},{"latitude":-36.786679,"longitude":175.002608},{"latitude":-36.786555,"longitude":175.002647},{"latitude":-36.786316,"longitude":175.002596},{"latitude":-36.786064,"longitude":175.002518},{"latitude":-36.785943,"longitude":175.002502},{"latitude":-36.785883,"longitude":175.00251},{"latitude":-36.78582,
           "longitude":175.002546},{"latitude":-36.785507,"longitude":175.002783},{"latitude":-36.785472,"longitude":175.002807},{"latitude":-36.785417,"longitude":175.002842},{"latitude":-36.785404,"longitude":175.00285},{"latitude":-36.785194,"longitude":175.002963},{"latitude":-36.785158,"longitude":175.002988},{"latitude":-36.785049,"longitude":175.00309},{"latitude":-36.784915,"longitude":175.003235},{"latitude":-36.784875,"longitude":175.003272},{"latitude":-36.784796,"longitude":175.003319},{"latitude":-36.784502,"longitude":175.003425},{"latitude":-36.784413,"longitude":175.003493},{"latitude":-36.78438,"longitude":175.003538},{"latitude":-36.784268,"longitude":175.003717},{"latitude":-36.784162,"longitude":175.003905},{"latitude":-36.784097,"longitude":175.004067},{"latitude":-36.784069,"longitude":175.004213},{"latitude":-36.784059,"longitude":175.004247},{"latitude":-36.784001,"longitude":175.004344},{"latitude":-36.783852,"longitude":175.004494},{"latitude":-36.783762,"longitude":175.004555},{"latitude":-36.783685,"longitude":175.004644},{"latitude":-36.783652,"longitude":175.004759},{"latitude":-36.783681,"longitude":175.004761},{"latitude":-36.783682,"longitude":175.004735},{"latitude":-36.783681,"longitude":175.004761},{"latitude":-36.783652,"longitude":175.004759},{"latitude":-36.783538,"longitude":175.004797},{"latitude":-36.783491,"longitude":175.004834},{"latitude":-36.783161,"longitude":175.005215},{"latitude":-36.782561,"longitude":175.005602},{"latitude":-36.782495,"longitude":175.005623},{"latitude":-36.782429,"longitude":175.005601},{"latitude":-36.782348,"longitude":175.005519},{"latitude":-36.782354,"longitude":175.005504},{"latitude":-36.782338,"longitude":175.005493},{"latitude":-36.782354,"longitude":175.005504},{"latitude":-36.782348,"longitude":175.005519},{"latitude":-36.78217,"longitude":175.005806},{"latitude":-36.782133,"longitude":175.005852},{"latitude":-36.781954,"longitude":175.00603},{"latitude":-36.781576,"longitude":175.006324},{"latitude":-36.781482,"longitude":175.006474},{"latitude":-36.78146,"longitude":175.006453},{"latitude":-36.781482,"longitude":175.006474},{"latitude":-36.781465,"longitude":175.006501},{"latitude":-36.781425,"longitude":175.006664},{"latitude":-36.78142,"longitude":175.006844},{"latitude":-36.781449,"longitude":175.006984},{"latitude":-36.78156,"longitude":175.007231},{"latitude":-36.781635,"longitude":175.007385},{"latitude":-36.781658,"longitude":175.007492},{"latitude":-36.781636,"longitude":175.007499},{"latitude":-36.781658,"longitude":175.007492},{"latitude":-36.78166,"longitude":175.007503},{"latitude":-36.78166,"longitude":175.00767},{"latitude":-36.781645,"longitude":175.008414},{"latitude":-36.781609,"longitude":175.008413},{"latitude":-36.781645,"longitude":175.008414},{"latitude":-36.781644,"longitude":175.008441},{"latitude":-36.781635,"longitude":175.008656},{"latitude":-36.781676,"longitude":175.008772},{"latitude":-36.781728,"longitude":175.008852},{"latitude":-36.781822,"longitude":175.008919},{"latitude":-36.781826,"longitude":175.00894},{"latitude":-36.781837,"longitude":175.008962},{"latitude":-36.781832,"longitude":175.008973},{"latitude":-36.781829,"longitude":175.008971},{"latitude":-36.781832,"longitude":175.008973},{"latitude":-36.781837,"longitude":175.008962},{"latitude":-36.781889,"longitude":175.008987},{"latitude":-36.781923,"longitude":175.008968},{"latitude":-36.78222,"longitude":175.009298},{"latitude":-36.782556,"longitude":175.009616},{"latitude":-36.782746,"longitude":175.009801},{"latitude":-36.782834,"longitude":175.009931},{"latitude":-36.782836,"longitude":175.009929},{"latitude":-36.782834,"longitude":175.009931},{"latitude":-36.782914,"longitude":175.01005},{"latitude":-36.78314,
           "longitude":175.010397},{"latitude":-36.783385,"longitude":175.010787},{"latitude":-36.783442,"longitude":175.010873},{"latitude":-36.78344,"longitude":175.010894},{"latitude":-36.783429,"longitude":175.010892},{"latitude":-36.78344,"longitude":175.010894},{"latitude":-36.783437,"longitude":175.010939},{"latitude":-36.783487,"longitude":175.01096},{"latitude":-36.783644,"longitude":175.011516},{"latitude":-36.783721,"longitude":175.011755},{"latitude":-36.783795,"longitude":175.011927},{"latitude":-36.784024,"longitude":175.012353},{"latitude":-36.784082,"longitude":175.012459},{"latitude":-36.784071,"longitude":175.012468},{"latitude":-36.784082,"longitude":175.012459},{"latitude":-36.784109,"longitude":175.012508},{"latitude":-36.78433,"longitude":175.012953},{"latitude":-36.78455,"longitude":175.013314},{"latitude":-36.784692,"longitude":175.013518},{"latitude":-36.784735,"longitude":175.013584},{"latitude":-36.784771,"longitude":175.013681},{"latitude":-36.784772,"longitude":175.013686},{"latitude":-36.784754,"longitude":175.013691},{"latitude":-36.784772,"longitude":175.013686},{"latitude":-36.784792,"longitude":175.013796},{"latitude":-36.784805,"longitude":175.013936},{"latitude":-36.784797,"longitude":175.014236},{"latitude":-36.784788,"longitude":175.014427},{"latitude":-36.784783,"longitude":175.014523},{"latitude":-36.784755,"longitude":175.014967},{"latitude":-36.784729,"longitude":175.015361},{"latitude":-36.784715,"longitude":175.015658},{"latitude":-36.784724,"longitude":175.016086},{"latitude":-36.784773,"longitude":175.016472},{"latitude":-36.784882,"longitude":175.016924},{"latitude":-36.785076,"longitude":175.017346},{"latitude":-36.785270,"longitude":175.017803},{"latitude":-36.785335,"longitude":175.018168},{"latitude":-36.785345,"longitude":175.018565},{"latitude":-36.785410000000006,"longitude":175.01883},{"latitude":-36.78587,"longitude":175.0197},{"latitude":-36.78678,"longitude":175.02146000000002},{"latitude":-36.78719,"longitude":175.02182000000002},{"latitude":-36.787760000000006,"longitude":175.02273000000002},{"latitude":-36.78804,"longitude":175.02312},{"latitude":-36.78851,"longitude":175.02303},{"latitude":-36.789060000000006,"longitude":175.0233},{"latitude":-36.789280000000005,"longitude":175.02348},{"latitude":-36.78967,"longitude":175.02364},{"latitude":-36.790400000000005,"longitude":175.02359},{"latitude":-36.79168,"longitude":175.02315000000002},{"latitude":-36.79234,"longitude":175.02298000000002},{"latitude":-36.79307,"longitude":175.02445},{"latitude":-36.792950000000005,"longitude":175.0251},{"latitude":-36.792860000000005,"longitude":175.02558000000002},{"latitude":-36.79361,"longitude":175.02614000000003},{"latitude":-36.793980000000005,"longitude":175.02629000000002},{"latitude":-36.79473,"longitude":175.02637000000001},{"latitude":-36.7952,"longitude":175.02664000000001},{"latitude":-36.79529,"longitude":175.02727000000002},{"latitude":-36.79493,"longitude":175.02888000000002},{"latitude":-36.79496,"longitude":175.02946},{"latitude":-36.795570000000005,"longitude":175.03118},{"latitude":-36.79623,"longitude":175.03201},{"latitude":-36.796380000000006,"longitude":175.03221000000002},{"latitude":-36.79643,"longitude":175.03224},{"latitude":-36.79677,"longitude":175.03245},{"latitude":-36.797940000000004,"longitude":175.0329},{"latitude":-36.79845,"longitude":175.03338000000002},{"latitude":-36.79876,"longitude":175.03408000000002},{"latitude":-36.79901,"longitude":175.03525000000002},{"latitude":-36.79887,"longitude":175.03585},{"latitude":-36.79833,"longitude":175.03727},{"latitude":-36.797610000000006,"longitude":175.03935},{"latitude":-36.797880000000006,"longitude":175.04172000000003},{"latitude":-36.79621,"longitude":175.0431},{"latitude":-36.79554,"longitude":175.04376000000002},{"latitude":-36.79552,"longitude":175.04405000000003},{"latitude":-36.796020000000006,"longitude":175.04777},{"latitude":-36.79581,"longitude":175.04904000000002},{"latitude":-36.79572,"longitude":175.05026},{"latitude":-36.795500000000004,"longitude":175.05131},{"latitude":-36.79553000000001,"longitude":175.05172000000002},{"latitude":-36.79567,"longitude":175.05211000000003},{"latitude":-36.796440000000004,"longitude":175.05366},{"latitude":-36.79704,"longitude":175.05516},{"latitude":-36.79746,"longitude":175.05569000000003},{"latitude":-36.797900000000006,"longitude":175.05640000000002},{"latitude":-36.798390000000005,"longitude":175.05677},{"latitude":-36.79871,"longitude":175.05684000000002},{"latitude":-36.79952,"longitude":175.05691000000002},{"latitude":-36.800940000000004,"longitude":175.05803},{"latitude":-36.80144,"longitude":175.05841},{"latitude":-36.80216,"longitude":175.05936000000003},{"latitude":-36.80261,"longitude":175.06026000000003},{"latitude":-36.802640000000004,"longitude":175.06078000000002},{"latitude":-36.80238000000001,"longitude":175.06136},{"latitude":-36.801570000000005,"longitude":175.06209},{"latitude":-36.80124,"longitude":175.06264000000002},{"latitude":-36.80143,"longitude":175.06561000000002},{"latitude":-36.801550000000006,"longitude":175.06758000000002},{"latitude":-36.80113,"longitude":175.06958},{"latitude":-36.80046,"longitude":175.07219},{"latitude":-36.799820000000004,"longitude":175.07363},{"latitude":-36.797380000000004,"longitude":175.07801},{"latitude":-36.7971,"longitude":175.07816000000003},{"latitude":-36.79655,"longitude":175.07809},{"latitude":-36.79621,"longitude":175.07788000000002},{"latitude":-36.79549,"longitude":175.07728},{"latitude":-36.79437,"longitude":175.07744000000002},{"latitude":-36.793670000000006,"longitude":175.07768000000002},{"latitude":-36.7924,"longitude":175.07961},{"latitude":-36.79065000000001,"longitude":175.08126000000001},{"latitude":-36.790440000000004,"longitude":175.08136000000002},{"latitude":-36.79023,"longitude":175.08139000000003},{"latitude":-36.790290000000006,"longitude":175.08159},{"latitude":-36.790560000000006,"longitude":175.08192000000003},{"latitude":-36.79092,"longitude":175.08254000000002},{"latitude":-36.79092,"longitude":175.08292},{"latitude":-36.790420000000005,"longitude":175.08294},{"latitude":-36.788970000000006,"longitude":175.08308000000002},{"latitude":-36.7884,"longitude":175.08313},{"latitude":-36.78804,"longitude":175.08317000000002},{"latitude":-36.788030000000006,"longitude":175.08296},{"latitude":-36.787980000000005,"longitude":175.08197},{"latitude":-36.78795,"longitude":175.08121000000003}, {"latitude":-36.787954,"longitude":175.08121},{"latitude":-36.787957,"longitude":175.081251},{"latitude":-36.788201,"longitude":175.081243},{"latitude":-36.7882,"longitude":175.08118000000002},{"latitude":-36.788470000000004,"longitude":175.08114},{"latitude":-36.788740000000004,"longitude":175.08110000000002},{"latitude":-36.78913,"longitude":175.08118000000002},{"latitude":-36.78981,"longitude":175.08139000000003},{"latitude":-36.79048,"longitude":175.08135000000001},{"latitude":-36.79155,"longitude":175.08057000000002},{"latitude":-36.792680000000004,"longitude":175.07917},{"latitude":-36.79374,"longitude":175.07761000000002},{"latitude":-36.79531,"longitude":175.07727000000003},{"latitude":-36.79558,"longitude":175.0773},{"latitude":-36.79628,"longitude":175.07795000000002},{"latitude":-36.796600000000005,"longitude":175.07811},{"latitude":-36.797140000000006,"longitude":175.07815000000002},{"latitude":-36.7974,"longitude":175.07799000000003},{"latitude":-36.79878,"longitude":175.07553000000001},{"latitude":-36.800070000000005,"longitude":175.0732},{"latitude":-36.800740000000005,"longitude":175.07107000000002},{"latitude":-36.80124,"longitude":175.06917},{"latitude":-36.80154,"longitude":175.06785000000002},{"latitude":-36.801460000000006,"longitude":175.06603},{"latitude":-36.80142,"longitude":175.06544000000002},{"latitude":-36.80124,"longitude":175.06306},{"latitude":-36.801350000000006,"longitude":175.06232000000003},{"latitude":-36.80207,"longitude":175.06168000000002},{"latitude":-36.80256,"longitude":175.06111},{"latitude":-36.80265,"longitude":175.06051000000002},{"latitude":-36.80263,"longitude":175.06036},{"latitude":-36.803140000000006,"longitude":175.06057},{"latitude":-36.80449,"longitude":175.06119},{"latitude":-36.80507,"longitude":175.06117},{"latitude":-36.80552,"longitude":175.06076000000002},{"latitude":-36.80599,"longitude":175.06052000000003},{"latitude":-36.80684,"longitude":175.06132000000002},{"latitude":-36.80736,"longitude":175.06183000000001},{"latitude":-36.80792,"longitude":175.062},{"latitude":-36.808620000000005,"longitude":175.06211000000002},{"latitude":-36.808960000000006,"longitude":175.06199},{"latitude":-36.809070000000006,"longitude":175.0618},{"latitude":-36.80946,"longitude":175.06217},{"latitude":-36.80979,"longitude":175.06314},{"latitude":-36.810480000000005,"longitude":175.06426000000002},{"latitude":-36.810660000000006,"longitude":175.06491000000003},{"latitude":-36.810810000000004,"longitude":175.06638},{"latitude":-36.8111,"longitude":175.06793000000002},{"latitude":-36.811580000000006,"longitude":175.06910000000002},{"latitude":-36.811930000000004,"longitude":175.07010000000002},{"latitude":-36.81253,"longitude":175.07070000000002},{"latitude":-36.813250000000004,"longitude":175.07199000000003},{"latitude":-36.81481,"longitude":175.07362},{"latitude":-36.81561,"longitude":175.07510000000002},{"latitude":-36.815850000000005,"longitude":175.07616000000002},{"latitude":-36.81589,"longitude":175.07676},{"latitude":-36.81613,"longitude":175.07814000000002},{"latitude":-36.81618,"longitude":175.07858000000002},{"latitude":-36.815920000000006,"longitude":175.08016},{"latitude":-36.81582,"longitude":175.08089},{"latitude":-36.815490000000004,"longitude":175.08162000000002},{"latitude":-36.81486,"longitude":175.08235000000002},{"latitude":-36.814550000000004,"longitude":175.08243000000002},{"latitude":-36.81486,"longitude":175.08235000000002},{"latitude":-36.815490000000004,"longitude":175.08162000000002},{"latitude":-36.81582,"longitude":175.08089},{"latitude":-36.815920000000006,"longitude":175.08016},{"latitude":-36.81618,"longitude":175.07858000000002},{"latitude":-36.81613,"longitude":175.07814000000002},{"latitude":-36.81589,"longitude":175.07676},{"latitude":-36.815850000000005,"longitude":175.07616000000002},{"latitude":-36.81561,"longitude":175.07510000000002},{"latitude":-36.81481,"longitude":175.07362},{"latitude":-36.813250000000004,"longitude":175.07199000000003},{"latitude":-36.81253,"longitude":175.07070000000002},{"latitude":-36.811930000000004,"longitude":175.07010000000002},{"latitude":-36.811580000000006,"longitude":175.06910000000002},{"latitude":-36.8111,"longitude":175.06793000000002},{"latitude":-36.810810000000004,"longitude":175.06638},{"latitude":-36.810660000000006,"longitude":175.06491000000003},{"latitude":-36.810480000000005,"longitude":175.06426000000002},{"latitude":-36.80979,"longitude":175.06314},{"latitude":-36.80946,"longitude":175.06217},{"latitude":-36.809070000000006,"longitude":175.0618},{"latitude":-36.808960000000006,"longitude":175.06199},{"latitude":-36.808620000000005,"longitude":175.06211000000002},{"latitude":-36.80792,"longitude":175.062},{"latitude":-36.80736,"longitude":175.06183000000001},{"latitude":-36.80684,"longitude":175.06132000000002},{"latitude":-36.80599,"longitude":175.06052000000003},{"latitude":-36.80552,"longitude":175.06076000000002},{"latitude":-36.80507,"longitude":175.06117},{"latitude":-36.80449,"longitude":175.06119},{"latitude":-36.803140000000006,"longitude":175.06057},{"latitude":-36.80261,"longitude":175.06026000000003},{"latitude":-36.80216,"longitude":175.05936000000003},{"latitude":-36.80144,"longitude":175.05841},{"latitude":-36.800940000000004,"longitude":175.05803},{"latitude":-36.79952,"longitude":175.05691000000002},{"latitude":-36.79871,"longitude":175.05684000000002},{"latitude":-36.798390000000005,"longitude":175.05677},{"latitude":-36.797900000000006,"longitude":175.05640000000002},{"latitude":-36.79746,"longitude":175.05569000000003},{"latitude":-36.79711,"longitude":175.05527},{"latitude":-36.796940000000006,"longitude":175.05491},{"latitude":-36.796440000000004,"longitude":175.05366},{"latitude":-36.79567,"longitude":175.05211000000003},{"latitude":-36.795500000000004,"longitude":175.05146000000002},{"latitude":-36.79572,"longitude":175.05026},{"latitude":-36.79581,"longitude":175.04904000000002},{"latitude":-36.796020000000006,"longitude":175.04777},{"latitude":-36.79552,"longitude":175.04405000000003},{"latitude":-36.79554,"longitude":175.04376000000002},{"latitude":-36.79621,"longitude":175.0431},{"latitude":-36.797880000000006,"longitude":175.04172000000003},{"latitude":-36.797610000000006,"longitude":175.03935},{"latitude":-36.79833,"longitude":175.03727},{"latitude":-36.79887,"longitude":175.03585},{"latitude":-36.79901,"longitude":175.03525000000002},{"latitude":-36.79876,"longitude":175.03408000000002},{"latitude":-36.79845,"longitude":175.03338000000002},{"latitude":-36.797940000000004,"longitude":175.0329},{"latitude":-36.79677,"longitude":175.03245},{"latitude":-36.7965,"longitude":175.03206},{"latitude":-36.79639,"longitude":175.03202000000002},{"latitude":-36.796220000000005,"longitude":175.03198},{"latitude":-36.79563,"longitude":175.03128},{"latitude":-36.7954,"longitude":175.03044000000003},{"latitude":-36.79491,"longitude":175.02913},{"latitude":-36.79524,"longitude":175.02747000000002},{"latitude":-36.795280000000005,"longitude":175.0268},{"latitude":-36.794850000000004,"longitude":175.02643},{"latitude":-36.794320000000006,"longitude":175.02627},{"latitude":-36.79381,"longitude":175.02625},{"latitude":-36.79309000000001,"longitude":175.02575000000002},{"latitude":-36.792860000000005,"longitude":175.02558000000002},{"latitude":-36.7929,"longitude":175.02535},{"latitude":-36.793000000000006,"longitude":175.02488000000002},{"latitude":-36.79307,"longitude":175.02452000000002},{"latitude":-36.79294,"longitude":175.02411},{"latitude":-36.79224,"longitude":175.02294},{"latitude":-36.790510000000005,"longitude":175.02358},{"latitude":-36.78983,"longitude":175.02366},{"latitude":-36.789370000000005,"longitude":175.02355},{"latitude":-36.78867,"longitude":175.02300000000002},{"latitude":-36.788050000000005,"longitude":175.02312},{"latitude":-36.787310000000005,"longitude":175.02201000000002},{"latitude":-36.7866,"longitude":175.02123},{"latitude":-36.78548,"longitude":175.01899},{"latitude":-36.78529,"longitude":175.01787000000002},{"latitude":-36.78477,"longitude":175.01642},{"latitude":-36.78479,"longitude":175.01419},{"latitude":-36.784710000000004,"longitude":175.01353},{"latitude":-36.78372,"longitude":175.01175},{"latitude":-36.78332,"longitude":175.01069},{"latitude":-36.782900000000005,"longitude":175.01003},{"latitude":-36.78193,"longitude":175.00897},{"latitude":-36.781940000000006,"longitude":175.00895000000003},{"latitude":-36.78195,"longitude":175.00891000000001},{"latitude":-36.78192,"longitude":175.00884000000002},{"latitude":-36.781850000000006,"longitude":175.00883000000002},{"latitude":-36.781670000000005,"longitude":175.00858000000002},{"latitude":-36.781650000000006,"longitude":175.00773},{"latitude":-36.78143,"longitude":175.00696000000002},{"latitude":-36.78143,"longitude":175.00655},{"latitude":-36.78166,"longitude":175.00622},{"latitude":-36.78237,"longitude":175.00544000000002},{"latitude":-36.78253,"longitude":175.00486},{"latitude":-36.782300000000006,"longitude":175.00317},{"latitude":-36.78219,"longitude":175.00194000000002},{"latitude":-36.781890000000004,"longitude":175.00101},{"latitude":-36.781510000000004,"longitude":174.99954000000002},{"latitude":-36.78141,"longitude":174.99885},{"latitude":-36.781490000000005,"longitude":174.99807},{"latitude":-36.781600000000005,"longitude":174.99623000000003},{"latitude":-36.781400000000005,"longitude":174.99570000000003},{"latitude":-36.78119,"longitude":174.99545},{"latitude":-36.78088,"longitude":174.99496000000002},{"latitude":-36.780840000000005,"longitude":174.99382000000003},{"latitude":-36.7807,"longitude":174.99325000000002},{"latitude":-36.780390000000004,"longitude":174.99272000000002},{"latitude":-36.780120000000004,"longitude":174.99216}]
        """.trimIndent()
        val jsonArray = JSONArray(jsonString2)

        for (i in 0 until jsonArray.length()) {
            val item = jsonArray.getJSONObject(i)
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
    fun getBusStopOffline(): List<GeoPoint> {
        val geoPoint = mutableListOf<GeoPoint>()
        val jsonString1 = """
        [
            {"latitude":-36.78012,"longitude":174.99216}, // bus start
            {"latitude":-36.78139,"longitude":175.007},   // bus 1
            {"latitude":-36.78338,"longitude":175.01086}, // bus 2
            {"latitude":-36.79883,"longitude":175.03447}, // bus 3
            {"latitude":-36.79589,"longitude":175.04737}, // bus 4
            {"latitude":-36.80141,"longitude":175.06579}, // bus 5
            {"latitude":-36.80106,"longitude":175.06972}, // bus 6
            {"latitude":-36.79887,"longitude":175.07527}, // bus 7
            {"latitude":-36.78842,"longitude":175.08309}, // bus 8
            {"latitude":-36.8011,"longitude":175.06984},  // bus 9
            {"latitude":-36.80149,"longitude":175.06619}, // bus 10
            {"latitude":-36.81456,"longitude":175.08249}, // bus 11
            {"latitude":-36.80916,"longitude":175.06174}, // bus 12
            {"latitude":-36.79601,"longitude":175.04829}, // bus 13
            {"latitude":-36.79689,"longitude":175.03242}, // bus 14
            {"latitude":-36.78365,"longitude":175.01139}, // bus 15
            {"latitude":-36.79159,"longitude":174.99938}, // bus 16
            {"latitude":-36.78724,"longitude":175.00125}, // bus 17
            {"latitude":-36.78012,"longitude":174.99216}  // bus end
        ]
    """.trimIndent()
        val jsonString2 = """
        [
            {"latitude":-36.78012,"longitude":174.99216}, // bus start
            {"latitude":-36.79159,"longitude":174.99938}, // bus 1
            {"latitude":-36.78724,"longitude":175.00125}, // bus 2
            {"latitude":-36.78139,"longitude":175.007},   // bus 3
            {"latitude":-36.78338,"longitude":175.01086}, // bus 4
            {"latitude":-36.79883,"longitude":175.03447}, // bus 5
            {"latitude":-36.79589,"longitude":175.04737}, // bus 6
            {"latitude":-36.80141,"longitude":175.06579}, // bus 7
            {"latitude":-36.79887,"longitude":175.07527}, // bus 8
            {"latitude":-36.78842,"longitude":175.08309}, // bus 9
            {"latitude":-36.8011,"longitude":175.06984},  // bus 10
            {"latitude":-36.80149,"longitude":175.06619}, // bus 11
            {"latitude":-36.81456,"longitude":175.08249}, // bus 12
            {"latitude":-36.79601,"longitude":175.04829}, // bus 13
            {"latitude":-36.79689,"longitude":175.03242}, // bus 14
            {"latitude":-36.78365,"longitude":175.01139}, // bus 15
            {"latitude":-36.78012,"longitude":174.99216}  // bus end
        ]
    """.trimIndent()
        val jsonArray = JSONArray(jsonString2)
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



