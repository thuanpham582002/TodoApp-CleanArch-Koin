package com.example.todoappcleanarchwithkoin

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.preference.PreferenceManager
import com.example.core.di.databaseModule
import com.example.core.di.repositoryModule
import com.example.todoappcleanarchwithkoin.di.notificationModule
import com.example.todoappcleanarchwithkoin.di.useCaseModule
import com.example.todoappcleanarchwithkoin.di.viewModelModule
import com.example.todoappcleanarchwithkoin.ui.notification.constants.TODO_CHANNEL_ID
import com.example.todoappcleanarchwithkoin.ui.setting.constants.APP_LANGUAGE
import com.example.todoappcleanarchwithkoin.ui.setting.constants.APP_THEME_MODE
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        val sp = PreferenceManager.getDefaultSharedPreferences(this)
        val language = sp.getString(APP_LANGUAGE, "en")
        val locales = LocaleListCompat.forLanguageTags(language)
        AppCompatDelegate.setApplicationLocales(locales)
        val isNightMode = sp.getString(APP_THEME_MODE, "light")
        if (isNightMode == "dark" && (AppCompatDelegate.getDefaultNightMode() != AppCompatDelegate.MODE_NIGHT_YES)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else if (isNightMode == "light" && (AppCompatDelegate.getDefaultNightMode() != AppCompatDelegate.MODE_NIGHT_NO)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        startKoin {
            androidContext(this@App)
            modules(
                listOf(
                    databaseModule,
                    repositoryModule,
                    useCaseModule,
                    viewModelModule,
                    notificationModule
                )
            )
        }

        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val name = "Alarm ToDo"
        val descriptionText = "Alarm details"
        val mChannel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel(TODO_CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT)
        } else {
            return
        }
        mChannel.description = descriptionText
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(mChannel)
    }
}