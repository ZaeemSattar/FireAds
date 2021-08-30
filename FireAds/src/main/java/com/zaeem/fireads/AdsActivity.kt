/*
 * Copyright (C) 2017 Google, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zaeem.fireads

import android.app.Activity
import android.content.Context
import android.os.Build
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView
import java.util.*


const val TEST_INTERSTITIAL="ca-app-pub-3940256099942544/1033173712"
const val TEST_NATIVE="ca-app-pub-3940256099942544/2247696110"
const val TEST_BANNER="ca-app-pub-3940256099942544/6300978111"

abstract class AdsActivity : AppCompatActivity() {
  private var currentNativeAd: NativeAd? = null
  private  var adView: AdView? = null
  private var mInterstitialAd: InterstitialAd? = null


  private fun populateUnifiedNativeAdView(nativeAd: NativeAd, adView: NativeAdView) {


    // Set the media view.
    adView.mediaView = adView.findViewById(R.id.ad_media)

    // Set other ad assets.
    adView.headlineView = adView.findViewById(R.id.ad_headline)
    adView.bodyView = adView.findViewById(R.id.ad_body)
    adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)
    adView.iconView = adView.findViewById(R.id.ad_app_icon)
    adView.priceView = adView.findViewById(R.id.ad_price)
    adView.starRatingView = adView.findViewById(R.id.ad_stars)
    adView.storeView = adView.findViewById(R.id.ad_store)
    adView.advertiserView = adView.findViewById(R.id.ad_advertiser)

    // The headline and media content are guaranteed to be in every UnifiedNativeAd.
    (adView.headlineView as TextView).text = nativeAd.headline
    nativeAd.mediaContent?.let {
      adView.mediaView?.setMediaContent(it)
    }

    // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
    // check before trying to display them.
    if (nativeAd.body == null) {
      adView.bodyView?.visibility = View.INVISIBLE
    } else {
      adView.bodyView?.visibility = View.VISIBLE
      (adView.bodyView as TextView).text = nativeAd.body
    }

    if (nativeAd.callToAction == null) {
      adView.callToActionView?.visibility = View.INVISIBLE
    } else {
      adView.callToActionView?.visibility = View.VISIBLE
      (adView.callToActionView as Button).text = nativeAd.callToAction
    }

    if (nativeAd.icon == null) {
      adView.iconView?.visibility = View.GONE
    } else {
      (adView.iconView as ImageView).setImageDrawable(
        nativeAd.icon?.drawable
      )
      adView.iconView?.visibility = View.VISIBLE
    }

    if (nativeAd.price == null) {
      adView.priceView?.visibility = View.INVISIBLE
    } else {
      adView.priceView?.visibility = View.VISIBLE
      (adView.priceView as TextView).text = nativeAd.price
    }

    if (nativeAd.store == null) {
      adView.storeView?.visibility = View.INVISIBLE
    } else {
      adView.storeView?.visibility = View.VISIBLE
      (adView.storeView as TextView).text = nativeAd.store
    }

    if (nativeAd.starRating == null) {
      adView.starRatingView?.visibility = View.INVISIBLE
    } else {
      (adView.starRatingView as RatingBar).rating = nativeAd.starRating!!.toFloat()
      adView.starRatingView?.visibility = View.VISIBLE
    }

    if (nativeAd.advertiser == null) {
      adView.advertiserView?.visibility = View.INVISIBLE
    } else {
      (adView.advertiserView as TextView).text = nativeAd.advertiser
      adView.advertiserView?.visibility = View.VISIBLE
    }

    // This method tells the Google Mobile Ads SDK that you have finished populating your
    // native ad view with this native ad.
    adView.setNativeAd(nativeAd)


  }

  protected fun showNativeAd(ad_frame: FrameLayout, native_ad_id: String) {

    val builder = AdLoader.Builder(this, native_ad_id)

    builder.forNativeAd { unifiedNativeAd ->

      var activityDestroyed = false
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
        activityDestroyed = isDestroyed
      }
      if (activityDestroyed || isFinishing || isChangingConfigurations) {
        unifiedNativeAd.destroy()
        return@forNativeAd
      }
      currentNativeAd?.destroy()
      currentNativeAd = unifiedNativeAd
      val adView = layoutInflater
        .inflate(R.layout.native_ad_large, null) as NativeAdView
      populateUnifiedNativeAdView(unifiedNativeAd, adView)
      ad_frame.removeAllViews()
      ad_frame.addView(adView)
    }

    val isMuted= false
    val videoOptions = VideoOptions.Builder()
      .setStartMuted(isMuted)
      .build()

    val adOptions = NativeAdOptions.Builder()
      .setVideoOptions(videoOptions)
      .build()

    builder.withNativeAdOptions(adOptions)

    val adLoader = builder.withAdListener(object : AdListener() {
      override fun onAdFailedToLoad(loadAdError: LoadAdError) {

        if(BuildConfig.DEBUG)
        {
          val error =
            """
           domain: ${loadAdError.domain}, code: ${loadAdError.code}, message: ${loadAdError.message}
          """"
          Toast.makeText(
            this@AdsActivity, "Failed to load native ad with error $error",
            Toast.LENGTH_SHORT
          ).show()
        }

      }
    }).build()

    adLoader.loadAd(AdRequest.Builder().build())

  }

   fun  getAdSize(container : ViewGroup) : AdSize{
      val display = windowManager.defaultDisplay
      val outMetrics = DisplayMetrics()
      display.getMetrics(outMetrics)

      val density = outMetrics.density

      var adWidthPixels = container.width.toFloat()
      if (adWidthPixels == 0f) {
        adWidthPixels = outMetrics.widthPixels.toFloat()
      }

     val adWidth = (adWidthPixels / density).toInt()
      return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth)
    }


  protected fun showBannerAd(context: Context, container : ViewGroup, banner_id: String)
  {
    adView = AdView(context)
    adView?.adUnitId =banner_id
    adView?.adSize= AdSize.BANNER
    container.addView(adView)
    val adRequest = AdRequest.Builder().build()
    adView?.loadAd(adRequest)


    adView?.adListener = object: AdListener() {
      override fun onAdLoaded() {
        // Code to be executed when an ad finishes loading.
      }

      override fun onAdFailedToLoad(adError : LoadAdError) {
        // Code to be executed when an ad request fails.
      }

      override fun onAdOpened() {
        // Code to be executed when an ad opens an overlay that
        // covers the screen.
      }

      override fun onAdClicked() {
        // Code to be executed when the user clicks on an ad.
      }

      override fun onAdClosed() {
        // Code to be executed when the user is about to return
        // to the app after tapping on an ad.
      }
    }


    adView?.loadAd(adRequest)


  }


  protected fun showInterstitial(context: Activity,ad_id: String)
  {

    val adRequest = AdRequest.Builder().build()

    InterstitialAd.load(this,ad_id, adRequest, object : InterstitialAdLoadCallback() {
      override fun onAdFailedToLoad(adError: LoadAdError) {
        mInterstitialAd = null
      }

      override fun onAdLoaded(interstitialAd: InterstitialAd) {
        mInterstitialAd = interstitialAd
        mInterstitialAd?.fullScreenContentCallback = object: FullScreenContentCallback() {
          override fun onAdDismissedFullScreenContent() {
          }

          override fun onAdFailedToShowFullScreenContent(adError: AdError?) {
          }

          override fun onAdShowedFullScreenContent() {
            mInterstitialAd = null
          }
        }


        if (mInterstitialAd != null) {
          mInterstitialAd?.show(context)
        }
      }
    })

  }


  override fun onDestroy() {
    currentNativeAd?.destroy()
    adView?.destroy()
    super.onDestroy()
  }

}
