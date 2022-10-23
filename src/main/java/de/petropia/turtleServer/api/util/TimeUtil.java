package de.petropia.turtleServer.api.util;

import net.kyori.adventure.text.Component;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.concurrent.TimeUnit;

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

    public static String formatSeconds(int seconds) {
        int day = (int) TimeUnit.SECONDS.toDays(seconds);
        long hours = TimeUnit.SECONDS.toHours(seconds) - (day * 24);
        long minute = TimeUnit.SECONDS.toMinutes(seconds) - (TimeUnit.SECONDS.toHours(seconds) * 60);
        String format = "";
        if (day == 1) {
            format += day + " Tag ";
        }
        if (day > 1) {
            format += day + " Tage ";
        }
        if (hours == 1) {
            format += hours + " Stunde ";
        }
        if (hours > 1) {
            format += hours + " Stunden ";
        }
        if (minute == 1) {
            format += minute + " Miunute ";
        }
        if (minute > 1) {
            format += minute + " Minuten ";
        }
        return format;
    }
}
