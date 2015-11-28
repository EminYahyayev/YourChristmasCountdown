package uk.co.friendlycode.yourchristmascountdown.ui.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.LinearInterpolator;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import butterknife.Bind;
import butterknife.OnClick;
import timber.log.Timber;
import uk.co.friendlycode.yourchristmascountdown.R;
import uk.co.friendlycode.yourchristmascountdown.ui.event.TimeEvent;
import uk.co.friendlycode.yourchristmascountdown.ui.listener.NavigationListener;
import uk.co.friendlycode.yourchristmascountdown.ui.widget.ChristmasViewPager;
import uk.co.friendlycode.yourchristmascountdown.utils.PrefUtils;

public final class CountdownFragment extends BaseFragment {

    private static final int ANIMATION_DURATION = 16000;

    @Bind(R.id.view_pager) ChristmasViewPager mViewPager;
    @Bind(R.id.countdown_title) TextView mTitleView;
    @Bind(R.id.scroll_view) HorizontalScrollView mScrollView;

    private ValueAnimator mCurrentAnimator;
    private LinearInterpolator mLinearInterpolator;

    private NavigationListener mListener = NavigationListener.DUMMY;
    private int mBackgroundScrollWidth;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mListener = (NavigationListener) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedState) {
        return inflater.inflate(R.layout.fragment_countdown, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedState) {
        super.onViewCreated(view, savedState);

        mViewPager.setAdapter(new PagerAdapter(getChildFragmentManager()));
        mViewPager.setOffscreenPageLimit(TimerFragment.layoutsCount());

        mLinearInterpolator = new LinearInterpolator();

        ViewTreeObserver vto = mScrollView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override public void onGlobalLayout() {
                mScrollView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                //noinspection ConstantConditions
                mBackgroundScrollWidth = mScrollView.getChildAt(0)
                        .getMeasuredWidth() / 2;
                //Timber.e("ScrollWidth", Integer.toString(mBackgroundScrollWidth));
                startBackgroundAnimation();
            }
        });

        if (savedState == null)
            mViewPager.setCurrentItem(1, false);

        updateName();
    }

    @Override
    public void onResume() {
        super.onResume();
        startBackgroundAnimation();
    }

    @Override
    public void onPause() {
        stopBackgroundAnimation();
        super.onPause();
    }

    @Override
    public void onDetach() {
        mListener = NavigationListener.DUMMY;
        super.onDetach();
    }

    @OnClick(R.id.personalise_button) void onPersonaliseClick() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setTitle(R.string.title_dialog_personalise);
        dialog.setContentView(R.layout.dialog_personalise);

        final EditText nameEditText = (EditText) dialog.findViewById(R.id.personalise_name);
        nameEditText.setText(PrefUtils.getPersonaliseName(getActivity()));

        dialog.findViewById(R.id.button_cancel).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.button_clear).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                nameEditText.setText(null);
            }
        });
        dialog.findViewById(R.id.button_ok).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                final String name = nameEditText.getText().toString();
                PrefUtils.setPersonaliseName(getActivity(), name);
                updateName();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @OnClick(R.id.button_settings) void onSettingsClick() {
        mListener.onSettingsClick();
    }

    @OnClick(R.id.button_share) void onShareClick() {
        mListener.onShareClick();
    }

    @Subscribe
    public void onTimeEvent(TimeEvent event) {
        final long secondsLeft = event.duration.getStandardSeconds();
        if (secondsLeft <= 60) {
            mViewPager.setPagingEnabled(false);
            mViewPager.setCurrentItem(0, true);
        }
    }

    private void startBackgroundAnimation() {
        Timber.e("startBackgroundAnimation=%d", mBackgroundScrollWidth);
        if (mBackgroundScrollWidth == 0) {
            return;
        }

        //noinspection ConstantConditions
        mCurrentAnimator = ValueAnimator.ofInt(0, mBackgroundScrollWidth);
        mCurrentAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override public void onAnimationUpdate(ValueAnimator animation) {
                int value = (Integer) animation.getAnimatedValue();
                if (mScrollView != null)
                    mScrollView.scrollTo(value, 0);
            }
        });
        mCurrentAnimator.addListener(new AnimatorListenerAdapter() {
            @Override public void onAnimationStart(Animator animation) {
                if (mScrollView != null)
                    mScrollView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
            }

            @Override public void onAnimationCancel(Animator animation) {
                Timber.w("onAnimationCancel");
                if (mScrollView != null)
                    mScrollView.setLayerType(View.LAYER_TYPE_NONE, null);
            }
        });
        mCurrentAnimator.setDuration(ANIMATION_DURATION);
        mCurrentAnimator.setRepeatCount(-1);
        mCurrentAnimator.setInterpolator(mLinearInterpolator);
        mCurrentAnimator.start();
    }

    private void stopBackgroundAnimation() {
        if (mCurrentAnimator != null) {
            mCurrentAnimator.removeAllUpdateListeners();
            mCurrentAnimator.cancel();
            mCurrentAnimator = null;
        }
    }

    private void updateName() {
        final String name = PrefUtils.getPersonaliseName(getActivity());
        mTitleView.setText(TextUtils.isEmpty(name)
                ? getString(R.string.countdown_title)
                : getString(R.string.personalise_name, name));
    }

    final class PagerAdapter extends FragmentStatePagerAdapter {
        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return TimerFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return TimerFragment.layoutsCount();
        }
    }
}
