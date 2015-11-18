package uk.co.friendlycode.yourchristmascountdown.ui.activity;


import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RawRes;

import org.joda.time.DateTime;

import java.io.IOException;

import timber.log.Timber;
import uk.co.friendlycode.yourchristmascountdown.R;
import uk.co.friendlycode.yourchristmascountdown.ui.fragment.AboutFragment;
import uk.co.friendlycode.yourchristmascountdown.ui.fragment.CountdownFragment;
import uk.co.friendlycode.yourchristmascountdown.ui.fragment.HolidayFragment;
import uk.co.friendlycode.yourchristmascountdown.ui.fragment.SettingsFragment;
import uk.co.friendlycode.yourchristmascountdown.utils.PrefUtils;
import uk.co.friendlycode.yourchristmascountdown.utils.TimeModel;
import uk.co.friendlycode.yourchristmascountdown.utils.TimeUtils;

public final class MainActivity extends BaseActivity
        implements CountdownFragment.Listener, SettingsFragment.Listener,
        SharedPreferences.OnSharedPreferenceChangeListener, MediaPlayer.OnPreparedListener {

    @TimeUtils.State int mFragmentState = TimeUtils.STATE_EMPTY;
    @TimeUtils.State int mSfxState = TimeUtils.STATE_EMPTY;
    @TimeUtils.State int mMusicState = TimeUtils.STATE_EMPTY;

    private MediaPlayer mMusicPlayer;
    private MediaPlayer mSfxPlayer;
    private boolean mSfxPrepared = false;

    private Handler mHandler;
    private CountdownFragment mCountdownFragment;

    private final Runnable mTimer = new Runnable() {
        @Override public void run() {
            final DateTime now = DateTime.now();
            final TimeModel model = TimeUtils.getTimeModel(now);

            @TimeUtils.State int state = TimeUtils.getState(now);
            updateFragmentState(state);

            if (mCountdownFragment != null)
                mCountdownFragment.updateTimeContent(model);

            if (mSfxPrepared && !mSfxPlayer.isPlaying()) {
                mSfxPlayer.start();
                mSfxPrepared = false;
            }

            mHandler.postDelayed(this, 1000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mHandler = new Handler();

        initMediaPlayers();

        int state = TimeUtils.getState(DateTime.now());
        updateFragmentState(state);
    }

    @Override
    protected void onResume() {
        super.onResume();
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);

        final int state = TimeUtils.getState(DateTime.now());
        updateMusicState(state, true);
        updateSfxState(state, true);

        mHandler.post(mTimer);
    }

    @Override
    protected void onPause() {
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);

        resetPlayer(mMusicPlayer);
        resetPlayer(mSfxPlayer);

        mHandler.removeCallbacks(mTimer);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        releasePlayer(mMusicPlayer);
        releasePlayer(mSfxPlayer);
        super.onDestroy();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sp, String key) {
        final int state = TimeUtils.getState(DateTime.now());
        if (PrefUtils.PREF_MUSIC_ENABLED.equals(key)) {
            updateMusicState(state, false);
        } else if (PrefUtils.PREF_SFX_ENABLED.equals(key)) {
            updateSfxState(state, false);
        }
    }

    @Override
    public void onSettingsClick() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new SettingsFragment())
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onShareClick() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new HolidayFragment())
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onAboutClick() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new AboutFragment())
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        Timber.d("onPrepared");
        if (mp == mMusicPlayer) {
            Timber.d("Music player is prepared");
            mp.start();
        } else {
            Timber.d("Sfx player is prepared");
            mSfxPrepared = true;
        }
    }

    private void updateFragmentState(@TimeUtils.State int state) {
        if (mFragmentState == state) {
            return;
        }

        if (state == TimeUtils.STATE_HOLIDAY) {
            mCountdownFragment = null;

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new HolidayFragment())
                    .commit();
        } else {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, mCountdownFragment = new CountdownFragment())
                    .commit();
        }

        mFragmentState = state;
    }

    private void updateMusicState(@TimeUtils.State int state, boolean isResume) {
        if (mMusicState == state && !isResume) {
            return;
        }

        if (PrefUtils.isMusicEnabled(this)) {
            if (state == TimeUtils.STATE_HOLIDAY) {
                preparePlayer(mMusicPlayer, R.raw.merry_christmas_music);
            } else {
                preparePlayer(mMusicPlayer, R.raw.countdown_music);
            }
        } else {
            resetPlayer(mMusicPlayer);
        }

        mMusicState = state;
    }

    private void updateSfxState(@TimeUtils.State int state, boolean isResume) {
        if (mSfxState == state && !isResume) {
            return;
        }

        if (PrefUtils.isSfxEnabled(this)) {
            if (state != TimeUtils.STATE_HOLIDAY) {
                preparePlayer(mSfxPlayer, R.raw.countdown_clock);
            } else {
                resetPlayer(mSfxPlayer);
            }
        } else {
            resetPlayer(mSfxPlayer);
        }

        mSfxState = state;
    }

    private void preparePlayer(@NonNull MediaPlayer mediaPlayer, @RawRes int resId) {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.reset();
        }

        final AssetFileDescriptor afd = getResources().openRawResourceFd(resId);
        try {
            mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setVolume(100, 100);
            mediaPlayer.setLooping(true);
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            Timber.e(e, e.getMessage());
        }
    }

    private void initMediaPlayers() {
        mMusicPlayer = new MediaPlayer();
        mMusicPlayer.setOnPreparedListener(this);

        mSfxPlayer = new MediaPlayer();
        mSfxPlayer.setOnPreparedListener(this);
    }

    private void resetPlayer(@Nullable MediaPlayer mediaPlayer) {
        if (mediaPlayer != null) {
            if (mediaPlayer == mSfxPlayer) {
                mSfxPrepared = false;
            }

            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.reset();
        }
    }

    private void releasePlayer(@Nullable MediaPlayer mediaPlayer) {
        if (mediaPlayer != null) {
            if (mediaPlayer == mSfxPlayer) {
                mSfxPrepared = false;
            }

            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
        }
    }
}
