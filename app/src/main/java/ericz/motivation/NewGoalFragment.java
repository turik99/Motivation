package ericz.motivation;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NewGoalFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NewGoalFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewGoalFragment extends Fragment{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private FloatingActionButton button;
    private OnFragmentInteractionListener mListener;
    String userId;
    TextView startDateText;
    TextView endDateText;
    FirebaseFirestore firestore;
    int penalty = 15;
    EditText mileEdit;
    Calendar cal;
    DiscreteSeekBar distanceSeekBar;

    RadioGroup rg;
    RadioButton eight;
    RadioButton fifteen;
    RadioButton fourty;
    RadioButton oneHundred;
    EditText penaltyEditText;
    public NewGoalFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static NewGoalFragment newInstance(String userIdArg) {

        NewGoalFragment fragment = new NewGoalFragment();
        Bundle args = new Bundle();
        args.putString("userId", userIdArg);
        fragment.setArguments(args);



        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        firestore = FirebaseFirestore.getInstance();

        userId = getArguments().getString("userId");

        mListener.onFragmentInteraction("newgoalfragment");


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_new_goal, container, false);
        Button okButton = view.findViewById(R.id.okButton);


        rg = view.findViewById(R.id.radioGroup2);

        Button wtfButton = view.findViewById(R.id.wtfButton);
        wtfButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(getContext());
                dialog.setContentView(R.layout.wtf_layout);
                dialog.show();
                Button button = dialog.findViewById(R.id.okButtonWtfLayout);
                button.setOnClickListener(new Button.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (dialog.isShowing())
                        {
                            dialog.dismiss();
                        }
                    }
                });

            }
        });


        penaltyEditText = view.findViewById(R.id.penaltyEdit);
        penaltyEditText.setText("15");
        startDateText = view.findViewById(R.id.startDateText);
        endDateText = view.findViewById(R.id.endDateText);
        mileEdit = view.findViewById(R.id.mileEdit);
        distanceSeekBar = view.findViewById(R.id.distanceSeekBar);

        eight = view.findViewById(R.id.eightDollarPen);
        eight.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                penalty = 8;
                penaltyEditText.setText("8");

            }
        });
        fifteen = view.findViewById(R.id.fifteenDollarPen);
        fifteen.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                penalty = 15;
                penaltyEditText.setText("15");

            }
        });
        fourty = view.findViewById(R.id.fourtyDollarPen);
        fourty.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                penalty = 40;
                penaltyEditText.setText("40");

            }
        });

        oneHundred = view.findViewById(R.id.oneHundredDollarPen);
        oneHundred.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {

                penalty = 100;
                penaltyEditText.setText("100");
            }
        });

        penaltyEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.v("beforeTextChanged", String.valueOf(charSequence));

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String input = String.valueOf(charSequence);

                Log.v("onTextChanged penalty", "changed");

            }

            @Override
            public void afterTextChanged(Editable editable) {
                Log.v("afterTextChanged", editable.toString());
                String input = editable.toString();
                rg.clearCheck();
                if (input.equals("8") || input.equals("15") || input.equals("40") || input.equals("100"))
                {
                    if (input.equals("8"))
                    {
                        eight.setChecked(true);
                    }
                    if (input.equals("15"))
                    {
                        fifteen.setChecked(true);
                    }
                    if (input.equals("40"))
                    {
                        fourty.setChecked(true);
                    }
                    if (input.equals("100"))
                    {
                        oneHundred.setChecked(true);
                    }
                }
                else
                {
                    eight.setChecked(false);
                    fifteen.setChecked(false);
                    fourty.setChecked(false);
                    oneHundred.setChecked(false);
                }
            }
        });

        mileEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.v("beforeTextChanged", String.valueOf(charSequence));
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                int integer = 1;
                String string = String.valueOf(charSequence);
                if (!string.equals("")){
                    integer = Integer.valueOf(string);
                }
                Log.v("onTextChanged", string);

                if (!string.equals("")
                        && integer >1 && integer<100)
                {
                    distanceSeekBar.setProgress(integer);
                }
                if (string.equals("") || integer<2)
                    distanceSeekBar.setProgress(2);
                if (integer>100)
                    distanceSeekBar.setProgress(100);

                mileEdit.setSelection(mileEdit.getText().length());

            }

            @Override
            public void afterTextChanged(Editable editable) {
                Log.v("afterTextchanged", "yuh");

            }
        });


        DiscreteSeekBar.OnProgressChangeListener onProgressChangeListener = new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                if ( !mileEdit.getText().toString().equals("") && Integer.valueOf(mileEdit.getText().toString()) >100)
                {
                    //not changing the mileEdit value
                }
                else
                {
                    mileEdit.setText(String.valueOf(value));

                }
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {

            }
        };

        distanceSeekBar.setOnProgressChangeListener(onProgressChangeListener);

        final DatePickerDialog.OnDateSetListener startDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                startDateText.setText(month+1 + "/" + dayOfMonth + "/" + year);
            }
        };


        final DatePickerDialog.OnDateSetListener endDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                endDateText.setText(month+1 + "/" + dayOfMonth + "/" + year);

            }
        };

        okButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {

                updateGoal();
            }
        });

        final DatePickerDialog datePickerDialog = new DatePickerDialog(getContext());
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);

        final Spinner startDateSpinner = view.findViewById(R.id.startDateSpinner);
        Spinner endDateSpinner = view.findViewById(R.id.endDateSpinner);


        final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        final Date today = new Date();
        cal = Calendar.getInstance();
        cal.setTime(today);
        cal.add(Calendar.DAY_OF_MONTH, 1);
        final Date tomorrow = cal.getTime();
        cal.add(Calendar.DAY_OF_MONTH, 1);
        final Date theDayAfter = cal.getTime();


        startDateSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0)
                {
                    startDateText.setText(dateFormat.format(today));
                }
                if (i == 1)
                {
                    startDateText.setText(dateFormat.format(tomorrow));
                }
                if (i == 2)
                {
                    startDateText.setText(dateFormat.format(theDayAfter));
                }
                if (i == 3)
                {
                    datePickerDialog.setOnDateSetListener(startDateListener);
                    datePickerDialog.show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        endDateSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0)
                {
                    try
                    {
                        if (startDateSpinner.getSelectedItemPosition() == 0)
                        {

                            Date startDate = dateFormat.parse(startDateText.getText().toString());
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(startDate);
                            calendar.add(Calendar.DAY_OF_MONTH, 7);
                            Log.v("endDateSpinnerTest", dateFormat.format(calendar.getTime()));
                            endDateText.setText(dateFormat.format(calendar.getTime()));                        }
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
                if (i == 1)
                {
                    try
                    {
                        Date startDate = dateFormat.parse(startDateText.getText().toString());
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(startDate);
                        calendar.add(Calendar.MONTH, 1);
                        Log.v("endDateSpinnerTest", dateFormat.format(calendar.getTime()));
                        endDateText.setText(dateFormat.format(calendar.getTime()));
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
                if (i == 2)
                {
                    datePickerDialog.setOnDateSetListener(endDateListener);
                    datePickerDialog.show();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });





        return view;
    }


    //This is called when the User decides to hit OK and make a new Goal. Probably, there should be
    //Payment Related code in this method.
    public void updateGoal()
    {
        DocumentReference reference = firestore.collection("users").document(userId);

        //die Zeit is German for 'the time' Sorry for writing in german so much. When you have two similar variables,
        //and don't want to name them the same thing, just defaulting to a different language can help save some confu
        //sion if you know that language. Ja Wohl!!! und viel danke für deiner Hilfen!

        double dieZeit = System.currentTimeMillis();
        reference.update("goal", FieldValue.arrayUnion(userId, dieZeit,
                startDateText.getText().toString(), endDateText.getText().toString(), penalty, Integer.valueOf(mileEdit.getText().toString()), 0, 0.00)).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("database update", "DocumentSnapshot successfully updated!");

                mListener.onFragmentInteraction("newGoalMade");

            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }




    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(String signal);
    }
}
