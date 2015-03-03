package db;

/**
 * Created by tabunshikov.vadim on 03.03.2015.
 */

import android.provider.BaseColumns;

import java.util.Date;

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

    public static class Fueling {
        public static final String DEFAULT_SORT = FuelColumns.DATE + " DESC";
        public static final String TABLE_CONT = "Fueling"; //имя таблицы
        private long id; //наш айдишник
        private Date date;
        private Long odo;

        public long getId() {
            return id;
        }
        public Long getDate() {return date.getTime();  }
        public Long getOdo() { return odo;  }
        public void setId(long id) {
            this.id = id;
        }
        public void setDate(Long date) {
            this.date.setTime(date);
        }
        public void setOdo(Long odo) {
            this.odo = odo;
        }
        public class FuelColumns implements BaseColumns {
            public static final String DATE = "date";
            public static final String ODO = "odo";
        }
    }

}
