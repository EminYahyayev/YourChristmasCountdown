package uk.co.friendlycode.yourchristmascountdown.ui.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import butterknife.Bind;
import uk.co.friendlycode.yourchristmascountdown.R;

public final class HolidayFragment extends BaseFragment {

    @Bind(R.id.holiday_title) TextView mTitleView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_holiday, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.holiday_scale_up_down);
        mTitleView.startAnimation(animation);
    }
}
