package com.confidence_calibration.android.uForecast;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class AboutFragment extends Fragment {

    private TextView mAppNameVersion;
    String versionName = BuildConfig.VERSION_NAME;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);

        mAppNameVersion = (TextView) view.findViewById(R.id.app_name_and_version);
        String appName = getString(R.string.app_name);
        mAppNameVersion.setText(appName + " v" + versionName);

        return view;
    }

}
