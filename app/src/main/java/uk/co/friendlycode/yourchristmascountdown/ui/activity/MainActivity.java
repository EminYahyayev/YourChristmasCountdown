package uk.co.friendlycode.yourchristmascountdown.ui.activity;


import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.format.DateFormat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.squareup.otto.Produce;
import com.squareup.otto.Subscribe;

import java.io.OutputStream;
import java.util.Date;

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

    private static final int REQUEST_CODE_ASK_PERMISSIONS = 1;

    @Bind(R.id.ad_container) ViewGroup mAdContainer;
    @Bind(R.id.fragment_primary) ViewGroup mPrimaryView;

    private AdView mAdView;
    private AdRequest mAdRequest;

    @IdRes
    private int mMainFragmentId = -1;
    private boolean mCanUpdateFragment = true;

    private MusicManager mMusicManager;
    private Handler mHandler;

    @Nullable
    private String mShareMessage;

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

        AdSize adSize = getResources().getDisplayMetrics().density >= 2
                ? AdSize.LARGE_BANNER : AdSize.BANNER;

        mAdView = new AdView(this);
        mAdView.setAdUnitId(getString(R.string.banner_ad_unit_id));
        mAdView.setAdSize(adSize);
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
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    if (mShareMessage != null)
                        shareScreenshot(mShareMessage);
                } else {
                    // Permission Denied
                    Toast.makeText(MainActivity.this,
                            R.string.message_denied_external_storage, Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onShareClick(String message) {
        shareScreenshotWrapper(message);
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

    private void shareScreenshotWrapper(@NonNull String message) {
        mShareMessage = message;

        int hasWriteStoragePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (hasWriteStoragePermission != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_CONTACTS)) {
                new AlertDialog.Builder(this)
                        .setMessage(getString(R.string.message_storage_permission))
                        .setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
                            @Override public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                        REQUEST_CODE_ASK_PERMISSIONS);
                            }
                        })
                        .setNegativeButton(R.string.button_cancel, null)
                        .show();
                return;
            }
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_CODE_ASK_PERMISSIONS);
            return;
        }
        shareScreenshot(message);
    }

    private void shareScreenshot(@NonNull String message) {
        final ContentResolver resolver = getContentResolver();
        final Date now = new Date();
        final View view = mPrimaryView;
        DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

        try {
            // create bitmap screen capture
            view.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
            view.setDrawingCacheEnabled(false);

            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, message);
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            Uri uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            OutputStream outStream = resolver.openOutputStream(uri);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
            outStream.close();

            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("image/jpeg");
            share.putExtra(Intent.EXTRA_STREAM, uri);
            share.putExtra(Intent.EXTRA_TEXT, message);
            startActivity(Intent.createChooser(share, getString(R.string.share_title)));
        } catch (Throwable e) {
            Toast.makeText(this, R.string.share_error, Toast.LENGTH_SHORT).show();
            Timber.e(e, e.getMessage());
        }

        mShareMessage = null;
    }
}
