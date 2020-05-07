package com.deb.notific.helper;

import android.provider.BaseColumns;

public class Contract {
    public Contract() {
    }
    public static final class MissedCalls implements BaseColumns{
        public static final String TABLE_NAME = "MissedCalls";
        public static final String COLUMN_NAME ="name";
        public static final String COLUMN_NUMBER ="number";
        public static final String COLUMN_TIME ="time";
    }

}
