package uk.co.friendlycode.yourchristmascountdown.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import org.joda.time.Duration;
import org.joda.time.Period;

import butterknife.Bind;
import uk.co.friendlycode.yourchristmascountdown.R;
import uk.co.friendlycode.yourchristmascountdown.ui.event.TimeEvent;

import static org.joda.time.Weeks.weeksBetween;

public final class TimerFragment extends BaseFragment {
    private static final String ARG_POSITION = "arg_position";

    private static final int[] LAYOUTS = {
            R.layout.fragment_timer_seconds,
            R.layout.fragment_timer_hours,
            R.layout.fragment_timer_sleeps,
            R.layout.fragment_timer_days,
            R.layout.fragment_timer_weeks,
            R.layout.fragment_timer_months
    };

    @Nullable @Bind(R.id.countdown_sleeps) TextView mSleepsView;
    @Nullable @Bind(R.id.countdown_seconds) TextView mSecondsView;
    @Nullable @Bind(R.id.countdown_minutes) TextView mMinutesView;
    @Nullable @Bind(R.id.countdown_hours) TextView mHoursView;
    @Nullable @Bind(R.id.countdown_days) TextView mDaysView;
    @Nullable @Bind(R.id.countdown_weeks) TextView mWeeksView;
    @Nullable @Bind(R.id.countdown_months) TextView mMonthsView;
    @Nullable @Bind(R.id.countdown_seconds_of_year) TextView mSecondsOfYearView;
    @Nullable @Bind(R.id.countdown_hours_of_year) TextView mHoursOfYearView;
    @Nullable @Bind(R.id.countdown_days_of_year) TextView mDaysOfYearView;
    @Nullable @Bind(R.id.countdown_weeks_of_year) TextView mWeeksOfYearView;

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

    @Subscribe
    public void onTimeEvent(TimeEvent event) {
        final Period period = event.period;
        final Duration duration = event.duration;

        tryUpdateTextView(mSecondsView, period.getSeconds());
        tryUpdateTextView(mMinutesView, period.getMinutes());
        tryUpdateTextView(mHoursView, period.getHours());
        tryUpdateTextView(mDaysView, period.getDays());
        tryUpdateTextView(mWeeksView, event.period.getWeeks());
        tryUpdateTextView(mMonthsView, event.period.getMonths());
        tryUpdateTextView(mSleepsView, event.duration.getStandardDays() + 1);
        tryUpdateTextView(mSecondsOfYearView, event.duration.getStandardSeconds());
        tryUpdateTextView(mHoursOfYearView, event.duration.getStandardHours());
        tryUpdateTextView(mDaysOfYearView, event.duration.getStandardDays());
        tryUpdateTextView(mWeeksOfYearView, weeksBetween(event.now, event.christmas).getWeeks());
    }

    private void tryUpdateTextView(@Nullable TextView textView, long value) {
        if (textView != null) {
            String oldValue = textView.getText().toString();
            String newValue = String.valueOf(value);

            if (oldValue.equals(newValue))
                return;

            textView.setText(newValue);
            animateTextView(textView);
        }
    }

    private void animateTextView(@NonNull TextView textView) {
        textView.startAnimation(AnimationUtils.loadAnimation(
                getActivity(), R.anim.countdown_scale_up_down));
    }
}
