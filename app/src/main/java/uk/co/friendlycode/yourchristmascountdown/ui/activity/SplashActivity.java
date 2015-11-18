package uk.co.friendlycode.yourchristmascountdown.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import uk.co.friendlycode.yourchristmascountdown.R;

public final class SplashActivity extends BaseActivity {

    private static final int SPLASH_DELAY_MILLIS = 1500;

    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override public void run() {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
            }
        }, SPLASH_DELAY_MILLIS);
    }
}
