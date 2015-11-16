package uk.co.friendlycode.yourchristmascountdown.service;


import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;

import timber.log.Timber;
import uk.co.friendlycode.yourchristmascountdown.R;

public final class MusicIntentService extends IntentService {

    private MediaPlayer mMusicPlayer;
    private MediaPlayer mSfxPlayer;

    public MusicIntentService() {
        super(MusicIntentService.class.getSimpleName());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        final AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        final int volume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        mMusicPlayer = MediaPlayer.create(this, R.raw.christmas_loop);
        mMusicPlayer.setLooping(true);
        mMusicPlayer.setVolume(volume, volume);

        mSfxPlayer = MediaPlayer.create(this, R.raw.ticking_clock);
        mSfxPlayer.setLooping(true);
        mSfxPlayer.setVolume(volume, volume);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Timber.d("onHandleIntent");

        if (!mMusicPlayer.isPlaying()) {
            mMusicPlayer.start();
        }

        if (!mSfxPlayer.isPlaying()) {
            mSfxPlayer.start();
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (!mMusicPlayer.isPlaying()) {
            mMusicPlayer.stop();
        }

        if (!mSfxPlayer.isPlaying()) {
            mSfxPlayer.stop();
        }

        mMusicPlayer.release();
        mSfxPlayer.release();
    }
}
