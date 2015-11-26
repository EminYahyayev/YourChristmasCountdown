package uk.co.friendlycode.yourchristmascountdown.ui.event;


import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Period;

public final class TimeEvent {
    public DateTime now;
    public DateTime christmas;
    public Duration duration;
    public Period period;

    public TimeEvent(DateTime christmas) {
        this(DateTime.now(), christmas);
    }

    public TimeEvent(DateTime now, DateTime christmas) {
        this.now = now;
        this.christmas = christmas;
        duration = new Duration(now, christmas);
        period = new Period(now, christmas);
    }
}
