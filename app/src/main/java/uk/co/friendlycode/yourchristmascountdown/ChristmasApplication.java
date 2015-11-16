package uk.co.friendlycode.yourchristmascountdown;

import android.app.Application;
import android.content.Context;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Logger;
import com.google.android.gms.analytics.Tracker;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import net.danlew.android.joda.JodaTimeAndroid;

import timber.log.Timber;


public final class ChristmasApplication extends Application {

    private RefWatcher mRefWatcher;
    private Tracker mTracker;

    public static ChristmasApplication get(Context context) {
        return (ChristmasApplication) context.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        JodaTimeAndroid.init(this);
        configureTimber();

        mRefWatcher = BuildConfig.DEBUG
                ? LeakCanary.install(this)
                : RefWatcher.DISABLED;
    }

    synchronized public Tracker getTracker() {
        startTracking();
        return mTracker;
    }

    public void startTracking() {
        if (mTracker == null) {
            GoogleAnalytics ga = GoogleAnalytics.getInstance(this);
            mTracker = ga.newTracker(R.xml.track_app);
            ga.enableAutoActivityReports(this);

            ga.getLogger().setLogLevel(BuildConfig.DEBUG
                    ? Logger.LogLevel.VERBOSE
                    : Logger.LogLevel.ERROR);
        }
    }

    public RefWatcher getRefWatcher() {
        return mRefWatcher;
    }

    private void configureTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree() {
                // Add the line number to the tag
                @Override protected String createStackElementTag(StackTraceElement element) {
                    return super.createStackElementTag(element) + ":" + element.getLineNumber();
                }
            });
        } else {
            // Release mode
            Timber.plant(new ReleaseTree());
        }
    }
}
