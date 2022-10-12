package com.example.todoappcleanarchwithkoin.ui.notification.utils

import android.content.Context
import android.util.Log
import androidx.preference.PreferenceManager
import com.example.todoappcleanarchwithkoin.ui.setting.constants.APP_LANGUAGE

object NotificationUtils {
    fun getStringByLocal(context: Context, stringId: Int): String {
        val locale =
            PreferenceManager.getDefaultSharedPreferences(context).getString(APP_LANGUAGE, "en")
        Log.i("NotificationUtils", "getStringByLocal:  $locale")
        val contextConfig = LanguageUtil.attachBaseContext(context, locale!!)
        return contextConfig.resources.getString(stringId)
    }
}