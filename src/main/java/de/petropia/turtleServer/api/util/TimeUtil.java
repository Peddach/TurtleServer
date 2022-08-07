package de.petropia.turtleServer.api.util;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

public class TimeUtil {

    public static String unixTimestampToString(int timestamp){
        Date date = Date.from(Instant.ofEpochSecond(timestamp));
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm");
        return simpleDateFormat.format(date);
    }
}
