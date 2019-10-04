package com.avtdev.whoknows.Services

import android.content.Context
import android.os.Build
import android.os.LocaleList
import java.util.*

class LocaleHelper {

    companion object {

        fun onAttach(context: Context): Context {
            val lang = getPersistedData(context, Locale.getDefault().getLanguage())
            return setLocale(context, lang)
        }

        fun onAttach(context: Context, defaultLanguage: String): Context {
            val lang = getPersistedData(context, defaultLanguage)
            return setLocale(context, lang)
        }

        fun getLanguage(context: Context): String {
            return getPersistedData(context, Locale.getDefault().getLanguage())
        }

        fun setLocale(context: Context, language: String?): Context {
            if(language != null){
                persist(context, language)
                return updateResources(context, language)
            }else{
                return context
            }
        }

        private fun getPersistedData(context: Context, defaultLanguage: String): String {
            val sharedPreferences = context.getSharedPreferences(Constants.Companion.Preferences.NAME.name, Context.MODE_PRIVATE)
            return sharedPreferences.getString(Constants.Companion.Preferences.LANGUAGE.name, defaultLanguage)!!
        }

        private fun persist(context: Context, language: String?) {
            val sharedPreferences = context.getSharedPreferences(Constants.Companion.Preferences.NAME.name, Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()

            editor.putString(Constants.Companion.Preferences.LANGUAGE.name, language)
            editor.apply()
        }

        private fun updateResources(context: Context, language: String): Context {
            val locale = Locale(language)
            Locale.setDefault(locale)

            val resources = context.getResources()
            val configuration = resources.getConfiguration()

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                val localeList = LocaleList(locale)
                LocaleList.setDefault(localeList)
                configuration.setLocale(locale)
                configuration.setLocales(localeList)
                configuration.setLocale(locale);
                val c = context.createConfigurationContext(configuration);
                return c
            } else{
                configuration.setLocale(locale);
                return context.createConfigurationContext(configuration);
            }
        }
    }
}