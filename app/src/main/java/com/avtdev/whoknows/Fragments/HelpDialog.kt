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


class HelpDialog(context: Context) : Dialog(context) {

    val activity: MainActivity = context as MainActivity

    fun showDialog() {
        val builder = AlertDialog.Builder(activity, R.style.CustomDialogTheme)

        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_help, null)

        builder.setPositiveButton(R.string.close){ dialog, _ -> dialog.dismiss() }
        .setView(dialogView)
        .show()
    }
}