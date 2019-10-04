package com.avtdev.whoknows

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import com.avtdev.whoknows.Services.LocaleHelper



class MainApplication: Application() {

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(LocaleHelper.onAttach(base, "en"))
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        LocaleHelper.onAttach(this)
    }
}