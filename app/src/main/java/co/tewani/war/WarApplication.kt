package co.tewani.war

import android.app.Application
import com.google.android.material.color.DynamicColors

class WarApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this)
    }
}