package uk.co.friendlycode.yourchristmascountdown.ui.activity;


import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.ViewGroup;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.squareup.otto.Produce;
import com.squareup.otto.Subscribe;

import butterknife.Bind;
import timber.log.Timber;
import uk.co.friendlycode.yourchristmascountdown.R;
import uk.co.friendlycode.yourchristmascountdown.ui.event.TimeEvent;
import uk.co.friendlycode.yourchristmascountdown.ui.fragment.AboutFragment;
import uk.co.friendlycode.yourchristmascountdown.ui.fragment.CountdownFragment;
import uk.co.friendlycode.yourchristmascountdown.ui.fragment.HolidayFragment;
import uk.co.friendlycode.yourchristmascountdown.ui.fragment.SettingsFragment;
import uk.co.friendlycode.yourchristmascountdown.ui.listener.NavigationListener;
import uk.co.friendlycode.yourchristmascountdown.utils.BusProvider;
import uk.co.friendlycode.yourchristmascountdown.utils.MusicManager;
import uk.co.friendlycode.yourchristmascountdown.utils.TimeUtils;

public final class MainActivity extends BaseActivity
        implements NavigationListener, SettingsFragment.Listener,
        FragmentManager.OnBackStackChangedListener {

    @Bind(R.id.ad_container) ViewGroup mAdContainer;

    private AdView mAdView;
    private AdRequest mAdRequest;

    @IdRes
    private int mMainFragmentId = -1;
    private boolean mCanUpdateFragment = true;

    private MusicManager mMusicManager;
    private Handler mHandler;

    private final Runnable mTimerUpdate = new Runnable() {
        @Override public void run() {
            final long before = System.currentTimeMillis();
            BusProvider.getInstance().post(produceTimeEvent());
            final long delay = System.currentTimeMillis() - before;
            Timber.d("1000-delay = %d", 1000 - delay);
            mHandler.postDelayed(this, 1000 - delay);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mHandler = new Handler();
        mMusicManager = new MusicManager(this);

        getSupportFragmentManager().addOnBackStackChangedListener(this);

        mAdView = new AdView(this);
        mAdView.setAdUnitId(getString(R.string.banner_ad_unit_id));
        mAdView.setAdSize(AdSize.LARGE_BANNER);
        mAdContainer.addView(mAdView);

        mAdRequest = new AdRequest.Builder().build();
        mAdView.loadAd(mAdRequest);

        mAdView.setAdListener(new AdListener() {
            @Override public void onAdLoaded() {
                super.onAdLoaded();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);

        mMusicManager.resume();
        mAdView.resume();
        mHandler.post(mTimerUpdate);
    }

    @Override
    protected void onPause() {
        BusProvider.getInstance().unregister(this);
        super.onPause();

        mMusicManager.pause();
        mAdView.pause();
        mHandler.removeCallbacks(mTimerUpdate);
    }

    @Override
    protected void onDestroy() {
        mAdContainer.removeView(mAdView);
        mAdRequest = null;
        mAdView.removeAllViews();
        mAdView.setAdListener(null);
        mAdView.destroy();

        mMusicManager.destroy();
        super.onDestroy();
    }

    @Override
    public void onShareClick() {
        replacePrimaryFragment(new HolidayFragment());
    }

    @Override
    public void onSettingsClick() {
        replaceSecondaryFragment(new SettingsFragment());
    }

    @Override
    public void onAboutClick() {
        replaceSecondaryFragment(new AboutFragment());
    }

    @Override
    public void onBackStackChanged() {
        int size = getSupportFragmentManager().getBackStackEntryCount();
        Timber.w("onBackStackChanged, entryCount=%d", size);
        mCanUpdateFragment = size <= 1;
    }

    @Produce
    public TimeEvent produceTimeEvent() {
        final TimeEvent event = new TimeEvent(TimeUtils.getChristmasDate());
        Timber.d("produceTimeEvent: secondsLeft=%d", event.duration.getStandardSeconds());
        Timber.v("produceTimeEvent: date=%s", event.now);
        return event;
    }

    @Subscribe
    public void onTimeEvent(TimeEvent event) {
        updateFragmentState(event.duration.getStandardSeconds());
    }

    private void updateFragmentState(long secondsLeft) {
        if (!mCanUpdateFragment) {
            Timber.d("Can't update fragment. Skipping the update.");
            return;
        }

        @IdRes int newFragmentId;
        if (secondsLeft > 0) {
            newFragmentId = R.id.fragment_countdown;
        } else {
            newFragmentId = R.id.fragment_holiday;
        }

        if (mMainFragmentId != newFragmentId) {
            if (newFragmentId == R.id.fragment_countdown)
                replacePrimaryFragment(new CountdownFragment());
            else
                replacePrimaryFragment(new HolidayFragment());

            mMainFragmentId = newFragmentId;
        }
    }

    private void replacePrimaryFragment(@NonNull Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_primary, fragment)
                .commit();
    }

    private void replaceSecondaryFragment(@NonNull Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_secondary, fragment)
                .addToBackStack(fragment.getClass().getSimpleName())
                .commit();
    }
}
