package com.confidence_calibration.android.uForecast.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

import com.confidence_calibration.android.uForecast.Predict;
import com.confidence_calibration.android.uForecast.database.PredictionDbSchema.PredictionTable;

public class PredictionCursorWrapper extends CursorWrapper {
    public PredictionCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Predict getPredict() {
        String uuidString = getString(getColumnIndex(PredictionTable.Cols.UUID));
        String title = getString(getColumnIndex(PredictionTable.Cols.TITLE));
        String description = getString(getColumnIndex(PredictionTable.Cols.DESCRIPTION));
        long date_resolved = getLong(getColumnIndex(PredictionTable.Cols.DATE_OF_RESOLUTION));
        long date_entered = getLong(getColumnIndex(PredictionTable.Cols.DATE_OF_ENTRY));
        int confidence = getInt(getColumnIndex(PredictionTable.Cols.CONFIDENCE));
        int isCorrect = getInt(getColumnIndex(PredictionTable.Cols.OUTCOME));
        int isKnown = getInt(getColumnIndex(PredictionTable.Cols.KNOWN));

        Predict prediction = new Predict(UUID.fromString(uuidString));
        prediction.setTitle(title);
        prediction.setDescription(description);
        prediction.setDate(new Date(date_resolved));
        prediction.setDateEntered(new Date(date_entered));
        prediction.setConfidence(confidence);
        prediction.setCorrect(isCorrect != 0);
        prediction.setKnown(isKnown != 0);

        return prediction;
    }


    public ArrayList<Integer> getConfidenceAndBool() {
        int confidence = getInt(getColumnIndex(PredictionTable.Cols.CONFIDENCE));
        int isResolved = getInt(getColumnIndex(PredictionTable.Cols.OUTCOME));

        return new ArrayList<>(Arrays.asList(confidence, isResolved));
    }
}
