package db;

import android.text.format.DateFormat;

import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;

/**
 * Created by tabunshikov.vadim on 19.03.2015.
 */
public class Stat {
    Date date;
    NumberFormat formatter;
    DecimalFormat dformatter;
    public Stat() {
        date = new Date();
        dformatter = new DecimalFormat("#.00");
        formatter = NumberFormat.getNumberInstance(new Locale("ru", "RU"));
    }

    public long getLastDate() {
        return lastDate;
    }

    public String getLastDateString() {
        DateFormat df = new DateFormat();
        return df.format("dd.MM.yy", this.lastDate).toString();
    }

    public void setLastDate(long lastDate) {
        this.lastDate = lastDate;
    }

    public float getLastRate() {
        return lastRate;
    }

    public String getLastRateString() {
        return dformatter.format(lastRate).toString();//"lit/100km"
    }

    public void setLastRate(float lastRate) {
        this.lastRate = lastRate;
    }

    public float getLastQty() {
        return lastQty;
    }

    public String getLastQtyString() {
        return dformatter.format(lastQty).toString();// + " " + "lit";
    }

    public void setLastQty(float lastQty) {
        this.lastQty = lastQty;
    }

    public float getLastTrip() {
        return lastTrip;
    }

    public String getLastTripString() {
        return dformatter.format(lastTrip).toString();// + " " + "km";
    }

    public void setLastTrip(float lastTrip) {
        this.lastTrip = lastTrip;
    }

    public float getPMonthRate() {
        return pmonthRate;
    }

    public String getPMonthRateString() {
        return dformatter.format(pmonthRate).toString();//+ " "+ "lit/100km";
    }

    public void setPMonthRate(float pmonthRate) {
        this.pmonthRate = pmonthRate;
    }

    public float getPMonthQty() {
        return pmonthQty;
    }

    public String getPMonthQtyString() {
        return dformatter.format(pmonthQty).toString();// + " " + "lit";
    }

    public void setPMonthQty(float pmonthQty) {
        this.pmonthQty = pmonthQty;
    }

    public float getPMonthTrip() {
        return pmonthTrip;
    }

    public String getPMonthTripString() {
        return dformatter.format(pmonthTrip).toString();// + " " + "km";
    }
    public void setPMonthTrip(float pmonthTrip) {
        this.pmonthTrip = pmonthTrip;
    }

    public float getMonthRate() {
        return monthRate;
    }

    public String getMonthRateString() {
        return dformatter.format(monthRate).toString();//+ " "+ "lit/100km";
    }

    public void setMonthRate(float monthRate) {
        this.monthRate = monthRate;
    }

    public float getMonthQty() {
        return monthQty;
    }

    public String getMonthQtyString() {
        return dformatter.format(monthQty).toString();// + " " + "lit";
    }

    public void setMonthQty(float monthQty) {
        this.monthQty = monthQty;
    }

    public float getMonthTrip() {
        return monthTrip;
    }

    public String getMonthTripString() {
        return dformatter.format(monthTrip).toString();// + " " + "km";
    }
    public void setMonthTrip(float monthTrip) {
        this.monthTrip = monthTrip;
    }


    long lastDate=0;
    float lastRate=0;
    float lastQty=0;
    float lastTrip=0;

    float monthRate=0;
    float monthQty=0;
    float monthTrip=0;

    float pmonthRate=0;
    float pmonthQty=0;
    float pmonthTrip=0;
}
