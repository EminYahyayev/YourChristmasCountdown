package uk.co.friendlycode.yourchristmascountdown.utils;


import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RawRes;

import com.squareup.otto.Subscribe;

import java.io.IOException;

import timber.log.Timber;
import uk.co.friendlycode.yourchristmascountdown.R;
import uk.co.friendlycode.yourchristmascountdown.ui.event.TimeEvent;

public final class MusicManagerOld implements MediaPlayer.OnPreparedListener,
        SharedPreferences.OnSharedPreferenceChangeListener {

    private final Activity mActivity;

    @RawRes
    private int mLastMusicRes = -1;
    private MediaPlayer mMusicPlayer;
    private boolean mMusicEnabled;

    private MediaPlayer mSfxPlayer;
    private boolean mSfxEnabled;
    private boolean mSfxPrepared = false;

    public MusicManagerOld(Activity activity) {
        mActivity = activity;

        mMusicPlayer = new MediaPlayer();
        mMusicPlayer.setOnPreparedListener(this);

        mSfxPlayer = new MediaPlayer();
        mSfxPlayer.setOnPreparedListener(this);
    }

    public void resume() {
        PreferenceManager.getDefaultSharedPreferences(mActivity)
                .registerOnSharedPreferenceChangeListener(this);

        BusProvider.getInstance().register(this);

        mSfxEnabled = PrefUtils.isSfxEnabled(mActivity);
        mMusicEnabled = PrefUtils.isMusicEnabled(mActivity);
    }

    public void pause() {
        PreferenceManager.getDefaultSharedPreferences(mActivity)
                .unregisterOnSharedPreferenceChangeListener(this);

        BusProvider.getInstance().unregister(this);

        resetPlayer(mMusicPlayer);
        resetPlayer(mSfxPlayer);
    }

    public void destroy() {
        releasePlayer(mMusicPlayer);
        releasePlayer(mSfxPlayer);
    }

    @Subscribe
    public void onTimeEvent(TimeEvent event) {
        final long secondsLeft = event.duration.getStandardSeconds();
        // SFX update
        if (secondsLeft > 0 && mSfxEnabled) {
            // play sfx before christmas day
            if (!mSfxPlayer.isPlaying()) {
                if (!mSfxPrepared) {
                    preparePlayer(mSfxPlayer, R.raw.tick_tock);
                } else {
                    mSfxPlayer.start();
                }
            }
        } else {
            // stop sfx on christmas day or when sfx disabled
            resetPlayer(mSfxPlayer);
        }

        @RawRes int musicRes = -1;

        // Music Update
        if (!mMusicEnabled) {
            // music is disabled
            musicRes = -1;
        } else if (secondsLeft > 10) {
            // play standard countdown music
            musicRes = R.raw.countdown_music;
        } else if (secondsLeft == 10) {
            // play countdown voices
            musicRes = R.raw.countdown_voices;
        } else if (secondsLeft < 10 && secondsLeft > 0) {
            // if 10 sec moment is missed, ignore voices
            if (mLastMusicRes != R.raw.countdown_voices)
                musicRes = -1;
        } else {
            // it's christmas time!
            musicRes = R.raw.merry_christmas_music;
        }

        //check whether we need to change the music
        if (musicRes != mLastMusicRes) {
            // stop current music
            resetPlayer(mMusicPlayer);
            if (musicRes != -1) {
                // change music resource
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
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.reset();
        }

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
            Timber.d("Music player is prepared");
            mp.start();
        } else {
            Timber.d("Sfx player is prepared");
            mSfxPrepared = true;
        }
    }

    private void resetPlayer(@Nullable MediaPlayer mediaPlayer) {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                mediaPlayer.reset();
            }
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
