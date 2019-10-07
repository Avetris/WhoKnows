package com.avtdev.whoknows.Fragments

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.view.View
import android.widget.*
import com.avtdev.whoknows.Activities.MainActivity
import com.avtdev.whoknows.BuildConfig
import com.avtdev.whoknows.R
import com.avtdev.whoknows.Services.Constants
import com.avtdev.whoknows.Services.LocaleHelper
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdCallback
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import java.util.*


class SettingsDialog(context: Context) : Dialog(context) {

    var removeAds: Button? = null
    val activity: MainActivity = context as MainActivity
    var mRewardedAd: RewardedAd = createRewardedAdd(true)

    fun showDialog() {
        val builder = AlertDialog.Builder(activity, R.style.CustomDialogTheme)

        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_settings, null)

        val languageSpinner = dialogView.findViewById<Spinner> (R.id.spLanguageSpinner)

        val adapter = ArrayAdapter<String>(activity, R.layout.spinner_item,
            activity.getResources().getStringArray(R.array.languages_available))

        val shorts_lan = activity.getResources().getStringArray(R.array.languages_available_short)
        val currentLan = LocaleHelper.getLanguage(activity)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        languageSpinner.adapter = adapter
        val index = shorts_lan.indexOf(currentLan)
        if(index > 0){
            languageSpinner.setSelection(index)
        }

        removeAds = dialogView.findViewById<Button>(R.id.btnRemoveAds)

        enableAdsButton(false)
        updateAdsTime()

        builder.setPositiveButton(R.string.save){ dialog, _ ->
            val lan : String = shorts_lan[languageSpinner.selectedItemPosition]

            LocaleHelper.setLocale(activity, lan)
            activity.recreate()

            dialog.dismiss()
        }
        .setNegativeButton(R.string.cancel){ dialog, _ ->
            dialog.dismiss()
        }
        builder.setView(dialogView);
        builder.show()
    }

    fun changeTime(time: Long){
        var seconds = (time / 1000).toInt()
        var minutes = seconds / 60
        val hours = minutes / 60
        minutes -= hours * 60
        seconds -= minutes * 60 + hours * 3600
        if (hours > 0) {
            removeAds?.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds))
        } else if (minutes > 0) {
            removeAds?.setText(String.format("%02d:%02d", minutes, seconds))
        } else {
            removeAds?.setText(String.format("%02d", seconds))
        }
    }

    fun createRewardedAdd(firstLoad: Boolean): RewardedAd{
        val rewardedAd = RewardedAd(activity, if(BuildConfig.ADS) activity.getString(R.string.admods_rewarded) else "ca-app-pub-3940256099942544/5224354917")
        if(!firstLoad) {
            val adLoadCallback = object : RewardedAdLoadCallback() {
                override fun onRewardedAdLoaded() {
                    enableAdsButton(true)
                }

                override fun onRewardedAdFailedToLoad(errorCode: Int) {
                    enableAdsButton(false)
                }
            }
            rewardedAd.loadAd(AdRequest.Builder().build(), adLoadCallback)
        }
        return rewardedAd
    }

    fun enableAdsButton(enable: Boolean){
        if(enable){
            removeAds?.isEnabled = true;
            removeAds?.alpha = 1f
        }else{
            removeAds?.isEnabled = false;
            removeAds?.alpha = 0.5f
        }
    }

    fun updateAdsTime(){
        if(activity.areAdsEnabled()){
            createRewardedAdd(false)
            removeAds?.setOnClickListener{_->
                if (mRewardedAd.isLoaded()) {
                    enableAdsButton(false)
                    val adCallback = object : RewardedAdCallback(){
                        override fun onUserEarnedReward(reward: RewardItem) {
                            setRemoveAds(reward.amount)
                            activity.adsTimer()
                        }

                        override fun onRewardedAdClosed() {
                            mRewardedAd = createRewardedAdd(false)
                        }
                    }
                    mRewardedAd.show(activity, adCallback)
                }
            }
            removeAds?.setText(R.string.remove_ads)
        }
    }

    private fun setRemoveAds(time: Int) {
        val cal = Calendar.getInstance()
        cal.add(Calendar.SECOND, time)
        activity.setPreferences(Constants.Companion.Preferences.ADS_TIME.name, activity.getUTCDate())
        activity.setPreferences(Constants.Companion.Preferences.ADS_TIME.name, cal.timeInMillis)
    }
}