package uk.co.friendlycode.yourchristmascountdown.ui.fragment;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.OnClick;
import uk.co.friendlycode.yourchristmascountdown.R;
import uk.co.friendlycode.yourchristmascountdown.ui.listener.NavigationListener;

public final class HolidayFragment extends BaseFragment {

    @Bind(R.id.holiday_title) TextView mTitleView;

    private NavigationListener mListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mListener = (NavigationListener) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedState) {
        return inflater.inflate(R.layout.fragment_holiday, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.holiday_scale_up_down);
        mTitleView.startAnimation(animation);
    }

    @Override
    public void onDetach() {
        mListener = NavigationListener.DUMMY;
        super.onDetach();
    }

    @OnClick(R.id.button_settings) void onSettingsClick() {
        mListener.onSettingsClick();
    }

    @OnClick(R.id.button_share) void onShareClick() {
        mListener.onShareClick();
    }
}
