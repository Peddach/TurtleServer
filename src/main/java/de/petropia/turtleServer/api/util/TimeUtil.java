package de.petropia.turtleServer.api.util;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

public class TimeUtil {

    /**
     * Convert a unix timestamp to a human-readable string in format dd.MM.yyyy HH:mm
     * @param timestamp Unix timestamp second of epoche
     * @return Human-readable String (dd.MM.yyyy HH:mm)
     */
    public static String unixTimestampToString(int timestamp){
        Date date = Date.from(Instant.ofEpochSecond(timestamp));
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        return simpleDateFormat.format(date);
    }
}
