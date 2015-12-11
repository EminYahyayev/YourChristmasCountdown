package uk.co.friendlycode.yourchristmascountdown.utils;


import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.RawRes;

import com.squareup.otto.Subscribe;

import java.io.IOException;

import timber.log.Timber;
import uk.co.friendlycode.yourchristmascountdown.R;
import uk.co.friendlycode.yourchristmascountdown.ui.event.TimeEvent;

public final class MusicManager implements MediaPlayer.OnPreparedListener,
        SharedPreferences.OnSharedPreferenceChangeListener {

    private final Activity mActivity;

    @RawRes
    private int mLastMusicRes = -1;
    private MediaPlayer mMusicPlayer;
    private boolean mMusicEnabled;
    private boolean mMusicPrepared = false;

    @RawRes
    private int mLastSfxRes = -1;
    private MediaPlayer mSfxPlayer;
    private boolean mSfxEnabled;
    private boolean mSfxPrepared = false;

    public MusicManager(Activity activity) {
        mActivity = activity;

        mMusicPlayer = new MediaPlayer();
        mMusicPlayer.setOnPreparedListener(this);

        mSfxPlayer = new MediaPlayer();
        mSfxPlayer.setOnPreparedListener(this);
    }

    public void resume() {
        PreferenceManager.getDefaultSharedPreferences(mActivity)
                .registerOnSharedPreferenceChangeListener(this);

        mMusicEnabled = PrefUtils.isMusicEnabled(mActivity);
        mSfxEnabled = PrefUtils.isSfxEnabled(mActivity);

        if (mMusicEnabled && mMusicPrepared)
            mMusicPlayer.start();

        if (mSfxEnabled && mSfxPrepared)
            mSfxPlayer.start();

        BusProvider.getInstance().register(this);

    }

    public void pause() {
        BusProvider.getInstance().unregister(this);

        PreferenceManager.getDefaultSharedPreferences(mActivity)
                .unregisterOnSharedPreferenceChangeListener(this);

        if (mMusicPlayer.isPlaying())
            mMusicPlayer.pause();

        if (mSfxPlayer.isPlaying())
            mSfxPlayer.pause();
    }

    public void destroy() {
        mMusicPlayer.release();
        mSfxPlayer.release();
    }

    @Subscribe
    public void onTimeEvent(TimeEvent event) {
        final long secondsLeft = event.duration.getStandardSeconds();

        @RawRes int sfxRes;

        /** SFX update */
        if (secondsLeft > 0 && mSfxEnabled) {
            sfxRes = R.raw.tick_tock;
        } else {
            sfxRes = -1;
        }

        /** Music state changed */
        if (sfxRes != mLastSfxRes) {
            // stop current music
            if (mSfxPlayer.isPlaying()) {
                mSfxPlayer.stop();
            }

            if (mSfxPrepared) {
                Timber.d("Sfx player reset");
                mSfxPlayer.reset();
                mSfxPrepared = false;
            }

            if (sfxRes != -1) {
                // change sfx resource
                if (!mSfxPrepared)
                    preparePlayer(mSfxPlayer, sfxRes);
            }
            mLastSfxRes = sfxRes;
        }

        @RawRes int musicRes;

        /** Music update */
        if (!mMusicEnabled) {
            // music is disabled
            musicRes = -1;
        } else if (secondsLeft > 10) {
            // play standard countdown music
            musicRes = R.raw.countdown_music;
        } else if (secondsLeft == 10) {
            // play countdown voices
            musicRes = R.raw.countdown_voices;
        } else if (secondsLeft < 10 && secondsLeft > -5) {
            // if 10 sec moment is missed, ignore voices
            if (mLastMusicRes != R.raw.countdown_voices)
                musicRes = -1;
            else
                musicRes = R.raw.countdown_voices;
        } else {
            // it's christmas time!
            musicRes = R.raw.merry_christmas_music;
        }

        /** Music state changed */
        if (musicRes != mLastMusicRes) {
            // stop current music
            if (mMusicPlayer.isPlaying()) {
                mMusicPlayer.stop();
            }

            if (mMusicPrepared) {
                Timber.d("Music player reset");
                mMusicPlayer.reset();
                mMusicPrepared = false;
            }

            if (musicRes != -1) {
                // change music resource
                if (!mMusicPrepared)
                    preparePlayer(mMusicPlayer, musicRes);
            }
            mLastMusicRes = musicRes;
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sp, String key) {
        if (PrefUtils.PREF_MUSIC_ENABLED.equals(key)) {
            mMusicEnabled = PrefUtils.isMusicEnabled(mActivity);
        } else if (PrefUtils.PREF_SFX_ENABLED.equals(key)) {
            mSfxEnabled = PrefUtils.isSfxEnabled(mActivity);
        }
    }

    private void preparePlayer(@NonNull MediaPlayer mediaPlayer, @RawRes int resId) {
        final AssetFileDescriptor afd = mActivity.getResources().openRawResourceFd(resId);
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

    @Override
    public void onPrepared(MediaPlayer mp) {
        Timber.d("onPrepared");
        if (mp == mMusicPlayer) {
            Timber.d("Music player prepared");
            mMusicPrepared = true;
            mp.start();
        } else {
            Timber.d("Sfx player prepared");
            mSfxPrepared = true;
            mp.start();
        }
    }
}
