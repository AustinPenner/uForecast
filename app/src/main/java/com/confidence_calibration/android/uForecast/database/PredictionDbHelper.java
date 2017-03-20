package com.confidence_calibration.android.uForecast.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.confidence_calibration.android.uForecast.database.PredictionDbSchema.PredictionTable;

public class PredictionDbHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "predictionDatabase.db";

    public PredictionDbHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + PredictionTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                PredictionTable.Cols.UUID + " text, " +
                PredictionTable.Cols.TITLE + " text, " +
                PredictionTable.Cols.DESCRIPTION + " text, " +
                PredictionTable.Cols.DATE_OF_RESOLUTION + " integer, " +
                PredictionTable.Cols.DATE_OF_ENTRY + " integer, " +
                PredictionTable.Cols.CONFIDENCE + " integer, " +
                PredictionTable.Cols.OUTCOME + " integer, " +
                PredictionTable.Cols.KNOWN + " integer" +
                ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}