package com.confidence_calibration.android.uForecast.database;

public class PredictionDbSchema {
    public static final class PredictionTable {
        public static final String NAME = "predictions";

        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String TITLE = "title";
            public static final String DESCRIPTION = "description";
            public static final String DATE_OF_RESOLUTION = "date_of_resolution";
            public static final String DATE_OF_ENTRY = "date_of_entry";
            public static final String CONFIDENCE = "confidence";
            public static final String OUTCOME = "truth_value";
            public static final String KNOWN = "known";
        }
    }
}
