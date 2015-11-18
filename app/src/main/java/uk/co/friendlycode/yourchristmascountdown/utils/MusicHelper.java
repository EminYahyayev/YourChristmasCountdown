package uk.co.friendlycode.yourchristmascountdown.utils;


import android.app.Activity;
import android.media.AudioManager;
import android.media.MediaPlayer;

public final class MusicHelper implements MediaPlayer.OnPreparedListener {

    private final Activity mActivity;

    private MediaPlayer mMusicPlayer;
    private MediaPlayer mSfxPlayer;

    public MusicHelper(Activity activity) {
        mActivity = activity;
        initPlayers();
    }

    private void initPlayers() {
        mMusicPlayer = new MediaPlayer();
        mMusicPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMusicPlayer.setOnPreparedListener(this);
        mMusicPlayer.setLooping(true);
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
    }
}
