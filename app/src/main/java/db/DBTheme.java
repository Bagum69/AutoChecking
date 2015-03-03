package db;

/**
 * Created by tabunshikov.vadim on 03.03.2015.
 */

import android.provider.BaseColumns;

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


}
