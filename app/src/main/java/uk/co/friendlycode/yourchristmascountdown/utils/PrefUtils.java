package uk.co.friendlycode.yourchristmascountdown.utils;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public final class PrefUtils {

    /** Corresponds to saved user's name, showed on the top of countdown screen */
    public static final String PREF_PERSONALISE_NAME = "pref_personalise_name";

    /** Is music enabled  */
    public static final String PREF_MUSIC_ENABLED = "pref_music_enabled";

    /** Are sounds effects enabled */
    public static final String PREF_SFX_ENABLED = "pref_sfx_enabled";

    public static String getPersonaliseName(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(PREF_PERSONALISE_NAME, "");
    }

    public static void setPersonaliseName(final Context context, String name) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putString(PREF_PERSONALISE_NAME, name).apply();
    }

    public static boolean isMusicEnabled(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(PREF_MUSIC_ENABLED, true);
    }

    public static void setMusicEnabled(final Context context, boolean enabled) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putBoolean(PREF_MUSIC_ENABLED, enabled).apply();
    }

    public static boolean isSfxEnabled(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(PREF_SFX_ENABLED, true);
    }

    public static void setSfxEnabled(final Context context, boolean enabled) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putBoolean(PREF_SFX_ENABLED, enabled).apply();
    }

    private PrefUtils() {
        throw new AssertionError("No instances");
    }
}
