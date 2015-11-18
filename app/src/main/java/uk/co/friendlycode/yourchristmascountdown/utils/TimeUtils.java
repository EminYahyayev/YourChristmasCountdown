package uk.co.friendlycode.yourchristmascountdown.utils;


import android.support.annotation.IntDef;
import android.support.annotation.NonNull;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.Duration;
import org.joda.time.Period;
import org.joda.time.Weeks;

import timber.log.Timber;

import static org.joda.time.DateTimeConstants.DECEMBER;

public final class TimeUtils {

    @IntDef({STATE_EMPTY,
            STATE_COUNTDOWN,
            STATE_FINAL_COUNTDOWN,
            STATE_HOLIDAY})
    public @interface State {}

    public static final int STATE_EMPTY = -11;
    public static final int STATE_COUNTDOWN = 1;
    public static final int STATE_FINAL_COUNTDOWN = 2;
    public static final int STATE_HOLIDAY = 3;

    public static final DateTime CHRISTMAS_DATE;
    public static final DateTime POST_CHRISTMAS_DATE;

    static {
        CHRISTMAS_DATE = getChristmasDate();
        POST_CHRISTMAS_DATE = CHRISTMAS_DATE.plusDays(1);
    }

    @State
    public static int getState(@NonNull final DateTime date) {
        final Duration duration = new Duration(date, POST_CHRISTMAS_DATE);
        final long secondsLeft = duration.getStandardSeconds();
        Timber.d("getCurrentState: secondsLEft=%d", secondsLeft);

        if (secondsLeft > 60 + DateTimeConstants.SECONDS_PER_DAY) {
            return STATE_COUNTDOWN;
        } else if (secondsLeft > 0) {
            return STATE_FINAL_COUNTDOWN;
        } else
            return STATE_HOLIDAY;
    }

    public static TimeModel modelFromNow() {
        return getTimeModel(DateTime.now());
    }

    public static TimeModel getTimeModel(@NonNull final DateTime date) {
        final Period period = new Period(date, CHRISTMAS_DATE);
        final Duration duration = new Duration(date, CHRISTMAS_DATE);

        final int secondsLeft = period.getSeconds();
        final int minutesLeft = period.getMinutes();
        final int hoursLeft = period.getHours();
        final int daysLeft = period.getDays();
        final int weeksLeft = period.getWeeks();
        final int monthsLeft = period.getMonths();
        final long hoursOfYearLeft = duration.getStandardHours();
        final long daysOfYearLeft = duration.getStandardDays();
        final int weeksOfYearLeft = Weeks.weeksBetween(date, CHRISTMAS_DATE).getWeeks();

        return new TimeModel()
                .setSecondsLeft(secondsLeft)
                .setMinutesLeft(minutesLeft)
                .setHoursLeft(hoursLeft)
                .setDaysLeft(daysLeft)
                .setWeeksLeft(weeksLeft)
                .setMonthsLeft(monthsLeft)
                .setHoursOfYearLeft(hoursOfYearLeft)
                .setDaysOfYearLeft(daysOfYearLeft)
                .setWeeksOfYearLeft(weeksOfYearLeft);
    }

    private static DateTime getChristmasDate() {
        final DateTime now = DateTime.now();
        final int christmasYear = (now.getDayOfMonth() >= 25 && now.getMonthOfYear() == DECEMBER)
                ? now.getYear() + 1
                : now.getYear();

        return new DateTime(christmasYear, DECEMBER, 25, 0, 0, 0);
    }

    private TimeUtils() {
        throw new AssertionError("No instances.");
    }
}
