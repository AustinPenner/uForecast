package com.confidence_calibration.android.uForecast;

import java.util.Date;
import java.util.UUID;

public class Predict {

    private UUID mUUID;
    private String mTitle;
    private String mDescription;
    private Date mDate;
    private Date mDateEntered;
    private int mConfidence;
    private boolean mCorrect;
    private boolean mKnown;


    public Predict() {
        // Sets a random value as a unique ID
        this(UUID.randomUUID());
    }

    public Predict(UUID uuid) {
        mUUID = uuid;
        mDate = new Date();
        mDateEntered = new Date();
        mCorrect = false;
        mKnown = false;
        mConfidence = 0;
    }

    public UUID getUUID() {
        return mUUID;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public Date getDateEntered() {
        return mDateEntered;
    }

    public void setDateEntered(Date dateEntered) {
        mDateEntered = dateEntered;
    }

    public int getConfidence() {
        return mConfidence;
    }

    public void setConfidence(int confidence) {
        mConfidence = confidence;
    }

    public Boolean isCorrect() {
        return mCorrect;
    }

    public void setCorrect(boolean correct) {
        mCorrect = correct;
    }

    public Boolean isKnown() {
        return mKnown;
    }

    public void setKnown(boolean known) {
        mKnown = known;
    }
}
