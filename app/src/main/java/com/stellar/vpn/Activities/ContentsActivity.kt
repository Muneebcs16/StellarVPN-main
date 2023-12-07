package com.stellar.vpn.Activities

import android.content.Intent
import android.graphics.drawable.Drawable
import android.graphics.drawable.TransitionDrawable
import android.net.TrafficStats
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.text.Html
import android.text.Spanned
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RatingBar
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import com.airbnb.lottie.LottieAnimationView
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.facebook.ads.Ad
import com.facebook.ads.AdError
import com.facebook.ads.AdOptionsView
import com.facebook.ads.AdView
import com.facebook.ads.AudienceNetworkAds
import com.facebook.ads.InterstitialAd
import com.facebook.ads.InterstitialAdListener
import com.facebook.ads.NativeAd
import com.facebook.ads.NativeAdLayout
import com.facebook.ads.NativeAdListener
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.OnUserEarnedRewardListener
import com.google.android.gms.ads.formats.NativeAdOptions
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.stellar.vpn.Config
import com.stellar.vpn.R
import com.stellar.vpn.Utils.LocalFormatter
import com.stellar.vpn.speed.Speed
import com.stellar.vpn.ui.BaseDrawerActivity
import es.dmoral.toasty.Toasty
import java.net.Inet4Address
import java.net.NetworkInterface


abstract class ContentsActivity : BaseDrawerActivity() {
    private var STATUS: String? = "DISCONNECTED"
    private var mLastRxBytes: Long = 0
    private var mLastTxBytes: Long = 0
    private var mLastTime: Long = 0
    private var mSpeed: Speed? = null

    var lottieAnimationView: LottieAnimationView? = null
    var progressBar: ProgressBar? = null
    var vpnToastCheck = true
    var handlerTraffic: Handler? = null
    private val adCount = 0


    var progressBarValue = 0
    var handler = Handler(Looper.getMainLooper())
    private val customHandler = Handler(Looper.getMainLooper())
    private var startTime = 0L
    var timeInMilliseconds = 0L
    var timeSwapBuff = 0L
    var updatedTime = 0L


    var tvIpAddress: TextView? = null
    var textDownloading: TextView? = null
    var textUploading: TextView? = null
    var tvUploadUnit: TextView? = null
    var tvDownloadUnit: TextView? = null
    var ivVpnDetail: LinearLayout? = null
    var timerTextView: TextView? = null
    var btnConnect: LinearLayout? = null
    var btnPower: ImageView? = null
    var vpnStatusImage: ImageView? = null
    var connectionState: TextView? = null
//    var btnPower2: ImageView? = null
    var connectionStateOff: TextView? = null
//    var connectionStateOn: TextView? = null
    var bglayout: LinearLayout? = null
    var btnbg3: View? = null
    var btnbg2: View? = null

    var bg_changed: Boolean? = false

    var frameLayout: RelativeLayout? = null
    private var mInterstitialAdMob: com.google.android.gms.ads.interstitial.InterstitialAd? = null
    private var rewardedAd:RewardedAd? =null
    private var loadingAd: Boolean? = false

    @JvmField
    var imgFlag: ImageView? = null

    @JvmField
    var flagName: TextView? = null

    var facebookAdView: AdView? = null
    private var nativeAd: NativeAd? = null

    @JvmField
    var mInterstitialAd: InterstitialAd? = null

    @JvmField
    var facebookInterstitialAd: InterstitialAd? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        textDownloading = findViewById(R.id.downloading)
        textUploading = findViewById(R.id.uploading)
        tvUploadUnit = findViewById(R.id.tvUploadUnit)
        tvDownloadUnit = findViewById(R.id.tvDownloadUnit)
        ivVpnDetail = findViewById(R.id.vpn_details)
        timerTextView = findViewById(R.id.tv_timer)
        vpnStatusImage = findViewById(R.id.vpnStatusImage)
        connectionState = findViewById(R.id.connectionState)
        btnConnect = findViewById(R.id.btnConnect)
        connectionStateOff = findViewById(R.id.connection_state_off)
//        connectionStateOn = findViewById(R.id.connection_state_on)
        imgFlag = findViewById(R.id.flag_image)
        flagName = findViewById(R.id.flag_name)
        btnPower = findViewById(R.id.connectBtn)
//        btnPower2 = findViewById(R.id.connect_btn2)
        bglayout = findViewById(R.id.bglayout)
        btnbg2 = findViewById(R.id.view2)
        btnbg3 = findViewById(R.id.view3)
        frameLayout = findViewById(R.id.fl_adplaceholder)

        btnConnect?.setOnClickListener {
            onConnectBtnClick()
        }

        findViewById<View>(R.id.ic_crown).setOnClickListener {
            showServerList()
        }
        tvIpAddress = findViewById(R.id.tv_ip_address)
        showIP()

        lottieAnimationView = findViewById(R.id.animation_view)
        progressBar = findViewById(R.id.progressBar)

        ivVpnDetail?.setOnClickListener {
            showServerList()
        }

        if (canShowAd) {
            if (MainActivity.type == "ad") {
                MobileAds.initialize(
                    this
                ) { initializationStatus ->
                    val statusMap = initializationStatus.adapterStatusMap
                    for (adapterClass in statusMap.keys) {
                        val status = statusMap[adapterClass]
                        Log.d(
                            "MyApp", String.format(
                                "Adapter name: %s, Description: %s, Latency: %d",
                                adapterClass, status!!.description, status!!.latency
                            )
                        )
                    }


                    val adLoader = AdLoader.Builder(this, MainActivity.admob_native_id)
                        .forNativeAd { nativeAd ->
                            frameLayout!!.visibility = View.VISIBLE
                            val adView = layoutInflater
                                .inflate(R.layout.ad_unified, null) as NativeAdView
                            if (!Config.vip_subscription && !Config.all_subscription) {
                                populateUnifiedNativeAdView(nativeAd, adView)
                                frameLayout!!.removeAllViews()
                                frameLayout!!.addView(adView)
                            }
                        }
                        .withAdListener(object : AdListener() {
                            override fun onAdFailedToLoad(adError: LoadAdError) {
                                // Handle the failure by logging, altering the UI, and so on.
                            }
                        })
                        .withNativeAdOptions(
                            NativeAdOptions.Builder() // Methods in the NativeAdOptions.Builder class can be
                                // used here to specify individual options settings.
                                .build()
                        )
                        .build()

                    adLoader.loadAd(
                        AdRequest.Builder()
                            .build()
                    )


                }

            }

        }


//        handlerTraffic = Handler()
//        handleTrafficData()

        loadAdAgain()

    }

    private fun showIP() {
        val queue = Volley.newRequestQueue(this)
        val urlip = "https://checkip.amazonaws.com/"

        val stringRequest =
            StringRequest(Request.Method.GET, urlip, { response ->

                val result = response.replace("\n", "")
                tvIpAddress?.setText(result)
            })
            { e ->
                run {
                    Log.d("IP ERROR: ", e.message.toString())
                    tvIpAddress?.text = getIpv4HostAddress()
                }
            }
        queue.add(stringRequest)
    }

    override fun onStart() {
        super.onStart()

    }

    override fun onResume() {
        super.onResume()

    }

    override fun onPause() {
        super.onPause()

    }

    override fun onDestroy() {
        if (nativeAd != null) {
            nativeAd!!.destroy()
        }
        super.onDestroy()
    }

    private fun loadAdAgain() {
        if (MainActivity.type == "ad") {

            if (loadingAd == false) {

                loadingAd = true
                var adRequest = AdRequest.Builder().build()
                RewardedAd.load(this,MainActivity.will_dev_33223327_admob_reward, adRequest, object : RewardedAdLoadCallback() {
                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        rewardedAd = null
                        loadingAd = false
                    }

                    override fun onAdLoaded(ad: RewardedAd) {
                        Log.d(TAG, "Ad was loaded.")
                        rewardedAd = ad
                        loadingAd = false
                    }
                })
//                val adRequest = AdRequest.Builder().build()
//
//                RewardedAd.load(this@ContentsActivity,
//                    MainActivity.will_dev_33223327_admob_reward,
//                    adRequest,
//                    object : RewardedAd() {
//                        override fun onAdLoaded(interstitialAd: com.google.android.gms.ads.interstitial.InterstitialAd) {
//                            // The mInterstitialAd reference will be null until
//                            // an ad is loaded.
//                            mInterstitialAdMob = interstitialAd
//                            Log.i("INTERSTITIAL", "onAdLoaded")
//                            loadingAd = false
//
//
//                        }
//
//                        override fun onAdFailedToLoad(loadAdError: LoadAdError) {
//                            // Handle the error
//                            Log.i("INTERSTITIAL", loadAdError.message)
//                            loadingAd = false
//                            mInterstitialAdMob = null
//                        }
//                    })


            }

        }

    }

    private val mUIHandler = Handler(Looper.getMainLooper())
    val mUIUpdateRunnable: Runnable = object : Runnable {
        override fun run() {
            // updateUI()
            checkRemainingTraffic()
            mUIHandler.postDelayed(this, 10000)
        }
    }


    private fun onConnectBtnClick() {
        if (STATUS != "DISCONNECTED") {
            disconnectAlert()
        } else {
            if (!Utility.isOnline(applicationContext)) {
                showMessage("No Internet Connection")
            } else {
                checkSelectedCountry()
            }
        }

    }


    protected abstract fun checkSelectedCountry()
    protected abstract fun prepareVpn()
    protected abstract fun disconnectFromVpn()


    open fun updateConnectionStatus(
        duration: String?,
        lastPacketReceive: String?,
        byteIn: String,
        byteOut: String
    ) {
        val byteinKb = byteIn.split("-").toTypedArray()[1]
        val byteoutKb = byteOut.split("-").toTypedArray()[1]

        setTrafficData()
//        textDownloading!!.text = byteinKb.replace("kB/s","")
//        textUploading!!.text = byteoutKb.replace("kB/s","")
        // timerTextView!!.text = duration
    }


    protected abstract fun checkRemainingTraffic()

    protected fun updateUI(status: String) {


        when (status) {
            "WAIT" -> {
                STATUS = "WAITING"
                loadIcon(status)
                connectionOff()
                connectionStateOff!!.visibility = View.VISIBLE
                connectionStateOff!!.setText("Waiting")
//                connectionStateOn!!.visibility = View.GONE
                btnConnect!!.isEnabled = true
//                timerTextView!!.visibility = View.GONE
//                btnPower!!.visibility = View.VISIBLE
//                btnPower2!!.visibility = View.GONE
            }

            "RECONNECTING" -> {
                STATUS = "RECONNECTING"
                loadIcon(status)
                connectionOff()
                connectionStateOff!!.visibility = View.VISIBLE
                connectionStateOff!!.setText("Reconnecting")
//                connectionStateOn!!.visibility = View.GONE
                btnConnect!!.isEnabled = true
//                timerTextView!!.visibility = View.GONE
//                btnPower!!.visibility = View.VISIBLE
//                btnPower2!!.visibility = View.GONE
                showConnectProgress()
            }

            "CONNECTED" -> {
                STATUS = "CONNECTED"
//                textDownloading!!.visibility = View.VISIBLE
                textUploading!!.visibility = View.VISIBLE
                loadIcon(status)
                btnConnect!!.isEnabled = true
                connectionOn()
                connectionStateOff!!.visibility = View.VISIBLE
//                connectionStateOn!!.visibility = View.VISIBLE
//                connectionStateOn!!.text = getString(R.string.connected)
                connectionStateOff!!.text = getString(R.string.connected)
                timer()
                timerTextView!!.visibility = View.VISIBLE
                vpnStatusImage?.setImageDrawable(resources.getDrawable(R.drawable.active_vpn_wave))
                btnPower?.setColorFilter(resources.getColor(R.color.dark_blue))
                connectionState?.text = "Connected"
//                btnPower!!.visibility = View.VISIBLE
//                btnPower2!!.visibility = View.VISIBLE
            }

            "ASSIGN_IP" -> {
                STATUS = "LOAD"
                connectionOff()
                connectionStateOff!!.visibility = View.VISIBLE
                connectionStateOff!!.setText("Assigning IP")
//                connectionStateOn!!.visibility = View.GONE
                loadIcon(status)
                btnConnect!!.isEnabled = true
//                timerTextView!!.visibility = View.GONE
//                btnPower!!.visibility = View.VISIBLE
//                btnPower2!!.visibility = View.GONE
                showConnectProgress()
            }

            "GET_CONFIG" -> {
                STATUS = "LOAD"
                connectionOff()
                connectionStateOff!!.visibility = View.VISIBLE
                connectionStateOff!!.setText("Getting Config")
//                connectionStateOn!!.visibility = View.GONE
                loadIcon(status)
                btnConnect!!.isEnabled = true
//                timerTextView!!.visibility = View.GONE
//                btnPower!!.visibility = View.VISIBLE
//                btnPower2!!.visibility = View.GONE
                showConnectProgress()
            }

            "LOAD" -> {
                STATUS = "LOAD"
                connectionOff()
                connectionStateOff!!.visibility = View.VISIBLE
                // Retrieve the string from resources

                // Retrieve the string from resources
                val helloWorldString = getString(R.string.paused)

                // Parse the HTML-like string using Html.fromHtml (deprecated in API 24+)
                // If you target API 24+, consider using HtmlCompat.fromHtml from androidx.core.text.HtmlCompat

                // Parse the HTML-like string using Html.fromHtml (deprecated in API 24+)
                // If you target API 24+, consider using HtmlCompat.fromHtml from androidx.core.text.HtmlCompat
                val spannedString: Spanned
                spannedString = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Html.fromHtml(helloWorldString, Html.FROM_HTML_MODE_LEGACY)
                } else {
                    Html.fromHtml(helloWorldString)
                }

                // Display the formatted text in a TextView

                // Display the formatted text in a TextView
//    val textView = findViewById<TextView>(R.id.textView)
//    textView.text = spannedString
                connectionStateOff!!.setText(spannedString)
//                connectionStateOn!!.visibility = View.GONE
                loadIcon(status)
                btnConnect!!.isEnabled = true
//                timerTextView!!.visibility = View.GONE
//                btnPower!!.visibility = View.VISIBLE
//                btnPower2!!.visibility = View.GONE
                showConnectProgress()
            }

            "DISCONNECTED" -> {
                STATUS = "DISCONNECTED"
//                textDownloading!!.visibility = View.GONE
//                textUploading!!.visibility = View.GONE
                loadIcon(status)
                btnConnect!!.isEnabled = true
                connectionOff()
                connectionStateOff!!.visibility = View.VISIBLE

                val sp = Html.fromHtml(getString(R.string.disconnected))
                connectionStateOff?.text = sp
//                connectionStateOn!!.visibility = View.GONE
//                timer()
//                handler.removeCallbacksAndMessages(null)
                timerTextView?.isVisible = false

                vpnStatusImage?.setImageDrawable(resources.getDrawable(R.drawable.disconnected_vpn_wave))
                connectionState?.text = "Disconnected"
                // set gray color
                btnPower?.setColorFilter(resources.getColor(R.color.dark_gray))
//                timerTextView!!.visibility = View.GONE
//                btnPower!!.visibility = View.VISIBLE
//                btnPower2!!.visibility = View.GONE

            }

            "USERPAUSE" -> {
                STATUS = "DISCONNECTED"
//                textDownloading!!.visibility = View.GONE
//                textUploading!!.visibility = View.GONE
                loadIcon(status)
                btnConnect!!.isEnabled = true
                connectionOff()
                connectionStateOff!!.visibility = View.VISIBLE
                connectionStateOff!!.setText("User Pause")
//                connectionStateOn!!.visibility = View.GONE
                timer()
//                timerTextView!!.visibility = View.GONE
//                btnPower!!.visibility = View.VISIBLE
//                btnPower2!!.visibility = View.GONE

            }

            "NONETWORK" -> {
                STATUS = "DISCONNECTED"
//                textDownloading!!.visibility = View.GONE
//                textUploading!!.visibility = View.GONE
                loadIcon(status)
                btnConnect!!.isEnabled = true
                connectionOff()
                connectionStateOff!!.visibility = View.VISIBLE
                connectionStateOff!!.setText("Checking Network")
//                connectionStateOn!!.visibility = View.GONE
                timer()
//                timerTextView!!.visibility = View.GONE
//                btnPower!!.visibility = View.VISIBLE
//                btnPower2!!.visibility = View.GONE

            }
        }


    }

    protected fun updateTrafficStats(outBytes: Long, inBytes: Long) {
        val outString = LocalFormatter.easyRead(outBytes, false)
        val inString = LocalFormatter.easyRead(inBytes, false)
    }

    protected fun showConnectProgress() {
        Thread {
            while (STATUS.equals("LOAD") || STATUS.equals("RECONNECTING")) {
                progressBarValue++
                handler.post { }
                try {
                    Thread.sleep(300)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }.start()
    }

    protected fun connectionOff() {

        btnbg2?.setBackgroundResource(R.drawable.connect_btn2)
        btnbg3?.setBackgroundResource(R.drawable.connect_btn3)

        if (bg_changed == true) {
            bg_changed = false
            val backgrounds = arrayOfNulls<Drawable>(2)
            backgrounds[0] = ResourcesCompat.getDrawable(resources, R.drawable.main_bg2, null)
            backgrounds[1] = ResourcesCompat.getDrawable(resources, R.drawable.main_bg, null)

            val crossfade = TransitionDrawable(backgrounds)

            //bglayout?.background = crossfade
            crossfade.startTransition(1000)
        }
    }

    protected fun connectionOn() {

        btnbg2?.setBackgroundResource(R.drawable.connect_btn2_on)
        btnbg3?.setBackgroundResource(R.drawable.connect_btn3_on)

        if (bg_changed == false) {
            bg_changed = true
            val backgrounds = arrayOfNulls<Drawable>(2)
            backgrounds[0] = ResourcesCompat.getDrawable(resources, R.drawable.main_bg, null)
            backgrounds[1] = ResourcesCompat.getDrawable(resources, R.drawable.main_bg2, null)

            val crossfade = TransitionDrawable(backgrounds)

            //   bglayout?.background = crossfade
            crossfade.startTransition(1000)
        }
    }

    protected fun showMessage(msg: String?) {
        Toast.makeText(this@ContentsActivity, msg, Toast.LENGTH_SHORT).show()
    }

    protected fun timer() {
        if (adCount == 0) {
            startTime = SystemClock.uptimeMillis()
            customHandler.postDelayed(updateTimerThread, 0)
            timeSwapBuff += timeInMilliseconds
        }
    }

    private val updateTimerThread: Runnable = object : Runnable {
        override fun run() {
            timeInMilliseconds = SystemClock.uptimeMillis() - startTime
            updatedTime = timeSwapBuff + timeInMilliseconds
            var secs = (updatedTime / 1000).toInt()
            val mins = secs / 60
            val hrs = mins / 60
            secs = secs % 60
            val milliseconds = (updatedTime % 1000).toInt()
            timerTextView!!.text = (String.format("%02d", hrs) + " : "
                    + String.format("%02d", mins) + " : "
                    + String.format("%02d", secs))
            customHandler.postDelayed(this, 0)

        }
    }

    protected fun loadIcon(status: String) {
        if (status.equals("IDLE")) {
        } else if (status.equals("WAIT") || status.equals("ASSIGN_IP") || status.equals("RECONNECTING")) {

            btnConnect!!.visibility = View.VISIBLE
            lottieAnimationView!!.visibility = View.VISIBLE

            btnPower?.isVisible = false
            progressBar!!.visibility = View.VISIBLE
        } else if (status.equals("CONNECTED")) {

            btnConnect!!.visibility = View.VISIBLE
            lottieAnimationView!!.visibility = View.GONE

            btnPower?.isVisible = true
            progressBar!!.visibility = View.GONE

            if (vpnToastCheck == true) {
                vpnToastCheck = false
                //Toasty.success(this@ContentsActivity, "Server Connected", Toast.LENGTH_SHORT).show()

//                if (mInterstitialAdMob != null) {
//                    mInterstitialAdMob!!.show(this@ContentsActivity);
//                    loadingAd = true
//                    mInterstitialAdMob!!.setFullScreenContentCallback(object :
//                        FullScreenContentCallback() {
//                        override fun onAdDismissedFullScreenContent() {
//                            // Called when fullscreen content is dismissed.
//                            Log.d("TAG", "The ad was dismissed.")
//
//                            //   loadingAd = false;
//                            loadAdAgain()
//
//                        }
//
//                        fun onAdFailedToShowFullScreenContent(adError: AdError?) {
//                            // Called when fullscreen content failed to show.
//                            Log.d("TAG", "The ad failed to show.")
//
//                            loadAdAgain()
//                        }
//
//                        override fun onAdShowedFullScreenContent() {
//                            // Called when fullscreen content is shown.
//                            // Make sure to set your reference to null so you don't
//                            // show it a second time.
//                            mInterstitialAdMob = null
//                            Log.d("TAG", "The ad was shown.")
//                        }
//                    })
//                }
                if(rewardedAd!=null){
                    rewardedAd?.let { ad ->
                        ad.show(this, OnUserEarnedRewardListener { rewardItem ->
                            // Handle the reward.
                            loadAdAgain()
                            val rewardAmount = rewardItem.amount
                            val rewardType = rewardItem.type
                            Log.d(TAG, "User earned the reward.")
                        })
                    } ?: run {
                        Log.d(TAG, "The rewarded ad wasn't ready yet.")
                    }
                }
            }
        }
    }

    protected fun disconnectAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Do you want to disconnect?")
        builder.setPositiveButton(
            "Disconnect"
        ) { _, _ ->
            disconnectFromVpn()
            STATUS = "DISCONNECTED"
            updateUI(STATUS!!)

            textDownloading!!.text = "0.0"
            textUploading!!.text = "0.0"

            showIP()
            vpnToastCheck = true
            Toasty.success(this@ContentsActivity, "Server Disconnected", Toast.LENGTH_SHORT).show()
            if (mInterstitialAdMob != null) {
                mInterstitialAdMob!!.show(this@ContentsActivity);
                mInterstitialAdMob!!.setFullScreenContentCallback(object :
                    FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        // Called when fullscreen content is dismissed.
                        Log.d("TAG", "The ad was dismissed.")

                        loadingAd = false;
                        loadAdAgain()

                    }

                    fun onAdFailedToShowFullScreenContent(adError: AdError?) {
                        // Called when fullscreen content failed to show.
                        Log.d("TAG", "The ad failed to show.")

                        loadAdAgain()
                    }

                    override fun onAdShowedFullScreenContent() {
                        // Called when fullscreen content is shown.
                        // Make sure to set your reference to null so you don't
                        // show it a second time.
                        mInterstitialAdMob = null
                        Log.d("TAG", "The ad was shown.")
                    }
                })
            }

        }
        builder.setNegativeButton(
            "Cancel"
        ) { _, _ ->
            Toasty.success(
                this@ContentsActivity,
                "VPN Remains Connected",
                Toast.LENGTH_SHORT
            ).show()
        }
        builder.show()
    }


    private fun populateUnifiedNativeAdView(
        nativeAd: com.google.android.gms.ads.nativead.NativeAd,
        adView: NativeAdView
    ) {
        val mediaView: MediaView = adView.findViewById(R.id.ad_media)
        adView.mediaView = mediaView

        adView.headlineView = adView.findViewById(R.id.ad_headline)
        adView.bodyView = adView.findViewById(R.id.ad_body)
        adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)
        adView.iconView = adView.findViewById(R.id.ad_app_icon)
        adView.priceView = adView.findViewById(R.id.ad_price)
        adView.starRatingView = adView.findViewById(R.id.ad_stars)
        adView.storeView = adView.findViewById(R.id.ad_store)
        adView.advertiserView = adView.findViewById(R.id.ad_advertiser)

        (adView.headlineView as TextView).text = nativeAd.headline

        if (nativeAd.body == null) {
            adView!!.bodyView!!.visibility = View.INVISIBLE
        } else {
            adView!!.bodyView!!.visibility = View.VISIBLE
            (adView!!.bodyView as TextView).text = nativeAd.body
        }
        if (nativeAd.callToAction == null) {
            adView!!.callToActionView!!.visibility = View.INVISIBLE
        } else {
            adView!!.callToActionView!!.visibility = View.VISIBLE
            (adView.callToActionView as Button).text = nativeAd.callToAction
        }
        if (nativeAd.icon == null) {
            adView!!.iconView!!.visibility = View.GONE
        } else {
            (adView.iconView as ImageView).setImageDrawable(
                nativeAd!!.icon!!.drawable
            )
            adView!!.iconView!!.visibility = View.VISIBLE
        }
        if (nativeAd.price == null) {
            adView!!.priceView!!.visibility = View.INVISIBLE
        } else {
            adView!!.priceView!!.visibility = View.VISIBLE
            (adView.priceView as TextView).text = nativeAd.price
        }
        if (nativeAd.store == null) {
            adView!!.storeView!!.visibility = View.INVISIBLE
        } else {
            adView!!.storeView!!.visibility = View.VISIBLE
            (adView.storeView as TextView).text = nativeAd.store
        }
        if (nativeAd.starRating == null) {
            adView!!.starRatingView!!.visibility = View.INVISIBLE
        } else {
            (adView.starRatingView as RatingBar).rating = nativeAd.starRating!!.toFloat()
            adView!!.starRatingView!!.visibility = View.VISIBLE
        }
        if (nativeAd.advertiser == null) {
            adView!!.advertiserView!!.visibility = View.INVISIBLE
        } else {
            (adView.advertiserView as TextView).text = nativeAd.advertiser
            adView!!.advertiserView!!.visibility = View.VISIBLE
        }

        adView.setNativeAd(nativeAd)
    }


    private fun refreshAd() {
        val adLoader = AdLoader.Builder(this, MainActivity.admob_native_id)
            .forNativeAd { nativeAd ->
                frameLayout!!.visibility = View.VISIBLE
                val adView = layoutInflater
                    .inflate(R.layout.ad_unified, null) as NativeAdView
                if (!Config.vip_subscription && !Config.all_subscription) {
                    populateUnifiedNativeAdView(nativeAd, adView)
                    frameLayout!!.removeAllViews()
                    frameLayout!!.addView(adView)
                }
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    // Handle the failure by logging, altering the UI, and so on.
                }
            })
            .withNativeAdOptions(
                NativeAdOptions.Builder() // Methods in the NativeAdOptions.Builder class can be
                    // used here to specify individual options settings.
                    .build()
            )
            .build()

        adLoader.loadAd(
            AdRequest.Builder()
                .build()
        )
    }

    private fun showServerList() {
        startActivity(Intent(this, Servers::class.java))
    }

    private fun handleTrafficData() {
        if (handlerTraffic == null) return
        handlerTraffic!!.postDelayed({ setTrafficData() }, 1000)
    }

    private fun setTrafficData() {
        val currentRxBytes = TrafficStats.getTotalRxBytes()
        val currentTxBytes = TrafficStats.getTotalTxBytes()
        val usedRxBytes = currentRxBytes - mLastRxBytes
        val usedTxBytes = currentTxBytes - mLastTxBytes
        val currentTime = System.currentTimeMillis()
        val usedTime = currentTime - mLastTime
        mLastRxBytes = currentRxBytes
        mLastTxBytes = currentTxBytes
        mLastTime = currentTime
        mSpeed = Speed(this)
        mSpeed!!.calcSpeed(usedTime, usedRxBytes, usedTxBytes)

        Log.e(
            "speed-->>",
            "down-->>" + mSpeed!!.down.speedValue + "    upload-->>" + mSpeed!!.up.speedValue
        )
        if (mSpeed != null && mSpeed!!.up != null && mSpeed!!.down != null) {

            tvDownloadUnit!!.text = mSpeed!!.down.speedUnit
            tvUploadUnit!!.text = mSpeed!!.up.speedUnit
            textDownloading!!.text = mSpeed!!.down.speedValue
            textUploading!!.text = mSpeed!!.up.speedValue

        } else {
            tvDownloadUnit!!.text = mSpeed!!.down.speedUnit
            tvUploadUnit!!.text = mSpeed!!.up.speedUnit
            textDownloading!!.text = "0"
            textUploading!!.text = "0"
        }
        handleTrafficData()
    }


//    fun updateSubscription() {
//        if (MainActivity.will_dev_33223327_all_ads_on_off && !Config.ads_subscription && !Config.all_subscription && !Config.vip_subscription) {
//            Log.d(TAG, "onStart----: ")
//
//            val mainLayout = findViewById<View>(R.id.fl_adplaceholder) as RelativeLayout
//
//            when (MainActivity.type) {
//                "ad" -> {
//                    refreshAd()
//                }
//
//              }
//            if (MainActivity.type == "ad") {
//
//                if (loadingAd == false) {
//
//                    loadingAd = true
//                    val adRequest = AdRequest.Builder().build()
//
//                    com.google.android.gms.ads.interstitial.InterstitialAd.load(this@ContentsActivity,
//                        MainActivity.admob_interstitial_id,
//                        adRequest,
//                        object : InterstitialAdLoadCallback() {
//                            override fun onAdLoaded(interstitialAd: com.google.android.gms.ads.interstitial.InterstitialAd) {
//                                // The mInterstitialAd reference will be null until
//                                // an ad is loaded.
//                                mInterstitialAdMob = interstitialAd
//                                Log.i("INTERSTITIAL", "onAdLoaded")
//                                loadingAd = false
//
//                                if (mInterstitialAdMob != null) {
//
//                                    //     mInterstitialAdMob!!.show(this@ContentsActivity)
//
//                                    mInterstitialAdMob!!.setFullScreenContentCallback(object :
//                                        FullScreenContentCallback() {
//                                        override fun onAdDismissedFullScreenContent() {
//                                            // Called when fullscreen content is dismissed.
//                                            Log.d("TAG", "The ad was dismissed.")
//
//                                            if (STATUS == "CONNECTED") {
//                                                updateUI("CONNECTED")
//                                                //    connectToVpn()
//                                                loadAdAgain()
//                                            } else if (STATUS == "DISCONNECTED") {
//                                                //disconnectAlert()
//                                                loadAdAgain()
//                                            }
//                                        }
//
//                                        fun onAdFailedToShowFullScreenContent(adError: AdError?) {
//                                            // Called when fullscreen content failed to show.
//                                            Log.d("TAG", "The ad failed to show.")
//
//                                            if (STATUS == "CONNECTED") {
//                                                updateUI("CONNECTED")
//                                                //    connectToVpn()
//                                                loadAdAgain()
//                                            } else if (STATUS == "DISCONNECTED") {
//                                                //  disconnectAlert()
//                                                loadAdAgain()
//                                            }
//                                        }
//
//                                        override fun onAdShowedFullScreenContent() {
//                                            // Called when fullscreen content is shown.
//                                            // Make sure to set your reference to null so you don't
//                                            // show it a second time.
//                                            mInterstitialAdMob = null
//                                            Log.d("TAG", "The ad was shown.")
//                                        }
//                                    })
//                                } else {
//                                    Log.d("TAG", "The interstitial ad wasn't ready yet.")
//
//                                    if (STATUS == "CONNECTED") {
//                                        updateUI("CONNECTED")
//                                        //   connectToVpn()
//                                        loadAdAgain()
//                                    } else if (STATUS == "DISCONNECTED") {
//                                        //disconnectAlert()
//                                        loadAdAgain()
//                                    }
//                                }
//                            }
//
//                            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
//                                // Handle the error
//                                Log.i("INTERSTITIAL", loadAdError.message)
//                                loadingAd = false
//                                mInterstitialAdMob = null
//                            }
//                        })
//
//                }
//
//            }
//
//        } else {
//            Log.e(TAG, "onStart: ")
//        }
//    }

    private val canShowAd: Boolean
        get() = MainActivity.will_dev_33223327_all_ads_on_off &&
                !Config.ads_subscription &&
                !Config.all_subscription &&
                !Config.vip_subscription


    companion object {
        protected val TAG = MainActivity::class.java.simpleName
    }

    fun getIpv4HostAddress(): String {
        NetworkInterface.getNetworkInterfaces()?.toList()?.map { networkInterface ->
            networkInterface.inetAddresses?.toList()?.find {
                !it.isLoopbackAddress && it is Inet4Address
            }?.let { return it.hostAddress }
        }
        return ""
    }

//    fun showInterstitialAndConnect() {
//
//        if (MainActivity.will_dev_33223327_all_ads_on_off && !Config.ads_subscription && !Config.all_subscription && !Config.vip_subscription) {
//            if (MainActivity.type == "ad") {
//                if (loadingAd == false) {
//
//                    loadingAd = true
//                    val adRequest = AdRequest.Builder().build()
//
//                    com.google.android.gms.ads.interstitial.InterstitialAd.load(this@ContentsActivity,
//                        MainActivity.admob_interstitial_id,
//                        adRequest,
//                        object : InterstitialAdLoadCallback() {
//                            override fun onAdLoaded(interstitialAd: com.google.android.gms.ads.interstitial.InterstitialAd) {
//                                // The mInterstitialAd reference will be null until
//                                // an ad is loaded.
//                                mInterstitialAdMob = interstitialAd
//                                Log.i("INTERSTITIAL", "onAdLoaded")
//                                loadingAd = false
//
//                                if (mInterstitialAdMob != null) {
//
//                                    mInterstitialAdMob!!.show(this@ContentsActivity)
//
//                                    mInterstitialAdMob!!.setFullScreenContentCallback(object :
//                                        FullScreenContentCallback() {
//                                        override fun onAdDismissedFullScreenContent() {
//                                            // Called when fullscreen content is dismissed.
//                                            Log.d("TAG", "The ad was dismissed.")
//                                            // prepareVpn()
//                                        }
//
//                                        fun onAdFailedToShowFullScreenContent(adError: AdError?) {
//                                            // Called when fullscreen content failed to show.
//                                            Log.d("TAG", "The ad failed to show.")
//                                            //  prepareVpn()
//                                        }
//
//                                        override fun onAdShowedFullScreenContent() {
//                                            // Called when fullscreen content is shown.
//                                            // Make sure to set your reference to null so you don't
//                                            // show it a second time.
//                                            mInterstitialAdMob = null
//                                            Log.d("TAG", "The ad was shown.")
//                                        }
//                                    })
//                                } else {
//                                    Log.d("TAG", "The interstitial ad wasn't ready yet.")
//                                    //  prepareVpn()
//                                }
//                            }
//
//                            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
//                                // Handle the error
//                                Log.i("INTERSTITIAL", loadAdError.message)
//                                loadingAd = false
//                                mInterstitialAdMob = null
//                            }
//                        })
//
//                } else {
//
//                }
//
//
//            } else if (MainActivity.type == "fb") {
//                AudienceNetworkAds.initialize(this@ContentsActivity)
//
//                val interstitialAdListener: InterstitialAdListener =
//                    object : InterstitialAdListener {
//                        override fun onInterstitialDisplayed(ad: Ad) {}
//                        override fun onInterstitialDismissed(ad: Ad) {
//                            //   prepareVpn()
//                        }
//
//                        override fun onError(
//                            ad: Ad,
//                            adError: com.facebook.ads.AdError
//                        ) {
//                            Log.v("CHECKADS", adError.errorMessage)
//                            //    prepareVpn()
//                        }
//
//                        override fun onAdLoaded(ad: Ad) {
//                            facebookInterstitialAd!!.show()
//                            Log.v("CHECKADS", "loaded")
//                        }
//
//                        override fun onAdClicked(ad: Ad) {}
//                        override fun onLoggingImpression(ad: Ad) {}
//                    }
//
//                facebookInterstitialAd = InterstitialAd(
//                    this@ContentsActivity,
//                    MainActivity.will_dev_33223327_fb_interstitial_id
//                )
//                facebookInterstitialAd!!.loadAd(
//                    facebookInterstitialAd!!.buildLoadAdConfig()
//                        .withAdListener(interstitialAdListener).build()
//                )
//
//            } else {
//                // prepareVpn()
//            }
//        } else {
//            // prepareVpn()
//        }
//    }
}