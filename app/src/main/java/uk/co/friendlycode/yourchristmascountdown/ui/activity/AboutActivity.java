package uk.co.friendlycode.yourchristmascountdown.ui.activity;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import butterknife.Bind;
import butterknife.OnClick;
import uk.co.friendlycode.yourchristmascountdown.R;

public final class AboutActivity extends BaseActivity {

    @Bind(R.id.toolbar) Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        setSupportActionBar(mToolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @OnClick(R.id.about_visit_website) void onVisitWebsite(){
        String url = getString(R.string.url_website);
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(i);
    }

    @OnClick(R.id.about_facebook) void onFacebook(){
        String url = getString(R.string.url_facebook);
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(i);
    }

    @OnClick(R.id.about_twitter) void onTwitter(){
        String url = getString(R.string.url_twitter);
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(i);
    }

    @OnClick(R.id.about_play_market) void onPlayMarket(){
        String url = getString(R.string.url_play_market);
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(i);
    }
}
