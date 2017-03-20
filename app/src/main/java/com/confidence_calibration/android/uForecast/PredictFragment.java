package com.confidence_calibration.android.uForecast;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.Date;
import java.util.UUID;

public class PredictFragment extends Fragment {

    private static String ARG_PREDICT_ID = "predict_id";
    private static String DIALOG_DATE = "DialogDate";

    private static final int REQUEST_DATE = 0;

    private Predict mPredict;
    private EditText mTitleField;
    private EditText mDescription;
    private TextView mOutcomeDateText;
    private TextView mEntryDateText;
    private Spinner mSpinner;
    private EditText mConfidence;

    public static PredictFragment newInstance(UUID predictId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_PREDICT_ID, predictId);

        PredictFragment fragment = new PredictFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        // Gets UUID and either pulls existing predict or creates a blank one
        UUID predictId = (UUID) getArguments().getSerializable(ARG_PREDICT_ID);
        if (predictExists(predictId)) {
            mPredict = PredictManager.get(getActivity()).getPredict(predictId);
        } else {
            mPredict = new Predict(predictId);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        menuInflater.inflate(R.menu.fragment_prediction_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_save_predict:
                if (predictExists(mPredict.getUUID())) {
                    PredictManager.get(getActivity()).updatePredict(mPredict);
                    Toast.makeText(getActivity(), "1 prediction updated.", Toast.LENGTH_SHORT).show();
                } else {
                    PredictManager.get(getActivity()).addPredict(mPredict);
                    Toast.makeText(getActivity(), "1 prediction added.", Toast.LENGTH_SHORT).show();
                }
                getActivity().finish();
                return true;
            case R.id.menu_item_edit_predict:
                PredictManager.get(getActivity()).removePredict(mPredict);
                getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_prediction, container, false);

        mTitleField = (EditText)v.findViewById(R.id.predict_title);
        mTitleField.setText(mPredict.getTitle());
        mTitleField.measure(0,0);
        // If statement forces focus on EditText iff text is blank.
        if (!mTitleField.getText().toString().equals("")) {
            mTitleField.clearFocus();
        } else {
            mTitleField.requestFocus();
        }
        int titleHeight = mTitleField.getMeasuredHeight();
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mPredict.setTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Do nothing
            }
        });

        mConfidence = (EditText)v.findViewById(R.id.predict_confidence_level);
        if (mPredict.getConfidence() == 0) {
            mConfidence.setText("");
        } else {
            mConfidence.setText(Integer.toString(mPredict.getConfidence()));
        }
        mConfidence.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String confidenceInput = s.toString();

                // Avoids exception caused by conversion from empty string to int
                if (confidenceInput.equals("")) {
                    mPredict.setConfidence(0);
                } else {
                    mPredict.setConfidence(Integer.parseInt(confidenceInput));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Do nothing
            }
        });

        mOutcomeDateText = (TextView) v.findViewById(R.id.predict_date);
        mOutcomeDateText.setHeight(titleHeight);
        mOutcomeDateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(mPredict.getDate());
                dialog.setTargetFragment(PredictFragment.this, REQUEST_DATE);
                dialog.show(fragmentManager, DIALOG_DATE);
            }
        });

        mEntryDateText = (TextView) v.findViewById(R.id.entry_date);
        mEntryDateText.setHeight(titleHeight);
        String entryDate = DateFormat.getDateInstance().format(mPredict.getDateEntered());
        mEntryDateText.setText(entryDate);
        updateDate();

        mSpinner = (Spinner) v.findViewById(R.id.is_outcome_known);
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(
                getActivity().getBaseContext(), R.array.predict_spinner_options,
                android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(spinnerAdapter);
        if (!mPredict.isKnown()) {
            mSpinner.setSelection(0);
        } else {
            if (mPredict.isCorrect()) {
                mSpinner.setSelection(1);
            } else {
                mSpinner.setSelection(2);
            }
        }
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        mPredict.setKnown(false);
                        mPredict.setCorrect(false);
                        break;
                    case 1:
                        mPredict.setKnown(true);
                        mPredict.setCorrect(true);
                        break;
                    case 2:
                        mPredict.setKnown(true);
                        mPredict.setCorrect(false);
                        break;
                    default:
                        break;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        mDescription = (EditText)v.findViewById(R.id.predict_description);
        mDescription.setText(mPredict.getDescription());
        mDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mPredict.setDescription(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Do nothing
            }
        });

//        logViewHeight();

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_DATE) {
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mPredict.setDate(date);
            updateDate();
        }
    }

    public Boolean predictExists(UUID currentPredict) {
        Predict predictInDb = PredictManager.get(getActivity()).getPredict(currentPredict);
        return !(predictInDb == null);
    }

    private void updateDate() {
        String outcomeDate = DateFormat.getDateInstance().format(mPredict.getDate());
        mOutcomeDateText.setText(outcomeDate);
    }

    private void logViewHeight() {
        mTitleField.measure(0,0);
        int titleHeight = mTitleField.getMeasuredHeight();

        String logOutput = "Title height: " + titleHeight;
        Log.d("Cursor object", logOutput);
    }
}
