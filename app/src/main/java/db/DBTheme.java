package db;

/**
 * Created by tabunshikov.vadim on 03.03.2015.
 */

import android.provider.BaseColumns;
import android.text.format.DateFormat;

import java.text.NumberFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DBTheme {
    public static class Autos {
        public static final String DEFAULT_SORT = AutosColumns.MARK + " DESC";
        public static final String TABLE_CONT = "Autos"; //имя таблицы
        private long id; //наш айдишник
        private String mark; //поле имя
        private String model; //фамилия

        public String getMark() {
            return mark;
        }

        public long getId() {
            return id;
        }

        public String getModel() {
            return model;
        }

        public void setMark(String mark) {
            this.mark = mark;
        }

        public void setId(long id) {
            this.id = id;
        }

        public void setId(String id) {
            this.id = Long.valueOf(id);
        }

        public void setModel(String model) {
            this.model = model;
        }

        public class AutosColumns implements BaseColumns {
            public static final String MARK = "mark";
            public static final String MODEL = "model";
        }
    }

    public static Date stringToDate(String aDate, String aFormat) {

        if (aDate == null) return null;
        ParsePosition pos = new ParsePosition(0);
        SimpleDateFormat simpledateformat = new SimpleDateFormat(aFormat);
        Date stringDate = simpledateformat.parse(aDate, pos);
        return stringDate;

    }


    public static class Fueling {
        public static final String DEFAULT_SORT = FuelColumns.DATE + " DESC";
        public static final String TABLE_CONT = "Fueling"; //имя таблицы
        private long id; //наш айдишник
        private long id_auto;
        private Date date;
        private Long odo;
        private Long trip;
        private Float summa;
        private Float price;
        private Float litres;
        private int type;
        public static NumberFormat formatter;

        public Fueling() {
            date = new Date();
            formatter = NumberFormat.getNumberInstance(new Locale("ru", "RU"));
        }

        public long getId() {
            return this.id;
        }

        public long getId_auto() {
            return this.id_auto;
        }

        public Long getDate() {
            return this.date.getTime();
        }

        public String getDateString() {
            DateFormat df = new DateFormat();
            return df.format("dd.MM.yyyy", this.date).toString();
        }

        public Long getOdo() {
            return odo;
        }

        public String getOdoString() {
            return formatter.format(odo);
        }

        public Long getTrip() {
            return trip;
        }

        public String getTripString() {
            return formatter.format(trip);
        }

        public Float getSumma() {
            return summa;
        }

        public String getSummaString() {
            return formatter.format(summa);
        }

        public Float getPrice() {
            return price;
        }

        public String getPriceString() {
            return formatter.format(price);
        }

        public Float getLitres() {
            return litres;
        }

        public String getLitresString() {
            return formatter.format(litres);
        }

        public void setId(long id) {
            this.id = id;
        }

        public void setId_auto(long id_auto) {
            this.id_auto = id_auto;
        }

        public void setDate(Long date) {
            if (date == 0) {
                this.date.getTime();
            } else this.date.setTime(date);
        }

        public void setDate(Date date) {
            this.date = date;
        }

        public void setDate(String date) {
            this.date = stringToDate(date, "dd.MM.yyyy");
        }

        public void setOdo(Long odo) {
            this.odo = odo;
        }

        public void setOdo(String odo) {
            try {
                this.odo = formatter.parse(odo).longValue();
            } catch (Exception e) {
                this.odo = Long.valueOf(0);
            }
        }

        public void setTrip(Long trip) {
            this.trip = trip;
        }

        public void setTrip(String trip) {
            try {
                this.trip = formatter.parse(trip).longValue();
            } catch (Exception e) {
                this.trip = Long.valueOf(0);
            }
        }

        public void setSumma(Float summa) {
            this.summa = summa;
        }

        public void setSumma(String summa) {
            try {
                this.summa = formatter.parse(summa).floatValue();
            } catch (Exception e) {
                this.summa = Float.valueOf(0);
            }
        }

        public void setPrice(Float price) {
            this.price = price;
        }

        public void setPrice(String price) {
            try {
                this.price = formatter.parse(price).floatValue();
            } catch (Exception e) {
                this.price = Float.valueOf(0);
            }
        }

        public void setLitres(Float litres) {
            this.litres = litres;
        }

        public void setLitres(String litres) {
            try {
                this.litres = formatter.parse(litres).floatValue();
            } catch (Exception e) {
                this.litres = Float.valueOf(0);
            }
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }


        public class FuelColumns implements BaseColumns {
            public static final String ID_AUTO = "id_auto";
            public static final String DATE = "date";
            public static final String ODO = "odo";
            public static final String TRIP = "trip";
            public static final String SUMMA = "summa";
            public static final String PRICE = "price";
            public static final String LITR = "litr";
            public static final String TYPE = "type";
        }
    }

    public static class Photos {
        public static final String DEFAULT_SORT = PhotosColumns.NAME + " DESC";
        public static final String TABLE_NAME = "Photos"; //имя таблицы

        public void setId_f(Long id_f) {
            this.id_f = id_f;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Long getId_f() {
            return id_f;
        }

        public String getName() {
            return name;
        }

        private Long id_f;
        private String name;


        public class PhotosColumns implements BaseColumns {
            public static final String ID_F = "id_f";
            public static final String NAME = "name";
        }

    }
}