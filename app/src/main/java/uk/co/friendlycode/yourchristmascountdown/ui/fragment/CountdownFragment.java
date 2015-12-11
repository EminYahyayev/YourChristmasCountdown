package uk.co.friendlycode.yourchristmascountdown.ui.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.PluralsRes;
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

import com.squareup.otto.Subscribe;

import org.joda.time.Duration;
import org.joda.time.Period;

import butterknife.Bind;
import butterknife.OnClick;
import timber.log.Timber;
import uk.co.friendlycode.yourchristmascountdown.R;
import uk.co.friendlycode.yourchristmascountdown.ui.event.TimeEvent;
import uk.co.friendlycode.yourchristmascountdown.ui.listener.NavigationListener;
import uk.co.friendlycode.yourchristmascountdown.ui.widget.AutoResizeTextView;
import uk.co.friendlycode.yourchristmascountdown.ui.widget.ChristmasViewPager;
import uk.co.friendlycode.yourchristmascountdown.utils.PrefUtils;

import static org.joda.time.Weeks.weeksBetween;

public final class CountdownFragment extends BaseFragment {

    private static final int ANIMATION_DURATION = 16000;

    @Bind(R.id.view_pager) ChristmasViewPager mViewPager;
    @Bind(R.id.countdown_title_container) ViewGroup mTitleContainer;
    @Bind(R.id.countdown_title) AutoResizeTextView mTitleView;
    @Bind(R.id.background_view) HorizontalScrollView mScrollView;

    private ValueAnimator mCurrentAnimator;
    private LinearInterpolator mLinearInterpolator;

    private NavigationListener mListener = NavigationListener.DUMMY;
    private int mBackgroundScrollWidth;
    private PagerAdapter mPagerAdapter;
    private TimeEvent mLastTimeEvent;

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

        mPagerAdapter = new PagerAdapter(getChildFragmentManager());
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setOffscreenPageLimit(TimerFragment.getLayoutsCount());

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

        mTitleView.setEnableSizeCache(false);
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
        final String message = getString(R.string.share_template,
                getShareMessage(mViewPager.getCurrentItem()),
                getString(R.string.url_play_market));
        mListener.onShareClick(message);
    }

    @Subscribe
    public void onTimeEvent(TimeEvent event) {
        mLastTimeEvent = event;
        final long secondsLeft = event.duration.getStandardSeconds();
        if (secondsLeft <= 60) {
            mViewPager.setPagingEnabled(false);
            mViewPager.setCurrentItem(0, true);
        }
    }

    private void startBackgroundAnimation() {
        Timber.i("startBackgroundAnimation=%d", mBackgroundScrollWidth);
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
        final String text = TextUtils.isEmpty(name)
                ? getString(R.string.countdown_title).toUpperCase()
                : getString(R.string.personalise_name, name).toUpperCase();

        mTitleView.setText(text);
    }

    private String getShareMessage(final int position) {
        if (mLastTimeEvent == null) {
            Timber.w("mLastTimeEvent == null, returning default message");
            return getString(R.string.share_countdown_default);
        }

        final TimeEvent event = mLastTimeEvent;
        final Period period = event.period;
        final Duration duration = event.duration;
        long quantity;

        switch (TimerFragment.MESSAGE_STRINGS[position]) {
            // Seconds
            case R.string.share_countdown_seconds:
                quantity = duration.getStandardSeconds();
                return getString(R.string.share_countdown_seconds, quantity,
                        getQuantityText(R.plurals.countdown_seconds_full, quantity));
            // Hours
            case R.string.share_countdown_hours:
                quantity = duration.getStandardHours();
                return getString(R.string.share_countdown_hours, quantity,
                        getQuantityText(R.plurals.countdown_hours, quantity));
            // Sleeps
            case R.string.share_countdown_sleeps:
                quantity = duration.getStandardDays() + 1;
                return getString(R.string.share_countdown_sleeps, quantity,
                        getQuantityText(R.plurals.countdown_sleeps, quantity));
            // Days
            case R.string.share_countdown_days:
                quantity = duration.getStandardDays();
                return getString(R.string.share_countdown_days, quantity,
                        getQuantityText(R.plurals.countdown_days, quantity));
            // Weeks
            case R.string.share_countdown_weeks:
                quantity = weeksBetween(event.now, event.christmas).getWeeks();
                return getString(R.string.share_countdown_weeks, quantity,
                        getQuantityText(R.plurals.countdown_weeks, quantity));
            // Months
            case R.string.share_countdown_months:
                quantity = period.getMonths();
                return getString(R.string.share_countdown_months, quantity,
                        getQuantityText(R.plurals.countdown_months, quantity));
            default:
                throw new UnsupportedOperationException("Unknown string resource.");
        }
    }

    private CharSequence getQuantityText(@PluralsRes int id, long quantity) {
        final int value = quantity == 1 ? 1 : 10;
        return getResources().getQuantityText(id, value);
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
            return TimerFragment.getLayoutsCount();
        }
    }
}
