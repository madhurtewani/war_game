package co.tewani.war.utils

import android.content.Context

class PreferenceManager(context: Context) {

    companion object {
        private const val PREF_NAME = "warPreferences"
        private const val KEY_PRELOAD_IMAGES_COMPLETED = "preloadImagesCompleted"
    }

    private val preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun isPreloadImagesCompleted(): Boolean {
        return preferences.getBoolean(KEY_PRELOAD_IMAGES_COMPLETED, false)
    }

    fun setPreloadImagesCompleted(completed: Boolean) {
        preferences.edit().putBoolean(KEY_PRELOAD_IMAGES_COMPLETED, completed).apply()
    }

}