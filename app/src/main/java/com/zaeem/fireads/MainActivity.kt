package com.zaeem.fireads

import android.os.Bundle
import com.zaeem.fireads.databinding.ActivityMainBinding


class MainActivity: AdsActivity()
{

    private lateinit var binding : ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)


        setContentView(binding.root)

        showNativeAd(ad_container = binding.nativeAddLayout, TEST_NATIVE, NativeType.LARGE)


        showBannerAd(this,binding.bannerAddLayout, TEST_BANNER,BannerType.ADAPTIVE)


        showInterstitial(this, TEST_INTERSTITIAL)
    }
}