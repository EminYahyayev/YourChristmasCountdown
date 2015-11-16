package uk.co.friendlycode.yourchristmascountdown.ui.activity;


import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import butterknife.Bind;
import butterknife.OnClick;
import uk.co.friendlycode.yourchristmascountdown.R;

public abstract class BaseMainActivity extends BaseActivity {

    @Bind(R.id.ad_view) AdView mAdView;

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        final AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    @OnClick(R.id.button_settings) void onSettingsClick() {
        startActivity(new Intent(this, SettingsActivity.class));
    }
}
