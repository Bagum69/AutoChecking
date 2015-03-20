package db;

import android.text.format.DateFormat;

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
    public Stat() {
        date = new Date();
        formatter = NumberFormat.getNumberInstance(new Locale("ru", "RU"));
    }

    public long getLastDate() {
        return lastDate;
    }

    public String getLastDateString() {
        DateFormat df = new DateFormat();
        return df.format("dd.MM.yyyy", this.lastDate).toString();
    }

    public void setLastDate(long lastDate) {
        this.lastDate = lastDate;
    }

    public float getLastRate() {
        return lastRate;
    }

    public String getLastRateString() {
        return formatter.format(lastRate) + " "+ "lit/100km";
    }

    public void setLastRate(float lastRate) {
        this.lastRate = lastRate;
    }

    public float getLastQty() {
        return lastQty;
    }

    public String getLastQtyString() {
        return formatter.format(lastQty) + " " + "lit";
    }

    public void setLastQty(float lastQty) {
        this.lastQty = lastQty;
    }

    public float getLastTrip() {
        return lastTrip;
    }

    public String getLastTripString() {
        return formatter.format(lastTrip) + " " + "km";
    }

    public void setLastTrip(float lastTrip) {
        this.lastTrip = lastTrip;
    }

    public float getPMonthRate() {
        return pmonthRate;
    }

    public String getPMonthRateString() {
        return formatter.format(pmonthRate) + " "+ "lit/100km";
    }

    public void setPMonthRate(float pmonthRate) {
        this.pmonthRate = pmonthRate;
    }

    public float getPMonthQty() {
        return pmonthQty;
    }

    public String getPMonthQtyString() {
        return formatter.format(pmonthQty) + " " + "lit";
    }

    public void sePtMonthQty(float pmonthQty) {
        this.pmonthQty = pmonthQty;
    }

    public float getPMonthTrip() {
        return pmonthTrip;
    }

    public String getPMonthTripString() {
        return formatter.format(pmonthTrip) + " " + "km";
    }
    public void setPMonthTrip(float pmonthTrip) {
        this.monthTrip = pmonthTrip;
    }

    public float getMonthRate() {
        return monthRate;
    }

    public String getMonthRateString() {
        return formatter.format(monthRate) + " "+ "lit/100km";
    }

    public void setMonthRate(float monthRate) {
        this.monthRate = monthRate;
    }

    public float getMonthQty() {
        return monthQty;
    }

    public String getMonthQtyString() {
        return formatter.format(monthQty) + " " + "lit";
    }

    public void setMonthQty(float monthQty) {
        this.monthQty = monthQty;
    }

    public float getMonthTrip() {
        return monthTrip;
    }

    public String getMonthTripString() {
        return formatter.format(monthTrip) + " " + "km";
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
