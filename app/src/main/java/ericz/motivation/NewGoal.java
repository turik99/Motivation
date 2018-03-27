package ericz.motivation;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link NewGoal#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewGoal extends DialogFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public NewGoal() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static NewGoal newInstance() {
        NewGoal fragment = new NewGoal();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_new_goal, container, false);

        SeekBar mileBar = view.findViewById(R.id.mileGoalBar);
        SeekBar moneyBar = view.findViewById(R.id.moneyBar);

        final TextView mileText = view.findViewById(R.id.mileText);
        final TextView moneyText = view.findViewById(R.id.moneyText);

        //Getting the data from the bar with a listener
        mileBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int mileProgress = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mileProgress = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                mileText.setText(mileProgress + " Mile(s)");

            }
        });


        //Getting the data from the bar with a listener
        moneyBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            String moneyProgress = "";

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                moneyProgress = String.valueOf(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                moneyText.setText("$" + moneyProgress);
            }
        });

        return view;
    }
}
