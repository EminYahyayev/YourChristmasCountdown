package uk.co.friendlycode.yourchristmascountdown.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import butterknife.Bind;
import timber.log.Timber;
import uk.co.friendlycode.yourchristmascountdown.R;
import uk.co.friendlycode.yourchristmascountdown.ui.widget.ChristmasTextView;
import uk.co.friendlycode.yourchristmascountdown.utils.TimeModel;
import uk.co.friendlycode.yourchristmascountdown.utils.TimeUtils;

public final class TimerFragment extends BaseFragment {
    private static final String ARG_POSITION = "arg_position";

    private static final int[] LAYOUTS = {
            R.layout.fragment_timer_sleeps,
            R.layout.fragment_timer_hours,
            R.layout.fragment_timer_days,
            R.layout.fragment_timer_weeks,
            R.layout.fragment_timer_months
    };

    @Nullable @Bind(R.id.countdown_sleeps) ChristmasTextView mSleepsView;
    @Nullable @Bind(R.id.countdown_seconds) ChristmasTextView mSecondsView;
    @Nullable @Bind(R.id.countdown_minutes) ChristmasTextView mMinutesView;
    @Nullable @Bind(R.id.countdown_hours) ChristmasTextView mHoursView;
    @Nullable @Bind(R.id.countdown_days) ChristmasTextView mDaysView;
    @Nullable @Bind(R.id.countdown_weeks) ChristmasTextView mWeeksView;
    @Nullable @Bind(R.id.countdown_months) ChristmasTextView mMonthsView;
    @Nullable @Bind(R.id.countdown_days_of_year) ChristmasTextView mDaysOfYearView;
    @Nullable @Bind(R.id.countdown_hours_of_year) ChristmasTextView mHoursOfYearView;
    @Nullable @Bind(R.id.countdown_weeks_of_year) ChristmasTextView mWeeksOfYearView;

    private String mLogTag;

    public TimerFragment() {}

    public static int layoutsCount() {
        return LAYOUTS.length;
    }

    public static TimerFragment newInstance(final int position) {
        Bundle args = new Bundle();
        args.putInt(ARG_POSITION, position);

        TimerFragment fragment = new TimerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final int position = getArguments().getInt(ARG_POSITION);
        mLogTag = TimerFragment.class.getSimpleName().concat("-" + position);

        return inflater.inflate(LAYOUTS[position], container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateTimeContent(TimeUtils.modelFromNow());
    }

    public void updateTimeContent(@NonNull final TimeModel model) {
        Timber.tag(mLogTag).v("updateTimeContent(model=%s)", model.toString());

        if (isDetached()) {
            Timber.tag(mLogTag).w("fragment is detached, skipping update");
            return;
        }

        tryUpdateTextView(mSleepsView, model.getDaysOfYearLeft() + 1);
        tryUpdateTextView(mSecondsView, model.getSecondsLeft());
        tryUpdateTextView(mMinutesView, model.getMinutesLeft());
        tryUpdateTextView(mHoursView, model.getHoursLeft());
        tryUpdateTextView(mHoursOfYearView, model.getHoursOfYearLeft());
        tryUpdateTextView(mDaysView, model.getDaysLeft());
        tryUpdateTextView(mDaysOfYearView, model.getDaysOfYearLeft());
        tryUpdateTextView(mWeeksView, model.getWeeksLeft());
        tryUpdateTextView(mWeeksOfYearView, model.getWeeksOfYearLeft());
        tryUpdateTextView(mMonthsView, model.getMonthsLeft());
    }

    private void tryUpdateTextView(@Nullable TextView textView, final long value) {
        if (textView != null) {
            String oldValue = textView.getText().toString();
            String newValue = String.valueOf(value);

            if (!oldValue.equals(newValue)) {
                textView.setText(newValue);
                animateTextView(textView);
            }
        }
    }

    private void animateTextView(@NonNull TextView textView) {
        textView.startAnimation(AnimationUtils.loadAnimation(
                getActivity(), R.anim.countdown_scale_up_down));
    }
}
