package com.avtdev.whoknows.Activities

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.os.CountDownTimer
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ContextThemeWrapper
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.avtdev.whoknows.BuildConfig
import com.avtdev.whoknows.Fragments.GameFragment
import com.avtdev.whoknows.Fragments.MenuFragment
import com.avtdev.whoknows.Fragments.SettingsDialog
import com.avtdev.whoknows.Listeners.IMainListener
import com.avtdev.whoknows.Services.Constants
import com.avtdev.whoknows.Services.Constants.Companion.INTERSTITIAL_AD_TIMES
import com.google.android.gms.ads.*
import java.text.SimpleDateFormat
import java.util.*
import com.avtdev.whoknows.R
import com.avtdev.whoknows.Services.LocaleHelper


class MainActivity : AppCompatActivity(), IMainListener {

    var mFragmentManager: FragmentManager = supportFragmentManager;

    var context: Context = this

    var timer: CountDownTimer? = null
    var settingsDialog: SettingsDialog? = null
    internal var mAdRequest: AdRequest? = null
    var mAdView: AdView? = null
    var mInterstitialAd: InterstitialAd? = null
    var showInterstitial = INTERSTITIAL_AD_TIMES

    override fun attachBaseContext(base: Context) {
        context = LocaleHelper.onAttach(base)
        super.attachBaseContext(context)
    }

    override fun applyOverrideConfiguration(overrideConfiguration: Configuration?) {
        if (overrideConfiguration != null) {
            val uiMode = overrideConfiguration.uiMode
            overrideConfiguration.setTo(baseContext.resources.configuration)
            overrideConfiguration.uiMode = uiMode
        }
        super.applyOverrideConfiguration(overrideConfiguration)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        changeFragment(MenuFragment.newInstance())

        mAdView = findViewById(R.id.adView)
        MobileAds.initialize(this) { _ -> }

        mInterstitialAd = InterstitialAd(this);

        if(BuildConfig.ADS) {
            mAdView?.setAdUnitId(getString(R.string.admods_banner))
            mInterstitialAd?.adUnitId = getString(R.string.admods_intersticial)
        }else{
            mInterstitialAd?.adUnitId = "ca-app-pub-3940256099942544/1033173712"
        }
        mInterstitialAd?.loadAd(AdRequest.Builder().build())
        mInterstitialAd?.adListener = object : AdListener() {
            override fun onAdClosed() {
                mInterstitialAd?.loadAd(AdRequest.Builder().build())
            }
        }
        adsTimer()
    }

    fun adsTimer(){
        val timeLast = getAdsTime()

        if(timeLast > 0){
            timer?.cancel()
            timer = object: CountDownTimer(20000, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    settingsDialog?.changeTime(millisUntilFinished)
                }

                override fun onFinish() {
                    showBammerAds()
                    settingsDialog?.updateAdsTime()
                }
            }
            timer?.start()
        }else{
            showBammerAds()
        }
    }

    fun showBammerAds(){
        if (areAdsEnabled()) {
            if (mAdRequest == null)
                mAdRequest = AdRequest.Builder().build()
            mAdView?.loadAd(mAdRequest)
        }
    }

    override fun showInterstitialAd(){
        if(showInterstitial == 0) {
            if (areAdsEnabled() && mInterstitialAd?.isLoaded!!) {
                mInterstitialAd?.show()
            }
            showInterstitial = INTERSTITIAL_AD_TIMES
        }else{
            showInterstitial --
        }
    }

    override fun changeFragment(fragment: Fragment){
        val transaction: FragmentTransaction = mFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentManager, fragment);
        transaction.commit()
    }

    fun areAdsEnabled(): Boolean {
        val adsTime = getAdsTime();
        return adsTime == 0L
    }

    fun getUTCDate(): Long {
        val pattern = "yyyyMMddHHmmss"
        val date = Date()
        val simpleDateFormat = SimpleDateFormat(pattern)
        simpleDateFormat.timeZone = TimeZone.getTimeZone("UTC")
        return java.lang.Long.parseLong(simpleDateFormat.format(date))
    }

    fun getAdsTime(): Long{
        val sharedPreferences = getSharedPreferences(Constants.Companion.Preferences.NAME.name, Context.MODE_PRIVATE)
        val lastDate = sharedPreferences.getLong(Constants.Companion.Preferences.LAST_DATE.name, 0L)
        val currentDate = getUTCDate()

        // Avoid time changing
        if(lastDate != 0L && lastDate - currentDate < 0){
            return 0L
        }

        val time = sharedPreferences.getLong(Constants.Companion.Preferences.ADS_TIME.name, 0L) - currentDate
        if(time <= 0){
            return 0L
        }
        return time
    }

    fun setPreferences(key: String, data: Any){
        val sharedPreferences = getSharedPreferences(Constants.Companion.Preferences.NAME.name, Context.MODE_PRIVATE).edit()
        if(data is String){
            sharedPreferences.putString(key, data)
        }else if(data is Long){
            sharedPreferences.putLong(key, data)
        }
        sharedPreferences.commit()
    }

    override fun onBackPressed() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.exit)
        .setMessage(R.string.exit_sure)
        .setPositiveButton(R.string.exit) {_ , _ ->
            if (mFragmentManager.fragments.get(0) is GameFragment) {
                changeFragment(MenuFragment.newInstance())
            }else{
                System.exit(0)
            }
        }
        .setNegativeButton(R.string.cancel){dialog, _ ->
            dialog.dismiss()
        }
        .show()
    }
}
