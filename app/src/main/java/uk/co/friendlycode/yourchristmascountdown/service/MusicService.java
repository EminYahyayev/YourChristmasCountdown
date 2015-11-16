package uk.co.friendlycode.yourchristmascountdown.service;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RawRes;

import java.io.IOException;

import timber.log.Timber;
import uk.co.friendlycode.yourchristmascountdown.R;

public final class MusicService extends Service implements MediaPlayer.OnPreparedListener {
    public static final String ACTION_PLAY
            = "uk.co.friendlycode.yourchristmascountdown.actions.PLAY";
    public static final String ACTION_STOP
            = "uk.co.friendlycode.yourchristmascountdown.actions.PLAY";

    public static final String EXTRA_MEDIA_PLAYER
            = "uk.co.friendlycode.yourchristmascountdown.extras.MEDIA_PLAYER";

    public static final String MEDIA_PLAYER_MUSIC = "music";
    public static final String MEDIA_PLAYER_SFX = "sfx";

    @Nullable
    private MediaPlayer mMusicPlayer;
    @Nullable
    private MediaPlayer mSfxPlayer;

    @Nullable @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Timber.d("onCreate");
        final AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        final int volume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        try {
            mMusicPlayer = createMediaPlayer(R.raw.christmas_loop, volume);
            mSfxPlayer = createMediaPlayer(R.raw.ticking_clock, volume);
        } catch (IOException e) {
            Timber.e("create failed: %s", e.getMessage());
        }
    }

    private MediaPlayer createMediaPlayer(@RawRes int resId, int volume) throws IOException {
        final AssetFileDescriptor afd = getResources().openRawResourceFd(resId);

        MediaPlayer md = new MediaPlayer();
        md.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
        md.setAudioStreamType(AudioManager.STREAM_MUSIC);
        md.setLooping(true);
        md.setVolume(volume, volume);
        md.setOnPreparedListener(this);
        afd.close();

        return md;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final String action = intent.getAction();
        final String player = intent.getStringExtra(EXTRA_MEDIA_PLAYER);
        Timber.d("onStartCommand: action=%s, player=%s", action, player);

//        if (action == null) {
//            throw new NullPointerException("Action must not be null");
//        }

        if (player == null) {
            throw new NullPointerException("Player extra must not be null");
        }

        if (player.equals(MEDIA_PLAYER_MUSIC)) {
           //mMusicPlayer.prepareAsync();
        } else if (player.equals(MEDIA_PLAYER_SFX)) {
            mSfxPlayer.prepareAsync();
        } else {
            throw new UnsupportedOperationException("Unsupported action");
        }

        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        Timber.d("onDestroy");

        tryReleasePlayer(mMusicPlayer);
        tryReleasePlayer(mSfxPlayer);
    }

    private static void tryStartPlayer(@Nullable MediaPlayer mediaPlayer) {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }

    private static void tryReleasePlayer(@Nullable MediaPlayer mediaPlayer) {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
        }
    }
}
