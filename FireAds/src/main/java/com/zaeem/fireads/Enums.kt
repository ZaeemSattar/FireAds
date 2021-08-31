package com.zaeem.fireads

import androidx.annotation.LayoutRes
import com.google.android.gms.ads.AdSize


enum class NativeType(@LayoutRes val  viewId : Int){
    SMALL(R.layout.native_ad_small),
    MEDIUM(R.layout.native_ad_medium),
    LARGE(R.layout.native_ad_large),
}
enum class BannerType(val adSize: AdSize?){
    BANNER(AdSize.BANNER),
    LARGE_BANNER(AdSize.LARGE_BANNER),
    MEDIUM_RECTANGLE(AdSize.MEDIUM_RECTANGLE),
    FULL_BANNER(AdSize.FULL_BANNER),
    LEADERBOARD(AdSize.LEADERBOARD),
    ADAPTIVE(null)
}