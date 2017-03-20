package com.confidence_calibration.android.uForecast;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import java.util.ArrayList;
import java.util.Arrays;

import static android.content.Context.MODE_PRIVATE;

public class PredictGraphFragment extends Fragment {

    private BarChart mChart;
    private BarDataSet mBarDataSet;
    private BarData mBarData;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_predict_graph, container, false);
        mChart = (BarChart) v.findViewById(R.id.bar_graph);

//        Formats the X axis
        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setLabelCount(11, true);
        xAxis.setAxisMinimum(0f);
        xAxis.setAxisMaximum(100f);

//         Formats left side of the Y axis
        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setDrawTopYLabelEntry(true);
        leftAxis.setLabelCount(11, true);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setAxisMaximum(100f);

        LimitLine topLine = new LimitLine(100f);
        topLine.setLineColor(Color.GRAY);
        topLine.setLineWidth(.4f);
        leftAxis.addLimitLine(topLine);

//         Removes right side of Y axis
        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setDrawLabels(false);
        rightAxis.setDrawAxisLine(true);
        rightAxis.setDrawGridLines(false);

        Description description = new Description();
        description.setText("");
        mChart.setDescription(description);

        mChart.getLegend().setEnabled(false);
        mChart.setTouchEnabled(false);

        // Populates graph. If first time creating graph, default is quartiles.
        setData(getIntervalCount());

        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        menuInflater.inflate(R.menu.fragment_prediction_graph_menu, menu);
    }

    // Switches between quartiles and deciles for the graph fragment
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.fourBars:
                setData(4);
                mChart.invalidate();
                break;
            case R.id.tenBars:
                setData(10);
                mChart.invalidate();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void setData(int intervalCount) {
        // Saves number of intervals through activity lifecycle
        setIntervalCount(intervalCount);

        float intervalWidth = 90f / intervalCount;

        ArrayList<Float> rawBarEntries;
        rawBarEntries = getIntervalPercentage(intervalCount);

        ArrayList<BarEntry> entries;
        entries = addValuesToEntries(rawBarEntries);

        mBarDataSet = new BarDataSet(entries, "the label");
        mBarDataSet.setValueFormatter(new PercentValueFormatter());
        mBarDataSet.setBarBorderWidth(1.f);
        mBarDataSet.setValueTextSize(11f);
        mBarDataSet.setColors(new int[]{R.color.gray}, getContext());

        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(mBarDataSet);

        mBarData = new BarData(dataSets);
        mBarData.setBarWidth(intervalWidth);
        mChart.setData(mBarData);
        mChart.animateY(1500);
    }

    public ArrayList<BarEntry> addValuesToEntries(ArrayList<Float> rawEntries) {
        ArrayList<BarEntry> barEntry = new ArrayList<>();

        int intervals = rawEntries.size();
        float intervalWidth = 100f / intervals;

        for (int i = 0; i < intervals; i++) {
            float barXValue = (i * intervalWidth) + (.5f * intervalWidth); // Centers each bar
            float entry = rawEntries.get(i);
            barEntry.add(new BarEntry(barXValue, entry));
        }

        return barEntry;
    }

    public ArrayList<Float> getIntervalPercentage(int intervals) {
        ArrayList<ArrayList<Integer>> allPredicts;
        allPredicts = PredictManager.get(getActivity()).getGraphValues();

        int width = 100 / intervals; // Width has to be an integer

        int[] intervalCount;
        intervalCount = new int[intervals];
        int[] intervalSum;
        intervalSum = new int[intervals];

        for (ArrayList<Integer> member : allPredicts) {
            // Categorizes each prediction into one of the intervals. Note that predictions
            // right on the interval border will be rounded up. E.g. if there are 4
            // intervals and a prediction is exactly 25% confident, it's placed in the
            // 25%-50% interval instead of the 0%-25% interval.
            int predictPercent = member.get(0);
            int intervalIndex = predictPercent / width;

            // If the prediction was 100% this will move it into the topmost bracket
            // (e.g. if there are 4 intervals, it is moved into the 75% - 100% interval)
            if (intervalIndex == intervals) {
                intervalIndex -= 1;
            }

            // Writes out the count and sum, so that the average percent correct can be
            // calculated for a given interval.
            intervalCount[intervalIndex] += 1;
            intervalSum[intervalIndex] += member.get(1);
        }

//        Float[] intervalAvg;
//        intervalAvg = new Float[intervals];
//        if (intervals == 4) {
//            intervalAvg[0] = 19f;
//            intervalAvg[1] = 40f;
//            intervalAvg[2] = 66f;
//            intervalAvg[3] = 85f;
//        } else {
//            intervalAvg[0] = 8f;
//            intervalAvg[1] = 19f;
//            intervalAvg[2] = 30f;
//            intervalAvg[3] = 37f;
//            intervalAvg[4] = 47f;
//            intervalAvg[5] = 60f;
//            intervalAvg[6] = 69f;
//            intervalAvg[7] = 80f;
//            intervalAvg[8] = 88f;
//            intervalAvg[9] = 96f;
//        }

        Float[] intervalAvg;
        intervalAvg = new Float[intervals];
        for (int i = 0; i < intervals; i++) {
            if (intervalCount[i] == 0) {
                intervalAvg[i] = 0f;
            } else {
                float average = (float) intervalSum[i] / intervalCount[i];
                intervalAvg[i] = average * 100; // Converts to percentage
            }
        }

        return new ArrayList<>(Arrays.asList(intervalAvg));
    }

    public int getIntervalCount() {
        SharedPreferences graphDetails = getActivity().getSharedPreferences("graphdetails", MODE_PRIVATE);
        return graphDetails.getInt("intervals",4);
    }

    public void setIntervalCount(int intervals) {
        SharedPreferences graphDetails = getActivity().getSharedPreferences("graphdetails", MODE_PRIVATE);
        SharedPreferences.Editor editor = graphDetails.edit();
        editor.putInt("intervals", intervals);
        editor.apply();
    }
}
