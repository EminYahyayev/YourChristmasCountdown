package uk.co.friendlycode.yourchristmascountdown.service;


import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RawRes;

import java.io.IOException;

import timber.log.Timber;
import uk.co.friendlycode.yourchristmascountdown.R;
import uk.co.friendlycode.yourchristmascountdown.utils.PrefUtils;

public final class MusicService extends Service
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    private MediaPlayer mMusicPlayer;
    private MediaPlayerListener mMusicPlayerListener;

    private MediaPlayer mSfxPlayer;
    private MediaPlayerListener mSfxPlayerListener;

    @Override
    public void onCreate() {
        Timber.w("onCreate");

        mMusicPlayer = new MediaPlayer();
        mMusicPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMusicPlayerListener = new MediaPlayerListener(mMusicPlayer);

        mSfxPlayer = new MediaPlayer();
        mSfxPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mSfxPlayerListener = new MediaPlayerListener(mSfxPlayer);

        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new MusicBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Timber.d("onStartCommand");

        if (PrefUtils.isMusicEnabled(this)) {
            Timber.d("Music is enabled, start playing.");
            if (mMusicPlayer.isPlaying()) {
                Timber.d("Music is already playing.");
            } else {
                preparePlayer(mMusicPlayer, R.raw.countdown_music);
            }
        } else {
            if (mMusicPlayer.isPlaying()) {
                mMusicPlayer.stop();
            }
            mMusicPlayer.reset();
        }

        if (PrefUtils.isSfxEnabled(this)) {
            Timber.d("Sfx is enabled, start playing.");
            if (mSfxPlayer.isPlaying()) {
                Timber.d("Sfx is already playing.");
            } else {
                preparePlayer(mSfxPlayer, R.raw.countdown_clock);
            }
        } else {
            if (mSfxPlayer.isPlaying()) {
                mSfxPlayer.stop();
            }
            mSfxPlayer.reset();
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Timber.d("onDestroy");

        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);

        releasePlayer(mMusicPlayer);
        releasePlayer(mSfxPlayer);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sp, String key) {
        if (PrefUtils.PREF_MUSIC_ENABLED.equals(key)) {
            Timber.d("Music enabled=%s", PrefUtils.isMusicEnabled(this));
        } else if (PrefUtils.PREF_SFX_ENABLED.equals(key)) {
            Timber.d("SFX enabled=%s", PrefUtils.isSfxEnabled(this));
        }
    }

    private void preparePlayer(@NonNull MediaPlayer mediaPlayer, @RawRes int resId){
        final AssetFileDescriptor afd = getResources().openRawResourceFd(resId);
        try {
            mediaPlayer.setLooping(true);
            mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            mediaPlayer.setVolume(100, 100);
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            Timber.e(e, e.getMessage());
        }
    }

    private static void releasePlayer(@Nullable MediaPlayer mediaPlayer) {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
        }
    }

    final class MusicBinder extends Binder {
        @SuppressWarnings("unused") MusicService getService() {
            return MusicService.this;
        }
    }

    final class MediaPlayerListener implements MediaPlayer.OnPreparedListener,
            MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {

        public MediaPlayerListener(@NonNull MediaPlayer mediaPlayer) {
            listenMediaPlayer(mediaPlayer);
        }

        public void listenMediaPlayer(@NonNull MediaPlayer mediaPlayer) {
            mediaPlayer.setOnCompletionListener(this);
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setOnErrorListener(this);
        }

        @Override public void onCompletion(MediaPlayer mp) {
            Timber.d("onCompletion");
        }

        @Override public boolean onError(MediaPlayer mp, int what, int extra) {
            Timber.e("onError");
            return false;
        }

        @Override public void onPrepared(MediaPlayer mp) {
            Timber.d("onPrepared");
            mp.start();
        }
    }
}