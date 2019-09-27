package com.avtdev.whoknows.Activities

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.avtdev.whoknows.BuildConfig
import com.avtdev.whoknows.Fragments.MenuFragment
import com.avtdev.whoknows.Fragments.SettingsDialog
import com.avtdev.whoknows.Listeners.IMainListener
import com.avtdev.whoknows.R
import com.avtdev.whoknows.Services.Constants
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import kotlinx.android.synthetic.main.activity_main.view.*
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class MainActivity : AppCompatActivity(), IMainListener {

    var mFragmentManager: FragmentManager = supportFragmentManager;

    var timer: CountDownTimer? = null
    var settingsDialog: SettingsDialog? = null
    internal var mAdRequest: AdRequest? = null
    var mAdView: AdView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        changeFragment(MenuFragment.newInstance())

        mAdView = findViewById(R.id.adView)
        MobileAds.initialize(this) { initializationStatus -> }


        if(BuildConfig.ADS) {
            mAdView?.setAdUnitId(getString(R.string.admods_banner))
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

    override fun changeFragment(fragment: Fragment){
        var transaction: FragmentTransaction = mFragmentManager.beginTransaction();
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

    fun getCurrentLanguage(): String{
        val sharedPreferences = getSharedPreferences(Constants.Companion.Preferences.NAME.name, Context.MODE_PRIVATE)
        val defaultLanguage = "es";
        var language = sharedPreferences.getString(Constants.Companion.Preferences.LANGUAGE.name, defaultLanguage)
        if(language == null){
            language = defaultLanguage;
        }
        return language
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
}
