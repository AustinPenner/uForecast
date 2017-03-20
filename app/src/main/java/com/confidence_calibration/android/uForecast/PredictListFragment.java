package com.confidence_calibration.android.uForecast;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.List;

public class PredictListFragment extends Fragment {

    private RecyclerView mPredictRecyclerView;
    private TextView mEmptyView;
    private PredictAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_prediction_list, container, false);

        mEmptyView = (TextView) view.findViewById(R.id.empty_view);

        mPredictRecyclerView = (RecyclerView) view
                .findViewById(R.id.prediction_recycler_view);
        mPredictRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mPredictRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity()));

        FloatingActionButton myFab = (FloatingActionButton)  view.findViewById(R.id.fab);
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Predict prediction = new Predict();
                Intent intent = PredictActivity.newIntent(getActivity(), prediction.getUUID());
                startActivity(intent);
            }
        });

        updateUI();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    private void updateUI() {
        Bundle args = getArguments();
        int index = args.getInt("index");

        PredictManager predictManager = PredictManager.get(getActivity());
        List<Predict> predicts = predictManager.getPredicts(index);

        if (mAdapter == null) {
            mAdapter = new PredictAdapter(predicts);
            mPredictRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setPredicts(predicts);
            mAdapter.notifyDataSetChanged();
        }

        if (predicts.size() == 0) {
            mPredictRecyclerView.setVisibility(View.GONE);
            mEmptyView.setVisibility(View.VISIBLE);
        } else {
            mPredictRecyclerView.setVisibility(View.VISIBLE);
            mEmptyView.setVisibility(View.GONE);
        }
    }

    public class PredictHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private Predict mPredict;

        private TextView mTitleTextView;
        private TextView mDateTextView;
        private TextView mConfidenceLevel;

        public PredictHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            mTitleTextView = (TextView)
                    itemView.findViewById(R.id.list_item_predict_title_text_view);
            mDateTextView = (TextView)
                    itemView.findViewById(R.id.list_item_predict_date_text_view);
            mConfidenceLevel = (TextView)
                    itemView.findViewById(R.id.list_item_predict_confidence);
        }

        public void bindPredict(Predict predict) {
            mPredict = predict;
            mTitleTextView.setText(mPredict.getTitle());
            mTitleTextView.measure(0,0);

            String stringDate = DateFormat.getDateInstance().format(mPredict.getDate());
            mDateTextView.setText(stringDate);
            mDateTextView.measure(0,0);

            String confidence = Integer.toString(mPredict.getConfidence());
            mConfidenceLevel.setText(confidence + "%");
            int confidenceHeight = mTitleTextView.getMeasuredHeight() + mDateTextView.getMeasuredHeight();
            mConfidenceLevel.setHeight(confidenceHeight);
        }

        @Override
        public void onClick(View v) {
            Intent intent = PredictActivity.newIntent(getActivity(), mPredict.getUUID());
            startActivity(intent);
        }
    }

    private class PredictAdapter extends RecyclerView.Adapter<PredictHolder> {

        private List<Predict> mPredicts;

        public PredictAdapter(List<Predict> predicts) {
            mPredicts = predicts;
        }

        @Override
        public PredictHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater
                    .inflate(R.layout.list_item_prediction, parent, false);
            return new PredictHolder(view);
        }

        @Override
        public void onBindViewHolder(PredictHolder holder, int position) {
            Predict predict = mPredicts.get(position);
            holder.bindPredict(predict);
        }

        @Override
        public int getItemCount() {
            return mPredicts.size();
        }

        public void setPredicts(List<Predict> predicts) {
            mPredicts = predicts;
        }
    }

    public class DividerItemDecoration extends RecyclerView.ItemDecoration {
        // This creates a dividing line between list items.

        private final int[] ATTRS = new int[]{android.R.attr.listDivider};

        private Drawable mDivider;

        public DividerItemDecoration(Context context) {
            final TypedArray styledAttributes = context.obtainStyledAttributes(ATTRS);
            mDivider = styledAttributes.getDrawable(0);
            styledAttributes.recycle();
        }

        @Override
        public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
            int left = parent.getPaddingLeft();
            int right = parent.getWidth() - parent.getPaddingRight();

            int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = parent.getChildAt(i);

                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

                int top = child.getBottom() + params.bottomMargin;
                int bottom = top + mDivider.getIntrinsicHeight();

                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
        }
    }
}