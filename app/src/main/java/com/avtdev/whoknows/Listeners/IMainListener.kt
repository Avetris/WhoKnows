package com.avtdev.whoknows.Listeners

import androidx.fragment.app.Fragment

interface IMainListener {

    fun showSettingsDialog()
    fun changeFragment(fragment: Fragment)
    fun showInterstitialAd()


}