package com.confidence_calibration.android.uForecast;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.confidence_calibration.android.uForecast.database.PredictionCursorWrapper;
import com.confidence_calibration.android.uForecast.database.PredictionDbHelper;
import com.confidence_calibration.android.uForecast.database.PredictionDbSchema.PredictionTable;

public class PredictManager {
    public static PredictManager sPredictManager;

    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static PredictManager get(Context context) {
        if (sPredictManager == null) {
            sPredictManager = new PredictManager(context);
        }

        return sPredictManager;
    }

    private PredictManager(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new PredictionDbHelper(mContext).getWritableDatabase();
    }

    public void addPredict(Predict predict) {
        ContentValues values = getContentValues(predict);

        mDatabase.insert(PredictionTable.NAME, null, values);
    }

    public void removePredict(Predict predict) {
        mDatabase.delete(
                PredictionTable.NAME,
                PredictionTable.Cols.UUID + " = ?",
                new String[] {predict.getUUID().toString()}
        );
    }

    public List<Predict> getPredicts(int index) {
        // If 0, returns a list of all predictions. If 1, returns
        // a list of the pending predictions in the database
        PredictionCursorWrapper cursor;
        if (index == 0) {
            cursor = queryPredicts(null, null, null);
        } else {
            cursor = queryPredicts(
                    PredictionTable.Cols.KNOWN + " = 0",
                    null,
                    null
            );
        }

        List<Predict> predicts = new ArrayList<>();
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                predicts.add(cursor.getPredict());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }

        return predicts;
    }

    public Predict getPredict(UUID uuid) {
        // Returns a specific prediction from database
        PredictionCursorWrapper cursor = queryPredicts(
                PredictionTable.Cols.UUID + " = ?",
                null,
                new String[] {uuid.toString()}
        );

        try {
            if (cursor.getCount() == 0) {
                return null;
            }

            cursor.moveToFirst();
            return cursor.getPredict();
        } finally {
            cursor.close();
        }
    }

    public void updatePredict(Predict predict) {
        String uuidString = predict.getUUID().toString();
        ContentValues values = getContentValues(predict);

        mDatabase.update(PredictionTable.NAME,
                values,
                PredictionTable.Cols.UUID + " = ?",
                new String[] {uuidString});
    }

    public ArrayList<ArrayList<Integer>> getGraphValues() {
        // Pulls all known predictions' confidence levels and trues/falses from
        // database and returns an ArrayList of the all predictions' percent
        // confidence and outcome status.
        PredictionCursorWrapper cursor = queryPredicts(
                PredictionTable.Cols.KNOWN + " = 1",
                new String[] {PredictionTable.Cols.CONFIDENCE, PredictionTable.Cols.OUTCOME},
                null
        );

        ArrayList<ArrayList<Integer>> allPredicts = new ArrayList<>();

        if (!cursor.moveToFirst()) {
            return allPredicts;
        }

        try {
            while (!cursor.isAfterLast()) {
                allPredicts.add(cursor.getConfidenceAndBool());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }

        return allPredicts;
    }

    private static ContentValues getContentValues(Predict predict) {
        ContentValues values = new ContentValues();
        values.put(PredictionTable.Cols.UUID, predict.getUUID().toString());
        values.put(PredictionTable.Cols.TITLE, predict.getTitle());
        values.put(PredictionTable.Cols.DESCRIPTION, predict.getDescription());
        values.put(PredictionTable.Cols.DATE_OF_RESOLUTION, predict.getDate().getTime());
        values.put(PredictionTable.Cols.DATE_OF_ENTRY, predict.getDateEntered().getTime());
        values.put(PredictionTable.Cols.CONFIDENCE, predict.getConfidence());
        values.put(PredictionTable.Cols.OUTCOME, predict.isCorrect() ? 1 : 0);
        values.put(PredictionTable.Cols.KNOWN, predict.isKnown() ? 1 : 0);

        return values;
    }

    private PredictionCursorWrapper queryPredicts(String whereClause, String[] whereColumns, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                PredictionTable.NAME, // Table name
                whereColumns, // Specifies columns
                whereClause, // Only includes rows that satisfy these conditions
                whereArgs, // Replace question marks so that input is sanitized
                null, // Group by
                null, // Having
                PredictionTable.Cols.DATE_OF_ENTRY + " DESC"  // Order by
        );

        return new PredictionCursorWrapper(cursor);
    }

    // Writes out all database entries
    public void logWholeDB() {
        PredictionCursorWrapper cursor = queryPredicts(null, null, null);

        cursor.moveToFirst();
        Log.d("Cursor object", DatabaseUtils.dumpCursorToString(cursor));
        cursor.close();
    }
}
