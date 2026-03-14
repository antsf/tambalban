package com.tambal_ban.ads

import android.content.Context
import android.view.View
import android.widget.FrameLayout
import com.google.android.gms.ads.*
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.tambal_ban.R
import com.tambal_ban.utils.Constants

/** Manager class for AdMob ads */
class AdMobManager(private val context: Context) {

    private var bannerAd: AdView? = null
    private var nativeAd: NativeAd? = null
    private var isBannerAdLoaded = false

    /** Initialize AdMob */
    fun initialize() {
        MobileAds.initialize(context) { initializationStatus ->
            val status = initializationStatus.adapterStatusMap
            // AdMob initialized
        }
    }

    /** Load banner ad */
    fun loadBannerAd(adContainer: FrameLayout) {
        if (bannerAd != null) {
            adContainer.removeAllViews()
        }

        bannerAd =
                AdView(context).apply {
                    setAdSize(AdSize.BANNER)
                    adUnitId = Constants.ADMOB_BANNER_AD_UNIT
                    adListener =
                            object : AdListener() {
                                override fun onAdLoaded() {
                                    isBannerAdLoaded = true
                                    adContainer.visibility = View.VISIBLE
                                }

                                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                                    isBannerAdLoaded = false
                                    adContainer.visibility = View.GONE
                                }

                                override fun onAdOpened() {
                                    // Ad opened
                                }

                                override fun onAdClosed() {
                                    // Ad closed
                                }
                            }
                }

        adContainer.addView(bannerAd)
        val adRequest = AdRequest.Builder().build()
        bannerAd?.loadAd(adRequest)
    }

    /** Load native ad */
    fun loadNativeAd(callback: (NativeAd?) -> Unit) {
        val adLoader =
                AdLoader.Builder(context, Constants.ADMOB_NATIVE_AD_UNIT)
                        .forNativeAd { nativeAd ->
                            this.nativeAd = nativeAd
                            callback(nativeAd)
                        }
                        .withAdListener(
                                object : AdListener() {
                                    override fun onAdLoaded() {
                                        // Native ad loaded
                                    }

                                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                                        callback(null)
                                    }
                                }
                        )
                        .build()

        adLoader.loadAd(AdRequest.Builder().build())
    }

    /** Populate native ad view */
    fun populateNativeAdView(nativeAd: NativeAd, adView: NativeAdView) {
        adView.headlineView = adView.findViewById(R.id.ad_headline)
        adView.bodyView = adView.findViewById(R.id.ad_body)
        adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)
        adView.iconView = adView.findViewById(R.id.ad_icon)
        adView.advertiserView = adView.findViewById(R.id.ad_advertiser)

        adView.headlineView?.let { (it as? android.widget.TextView)?.text = nativeAd.headline }

        adView.bodyView?.let {
            if (nativeAd.body.isNullOrEmpty()) {
                it.visibility = View.GONE
            } else {
                it.visibility = View.VISIBLE
                (it as? android.widget.TextView)?.text = nativeAd.body
            }
        }

        adView.callToActionView?.let {
            if (nativeAd.callToAction.isNullOrEmpty()) {
                it.visibility = View.GONE
            } else {
                it.visibility = View.VISIBLE
                (it as? android.widget.Button)?.text = nativeAd.callToAction
            }
        }

        adView.iconView?.let {
            if (nativeAd.icon != null) {
                (it as? android.widget.ImageView)?.setImageDrawable(nativeAd.icon?.drawable)
            } else {
                it.visibility = View.GONE
            }
        }

        adView.advertiserView?.let {
            if (nativeAd.advertiser.isNullOrEmpty()) {
                it.visibility = View.GONE
            } else {
                it.visibility = View.VISIBLE
                (it as? android.widget.TextView)?.text = nativeAd.advertiser
            }
        }

        adView.setNativeAd(nativeAd)
    }

    /** Destroy banner ad */
    fun destroyBannerAd() {
        bannerAd?.destroy()
        bannerAd = null
    }

    /** Destroy native ad */
    fun destroyNativeAd() {
        nativeAd?.destroy()
        nativeAd = null
    }

    /** Pause banner ad */
    fun pauseBannerAd() {
        bannerAd?.pause()
    }

    /** Resume banner ad */
    fun resumeBannerAd() {
        bannerAd?.resume()
    }
}
