package uk.co.friendlycode.yourchristmascountdown.utils;

import com.squareup.otto.Bus;


public final class BusProvider {
    private static final Bus BUS = new Bus();

    public static Bus getInstance() {
        return BUS;
    }

    private BusProvider() {
        // No instances.
    }
}
