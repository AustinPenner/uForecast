package com.confidence_calibration.android.uForecast;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import java.util.UUID;

public class PredictActivity extends AppCompatActivity {

    private static final String EXTRA_PREDICT_ID =
            "com.prediction.android.predictit.prediction_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);

        //Starts PredictFragment
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);

        if (fragment == null) {
            fragment = createFragment();
            fm.beginTransaction()
                    .add(R.id.fragment_container, fragment).commit();
        }
    }

    public static Intent newIntent(Context packageContext, UUID predictionId) {
        Intent intent = new Intent(packageContext, PredictActivity.class);
        intent.putExtra(EXTRA_PREDICT_ID, predictionId);
        return intent;
    }

    protected Fragment createFragment() {
        UUID predictionId = (UUID) getIntent().getSerializableExtra(EXTRA_PREDICT_ID);

        return PredictFragment.newInstance(predictionId);
    }
}
