package uk.co.friendlycode.yourchristmascountdown.utils;

public final class TimeModel {
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
