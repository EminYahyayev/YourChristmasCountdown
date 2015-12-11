package uk.co.friendlycode.yourchristmascountdown.ui.activity;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.v7.app.AppCompatActivity;

import com.squareup.leakcanary.RefWatcher;

import butterknife.ButterKnife;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;
import uk.co.friendlycode.yourchristmascountdown.ChristmasApplication;

/**
 * Base class for all activities. Binds views and watches memory leaks
 *
 * @see ButterKnife
 * @see RefWatcher
 */
abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ChristmasApplication.get(this).startTracking();
    }

    @CallSuper
    @Override protected void onDestroy() {
        super.onDestroy();
        ChristmasApplication.get(this).getRefWatcher().watch(this);
    }

    @CallSuper
    @Override public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        ButterKnife.bind(this);
    }
}