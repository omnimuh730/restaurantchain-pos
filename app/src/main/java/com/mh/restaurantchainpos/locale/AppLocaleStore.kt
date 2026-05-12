package com.mh.restaurantchainpos.locale

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

/**
 * Persists the user's explicit app language (English / Korean) and applies it
 * via [AppCompatDelegate.setApplicationLocales], which updates resources and
 * recreates activities as needed.
 */
object AppLocaleStore {
    private const val PREFS = "pos_app_settings"
    private const val KEY_LOCALE = "app_locale_tag"

    private fun prefs(context: Context) =
        context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    /** BCP-47 tag `en` or `ko`; null means follow device locale until the user picks. */
    fun explicitTag(context: Context): String? =
        prefs(context).getString(KEY_LOCALE, null)

    /**
     * Call from [android.app.Activity.onCreate] before [android.app.Activity.super.onCreate]
     * so the first frame uses the stored locale when the user has chosen one.
     */
    fun applyStoredLocaleBeforeActivityOnCreate(context: Context) {
        val tag = explicitTag(context) ?: return
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(tag))
    }

    fun setLocale(context: Context, uiCode: String) {
        val tag = when (uiCode.uppercase()) {
            "KO" -> "ko"
            else -> "en"
        }
        // commit() so the value is persisted before activity recreate; apply() can race and
        // the next onCreate may read null and skip setApplicationLocales.
        prefs(context).edit().putString(KEY_LOCALE, tag).commit()
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(tag))
    }

    /** `"EN"` or `"KO"` for UI highlight (respects stored choice, else device language). */
    fun effectiveUiCode(context: Context): String {
        explicitTag(context)?.let { return it.uppercase() }
        val lang = context.resources.configuration.locales.get(0).language
        return if (lang == "ko") "KO" else "EN"
    }
}
