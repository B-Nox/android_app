package it.droidcon.b_nox.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by demiurgo on 4/11/15.
 */

public class Constants {

    final public static List<String> BEACONS = new ArrayList<>(2);

    static {
        BEACONS.add("00:07:80:20:02:0E");
        BEACONS.add("00:07:80:20:01:A0");
    }

    public static final String SERVER_ADDRESS = "http://192.168.23.254";
}
