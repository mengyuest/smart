package data; /**
 * Created by dynamit on 7/28/16.
 */
import java.text.DecimalFormat;

public class MYtime {
    private int YEAR;
    private int MONTH;
    private int DAY;
    private int HOUR;
    private int MINUTE;
    private int SECOND;

    public void getDate(String timeStr) {
        String[] seg = timeStr.split(":");
        HOUR = Integer.parseInt(seg[0]);
        MINUTE = Integer.parseInt(seg[1]);
        SECOND = Integer.parseInt(seg[2]);
    }

    public void getDate(int year, int month, int day, int hour, int minute, int second) {
        YEAR = year;
        MONTH = month;
        DAY = day;
        HOUR = hour;
        MINUTE = minute;
        SECOND = second;
    }

    public void getDate(int hour, int minute, int second) {
        YEAR = 2016;
        MONTH = 7;
        DAY = 4;
        HOUR = hour;
        MINUTE = minute;
        SECOND = second;
    }

    public void addSec(int second) {
        int incToMinute = 0;
        int incToHour = 0;
        int incToDay = 0;
        int incToMonth = 0;
        int incToYear = 0;

        SECOND = SECOND + second % 60;
        incToMinute = second / 60;
        if (SECOND >= 60) {
            SECOND = SECOND % 60;
            incToMinute++;
        }

        MINUTE = MINUTE + incToMinute % 60;
        incToHour = incToMinute / 60;
        if (MINUTE >= 60) {
            MINUTE = MINUTE % 60;
            incToHour++;
        }

        HOUR = HOUR + incToHour % 24;
        incToDay = incToHour / 24;
        if (HOUR >= 24) {
            HOUR = HOUR % 24;
            incToDay++;
        }

        //TODO DAY, MONTH, YEAR operation

    }

    public int getSECOND() {
        return SECOND;
    }

    public int getMINUTE() {
        return MINUTE;
    }

    public int getHOUR() {
        return HOUR;
    }

    public int getDAY() {
        return DAY;
    }

    public int getMONTH() {
        return MONTH;
    }

    public int getYEAR() {
        return YEAR;
    }

    public int getAbsoluteSecond() {
        return HOUR * 3600 + MINUTE * 60 + SECOND;
    }

    public static int getDeltaSec(MYtime t1, MYtime t2) {
        int len_t1 = t1.getHOUR() * 3600 + t1.getMINUTE() * 60 + t1.getSECOND();
        int len_t2 = t2.getHOUR() * 3600 + t2.getMINUTE() * 60 + t2.getSECOND();

        return len_t1 - len_t2;

        //TODO: CALCULATE WITH DAY,MONTH and YEAR
    }

    public static String generateDate(int second, String splitStr){
        int _SECOND = second%60;
        int _MINUTE = second/60%60;
        int _HOUR = second/3600%24;
        return String.format("%s%s%s%s%s",
                new DecimalFormat("00").format(_HOUR),splitStr,
                new DecimalFormat("00").format(_MINUTE),splitStr,
                new DecimalFormat("00").format(_SECOND));
    }

    public static int generateSeconds(String timeStr, String splitStr){
        String[] seg = timeStr.split(splitStr);
        return Integer.parseInt(seg[0])*3600+Integer.parseInt(seg[1])*60+Integer.parseInt(seg[2]);
    }

    @Override
    public String toString()
    {
        String yearStr = new DecimalFormat("0000").format(YEAR);
        String monthStr = new DecimalFormat("00").format(MONTH);
        String dayStr = new DecimalFormat("00").format(DAY);
        String hourStr = new DecimalFormat("00").format(HOUR);
        String minuteStr = new DecimalFormat("00").format(MINUTE);
        String secondStr = new DecimalFormat("00").format(SECOND);
        return String.format("MYT: %s-%s-%s %s:%s:%s",yearStr,monthStr,dayStr,hourStr,minuteStr,secondStr);
    }


    public static int[] getDeltaFromSec(MYtime t1, MYtime t2){
        int [] array = new int[3];
        int deltaSEC= getDeltaSec(t1, t2);
        array[0]=deltaSEC/3600;
        array[1]=deltaSEC%3600/60;
        array[2]=deltaSEC%60;

        return array;
        //TODO: CALCULATE WITH DAY, MONTH and YEAR
    }

    public static void main(String[] args){
        MYtime t1 = new MYtime();
        MYtime t2 = new MYtime();
        MYtime t3 = new MYtime();
        t1.getDate(2016,7,4,20,0,0);
        t2.getDate(2016,7,4,23,45,56);
        System.out.println(MYtime.getDeltaSec(t2,t1));

        t3 = t1;
        t3.addSec(1000);

        System.out.println(String.format("t1:%s t2:%s t3:%s", t1.toString(),t2.toString(),t3.toString()));
    }

}
