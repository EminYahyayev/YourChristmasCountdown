package uk.co.friendlycode.yourchristmascountdown.utils;


import android.support.annotation.NonNull;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Period;
import org.joda.time.Weeks;

import static org.joda.time.DateTimeConstants.DECEMBER;

public final class TimeUtils {

    public static final DateTime CHRISTMAS_DATE;

    static {
        final DateTime now = DateTime.now();
        final int christmasYear = (now.getDayOfMonth() >= 25 && now.getMonthOfYear() == DECEMBER)
                ? now.getYear() + 1
                : now.getYear();

        CHRISTMAS_DATE = new DateTime(christmasYear, DECEMBER, 25, 0, 0, 0);
    }

    public static TimeModel modelFromNow() {
        return modelFromDate(DateTime.now());
    }

    public static TimeModel modelFromDate(@NonNull final DateTime date) {
        final Period period = new Period(date, CHRISTMAS_DATE);
        final Duration duration = new Duration(date, CHRISTMAS_DATE);

        //Timber.w("Period");
        //Timber.w("hours=%d, minutes=%d, secs=%d", period.getHours(), period.getMinutes(), period.getSeconds());
        //Timber.w("days=%d, weeks=%d, months=%d", period.getDays(), period.getWeeks(), period.getMonths());

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

    public static final class TimeModel {
        private int sleepsLeft;
        private int secondsLeft;
        private int minutesLeft;
        private int hoursLeft;
        private int daysLeft;
        private int weeksLeft;
        private int monthsLeft;
        private long hoursOfYearLeft;
        private long daysOfYearLeft;
        private int weeksOfYearLeft;

        public int getSecondsLeft() {
            return secondsLeft;
        }

        public TimeModel setSecondsLeft(int secondsLeft) {
            this.secondsLeft = secondsLeft;
            return this;
        }

        public int getMinutesLeft() {
            return minutesLeft;
        }

        public TimeModel setMinutesLeft(int minutesLeft) {
            this.minutesLeft = minutesLeft;
            return this;
        }

        public int getHoursLeft() {
            return hoursLeft;
        }

        public TimeModel setHoursLeft(int hoursLeft) {
            this.hoursLeft = hoursLeft;
            return this;
        }

        public int getDaysLeft() {
            return daysLeft;
        }

        public TimeModel setDaysLeft(int daysLeft) {
            this.daysLeft = daysLeft;
            return this;
        }

        public int getWeeksLeft() {
            return weeksLeft;
        }

        public TimeModel setWeeksLeft(int weeksLeft) {
            this.weeksLeft = weeksLeft;
            return this;
        }

        public int getMonthsLeft() {
            return monthsLeft;
        }

        public TimeModel setMonthsLeft(int monthsLeft) {
            this.monthsLeft = monthsLeft;
            return this;
        }

        public long getHoursOfYearLeft() {
            return hoursOfYearLeft;
        }

        public TimeModel setHoursOfYearLeft(long hoursOfYearLeft) {
            this.hoursOfYearLeft = hoursOfYearLeft;
            return this;
        }

        public long getDaysOfYearLeft() {
            return daysOfYearLeft;
        }

        public TimeModel setDaysOfYearLeft(long daysOfYearLeft) {
            this.daysOfYearLeft = daysOfYearLeft;
            return this;
        }

        public int getWeeksOfYearLeft() {
            return weeksOfYearLeft;
        }

        public TimeModel setWeeksOfYearLeft(int weeksOfYearLeft) {
            this.weeksOfYearLeft = weeksOfYearLeft;
            return this;
        }

        @Override public String toString() {
            return "TimeModel{" +
                    "secondsLeft=" + secondsLeft +
                    ", minutesLeft=" + minutesLeft +
                    ", hoursLeft=" + hoursLeft +
                    ", daysLeft=" + daysLeft +
                    ", weeksLeft=" + weeksLeft +
                    ", monthsLeft=" + monthsLeft +
                    ", hoursOfYearLeft=" + hoursOfYearLeft +
                    ", daysOfYearLeft=" + daysOfYearLeft +
                    ", weeksOfYearLeft=" + weeksOfYearLeft +
                    '}';
        }
    }

    private TimeUtils() {
        throw new AssertionError("No instances.");
    }
}
