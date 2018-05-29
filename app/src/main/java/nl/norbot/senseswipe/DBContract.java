package nl.norbot.senseswipe;

import android.provider.BaseColumns;

public final class DBContract {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private DBContract() {}

    /* Inner class that defines the table contents */
    public static class DBEntry implements BaseColumns {
        public static final String TABLE_NAME = "results";
        public static final String COLUMN_NAME_CREATEDAT = "createdat";
        public static final String COLUMN_NAME_INPUTMETHOD = "inputmethod";
        public static final String COLUMN_NAME_SUBJECT = "subject";
        public static final String COLUMN_NAME_TASK = "task";
        public static final String COLUMN_NAME_SUBTASK = "subtask";
        public static final String COLUMN_NAME_VALUE = "value";
    }
}
