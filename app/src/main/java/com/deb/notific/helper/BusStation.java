package com.deb.notific.helper;

import com.squareup.otto.Bus;

public class BusStation {
    private static Bus bus = new Bus();

    public static Bus getBus() {
        return bus;
    }
}
