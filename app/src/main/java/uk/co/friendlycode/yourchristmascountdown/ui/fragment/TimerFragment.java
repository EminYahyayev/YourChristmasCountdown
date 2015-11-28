package uk.co.friendlycode.yourchristmascountdown.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.PluralsRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import org.joda.time.Duration;
import org.joda.time.Period;

import butterknife.Bind;
import timber.log.Timber;
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

    @Nullable @Bind(R.id.countdown_sleeps_label) TextView mSleepsLabel;
    @Nullable @Bind(R.id.countdown_seconds_label) TextView mSecondsLabel;
    @Nullable @Bind(R.id.countdown_seconds_of_year_label) TextView mSecondsOfYearLabel;
    @Nullable @Bind(R.id.countdown_minutes_label) TextView mMinutesLabel;
    @Nullable @Bind(R.id.countdown_weeks_label) TextView mWeeksLabel;
    @Nullable @Bind(R.id.countdown_days_label) TextView mDaysLabel;
    @Nullable @Bind(R.id.countdown_hours_label) TextView mHoursLabel;
    @Nullable @Bind(R.id.countdown_months_label) TextView mMonthsLabel;

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

        updateTextView(mSecondsView, mSecondsLabel, R.plurals.countdown_seconds, period.getSeconds());
        updateTextView(mMinutesView, mMinutesLabel, R.plurals.countdown_minutes, period.getMinutes());
        updateTextView(mHoursView, mHoursLabel, R.plurals.countdown_hours, period.getHours());
        updateTextView(mDaysView, mDaysLabel, R.plurals.countdown_days, period.getDays());
        updateTextView(mWeeksView, mWeeksLabel, R.plurals.countdown_weeks, period.getWeeks());
        updateTextView(mMonthsView, mMonthsLabel, R.plurals.countdown_months, period.getMonths());
        updateTextView(mSleepsView, mSleepsLabel, R.plurals.countdown_sleeps, duration.getStandardDays() + 1);

        updateTextView(mSecondsOfYearView, mSecondsOfYearLabel,
                R.plurals.countdown_seconds_full, duration.getStandardSeconds());
        updateTextView(mHoursOfYearView, mHoursLabel,
                R.plurals.countdown_hours, duration.getStandardHours());
        updateTextView(mDaysOfYearView, mDaysLabel,
                R.plurals.countdown_days, duration.getStandardDays());
        updateTextView(mWeeksOfYearView, mWeeksLabel,
                R.plurals.countdown_weeks, weeksBetween(event.now, event.christmas).getWeeks());
    }

    private void updateTextView(@Nullable TextView textView, @Nullable TextView textLabel,
                                @PluralsRes int pluralsRes, long value) {
        if (textView != null) {
            String oldValue = textView.getText().toString();
            String newValue = String.valueOf(value);

            if (oldValue.equals(newValue))
                return;

            if (textLabel != null) {
                final int quantity = value == 1 ? 1 : 10;
                textLabel.setText(getResources().getQuantityText(pluralsRes, quantity));
            } else {
                Timber.w("Label is missing.");
            }

            textView.setText(newValue);
            animateTextView(textView);
        }
    }

    private void animateTextView(@NonNull TextView textView) {
        textView.startAnimation(AnimationUtils.loadAnimation(
                getActivity(), R.anim.countdown_scale_up_down));
    }
}
