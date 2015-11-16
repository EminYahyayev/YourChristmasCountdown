package uk.co.friendlycode.yourchristmascountdown.ui.activity;


import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import butterknife.Bind;
import uk.co.friendlycode.yourchristmascountdown.R;

public final class HolidayActivity extends BaseMainActivity {

    @Bind(R.id.holiday_title) TextView mTitleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_holiday);
    }

    @Override
    protected void onStart() {
        super.onStart();

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.holiday_scale_up_down);
        mTitleView.startAnimation(animation);
    }
}
