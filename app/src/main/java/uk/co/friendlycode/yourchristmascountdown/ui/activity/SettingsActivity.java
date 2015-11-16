package uk.co.friendlycode.yourchristmascountdown.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.CompoundButton;
import android.widget.Switch;

import butterknife.Bind;
import butterknife.OnClick;
import uk.co.friendlycode.yourchristmascountdown.R;
import uk.co.friendlycode.yourchristmascountdown.ui.PrefUtils;

public final class SettingsActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener {

    @Bind(R.id.toolbar) Toolbar mToolbar;
    @Bind(R.id.music_switch) Switch mMusicSwitch;
    @Bind(R.id.sfx_switch) Switch mSfxSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        setSupportActionBar(mToolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mMusicSwitch.setChecked(PrefUtils.isMusicEnabled(this));
        mMusicSwitch.setOnCheckedChangeListener(this);
        mSfxSwitch.setChecked(PrefUtils.isSexEnabled(this));
    }

    @Override
    public void onCheckedChanged(CompoundButton button, boolean isChecked) {
        if (R.id.music_switch == button.getId()) {
            PrefUtils.setMusicEnabled(this, isChecked);
        } else if (R.id.sfx_switch == button.getId()) {
            PrefUtils.setSfxEnabled(this, isChecked);
        } else {
            throw new UnsupportedOperationException("No operation for such button.");
        }
    }

    @OnClick(R.id.button_about) void onAbout(){
        startActivity(new Intent(this, AboutActivity.class));
    }
}
