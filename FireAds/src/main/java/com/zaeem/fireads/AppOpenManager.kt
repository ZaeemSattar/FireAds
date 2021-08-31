package com.zaeem.fireads

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.appopen.AppOpenAd.AppOpenAdLoadCallback
import java.util.*


class AppOpenManager(var myApplication: App) :  Application.ActivityLifecycleCallbacks,
    LifecycleObserver {

    private var isShowingAd = false

    private var loadTime: Long = 0


    init {
        myApplication.registerActivityLifecycleCallbacks(this);
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);


    }

    private var appOpenAd: AppOpenAd? = null
    private var loadCallback: AppOpenAdLoadCallback? = null
    private var activity: Activity? = null




    private val adRequest: AdRequest
        get() = AdRequest.Builder().build()


    private val isAdAvailable: Boolean
        get() = appOpenAd != null && wasLoadTimeLessThanNHoursAgo(4);



    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        showAdIfAvailable()
    }


    public fun showAdIfAvailable()
    {

        if (!isShowingAd && isAdAvailable) {

            val fullScreenContentCallback: FullScreenContentCallback =
                object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        // Set the reference to null so isAdAvailable() returns false.
                        appOpenAd = null
                        isShowingAd = false
                        fetchAd()
                    }

                    override fun onAdFailedToShowFullScreenContent(adError: AdError) {}
                    override fun onAdShowedFullScreenContent() {
                        isShowingAd = true
                    }
                }

            appOpenAd!!.fullScreenContentCallback = fullScreenContentCallback
            appOpenAd!!.show(activity)

        }
        else
        {
            fetchAd()

        }
    }


    fun fetchAd() {

        if (isAdAvailable) {
            return
        }
        loadCallback = object : AppOpenAdLoadCallback() {


            override fun onAdLoaded(ad: AppOpenAd) {
                appOpenAd = ad
                loadTime = ( Date()).getTime();

            }

            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                // Handle the error.
            }
        }

        AppOpenAd.load(
            myApplication, TEST_AP_OPEN, adRequest,
            AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT, loadCallback
        )
    }


    /** Utility method to check if ad was loaded more than n hours ago.  */
    private fun wasLoadTimeLessThanNHoursAgo(numHours: Long): Boolean {
        val dateDifference = Date().time - loadTime
        val numMilliSecondsPerHour: Long = 3600000
        return dateDifference < numMilliSecondsPerHour * numHours
    }


    override fun onActivityCreated(mActivity: Activity, p1: Bundle?) {
    }

    override fun onActivityStarted(mActivity: Activity) {
        activity = mActivity
    }

    override fun onActivityResumed(mActivity: Activity) {
        activity = mActivity
    }

    override fun onActivityPaused(p0: Activity) {
    }

    override fun onActivityStopped(pmActivity: Activity) {

    }

    override fun onActivitySaveInstanceState(mActivity: Activity, p1: Bundle) {
    }

    override fun onActivityDestroyed(mActivity: Activity) {
        activity = mActivity
    }





}
