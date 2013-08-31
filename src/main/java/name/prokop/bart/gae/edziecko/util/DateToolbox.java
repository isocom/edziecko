package name.prokop.bart.gae.edziecko.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateToolbox {

    public static Calendar getCalendarInstance() {
        Calendar myCal = Calendar.getInstance();
        myCal.setTimeZone(TimeZone.getTimeZone("CET"));
        return myCal;
    }

    /**
     * HH:mm
     *
     * @param format
     * @param date
     * @return formated date
     */
    public static String getFormatedDate(String format, Date date) {
        SimpleDateFormat dateFormater = new SimpleDateFormat(format);
        dateFormater.setTimeZone(TimeZone.getTimeZone("CET"));
        return dateFormater.format(date);
    }

    /**
     * Decodes formated date
     *
     * @param format date format
     * @param date as String
     * @return decoded date or null if unsuccessfull
     */
    public static Date parseDateChecked(String format, String date) {
        SimpleDateFormat dateFormater = new SimpleDateFormat(format);
        dateFormater.setTimeZone(TimeZone.getTimeZone("CET"));
        try {
            return dateFormater.parse(date);
        } catch (ParseException ex) {
            return null;
        }
    }

    public static Date getBeginingOfDay(Date date) {
        Calendar myCal = getCalendarInstance();
        myCal.setTime(date);
        myCal.set(Calendar.HOUR_OF_DAY, 0);
        myCal.set(Calendar.MINUTE, 0);
        myCal.set(Calendar.SECOND, 0);
        // System.out.println("getBeginingOfDay: " + myCal);
        // System.out.println("getBeginingOfDay: " + myCal.getTime());
        return myCal.getTime();
    }

    public static int getYear(Date date) {
        Calendar myCal = getCalendarInstance();
        myCal.setTime(date);
        return myCal.get(Calendar.YEAR);
    }

    public static int getMonth(Date date) {
        Calendar myCal = getCalendarInstance();
        myCal.setTime(date);
        return myCal.get(Calendar.MONTH) + 1;
    }

    public static Date getBeginingOfDay(Date date, int secondsDelta) {
        Calendar myCal = getCalendarInstance();
        myCal.setTime(date);
        myCal.set(Calendar.HOUR_OF_DAY, 0);
        myCal.set(Calendar.MINUTE, 0);
        myCal.set(Calendar.SECOND, 0);
        myCal.add(Calendar.SECOND, secondsDelta);
        // System.out.println("getBeginingOfDay: " + myCal);
        // System.out.println("getBeginingOfDay: " + myCal.getTime());
        return myCal.getTime();
    }

    public static Date getEndOfDay(Date date) {
        Calendar myCal = getCalendarInstance();
        myCal.setTime(date);
        myCal.set(Calendar.HOUR_OF_DAY, 23);
        myCal.set(Calendar.MINUTE, 59);
        myCal.set(Calendar.SECOND, 59);
        myCal.set(Calendar.MILLISECOND, 999);

        return myCal.getTime();
    }

    public static int getDayNumber(Date date) {
        Calendar myCal = getCalendarInstance();
        myCal.setTime(date);
        return myCal.get(Calendar.DAY_OF_MONTH);
    }

    public static String seconds2String(int seconds) {
        int s = seconds % 60;
        int m = (seconds % 3600) / 60;
        int h = seconds / 3600;
        return h + "h " + m + "m " + s + "s";
    }

    /**
     * Converts string to date
     *
     * @param d date as YYYYMMDD
     * @return
     */
    public static Date encodeDate(String d) {
        SimpleDateFormat dateFormater = new SimpleDateFormat("yyyyMMdd");
        try {
            dateFormater.setTimeZone(TimeZone.getTimeZone("CET"));
            return dateFormater.parse(d);
        } catch (java.text.ParseException e) {
            return null;
        }
    }

    public static Date getBeginningOfMonth(int y, int m) {
        Calendar myCal = getCalendarInstance();
        myCal.clear();
        myCal.set(Calendar.YEAR, y);
        myCal.set(Calendar.MONTH, m);
        return myCal.getTime();
    }

    public static Date getEndOfMonth(int y, int m) {
        Calendar myCal = getCalendarInstance();
        myCal.clear();
        myCal.set(Calendar.YEAR, y);
        myCal.set(Calendar.MONTH, m);
        myCal.add(Calendar.MONTH, 1);
        myCal.add(Calendar.SECOND, -1);
        return myCal.getTime();
    }

    public static void main(String[] argv) {
        System.out.println(getBeginningOfMonth(2011, 11));
        System.out.println(getEndOfMonth(2011, 11));
        System.out.println(getBeginingOfDay(new Date()));
        System.out.println(getBeginingOfDay(new Date(), 3600 * 4));
        // System.out.println(getEndOfDay(new Date()));
        // System.out.println(getFormatedDate("yyyy-M-d H:m:s", new Date()));
        // System.out.println(parseDateChecked("yyyy-M-d H:m:s",
        // "2081-1-2 1:2:3"));
        // System.out.println(DateToolbox.encodeDate("21000801"));
        System.out.println(DateToolbox.getFormatedDate("yyyyMM", new Date()));
    }

    public static Date convert4Excel(Date date) {
        final String format = "dd MM yyyy";
        String formatedDate = getFormatedDate(format, date);
        SimpleDateFormat dateFormater = new SimpleDateFormat(format);
        dateFormater.setTimeZone(TimeZone.getTimeZone("GMT"));
        try {
            return dateFormater.parse(formatedDate);
        } catch (ParseException pe) {
            return null;
        }
    }
}
