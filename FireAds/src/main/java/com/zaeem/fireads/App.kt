package com.zaeem.fireads

import android.app.Application
import com.google.android.gms.ads.MobileAds


class App: Application() {


    companion object{

         var appOpenManager: AppOpenManager? = null

    }

    override fun onCreate() {
        super.onCreate()

        MobileAds.initialize(this) {}

        appOpenManager =  AppOpenManager(this);


    }

}