package ericz.motivation;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResult;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CurrentGoalFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CurrentGoalFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CurrentGoalFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener{
    // TODO: Rename parameter arguments, choose names that match


    /*This is a SPECIAL Class called a Fragment - it's like an ACTIVITY, but can't do all the same
    stuff, and is generally intended only to hold UI elemenants. IT can be killed off and re instantia
    ted very easily, which is nice.
     */

    //A Fragment interaction listener allows you to communicate with an activity, like when you have
    //something complicated to do, and need to let the Activity 'know' to do it, you can use the listener.

    private OnFragmentInteractionListener mListener;
    Map<String, Integer> myMap = new HashMap<String, Integer>();
    private String goalStatus;
    private DataSet fitData;
    private GoogleApiClient mClient;

    //German for "the finished list" - this variable is the final form so to speak of the data we gather
    //from the FireBase database, which is like the number of miles ran and all that.
    private String[] dieFertigeListe = new String[9];
    private String data;
    private Bundle args;
    public CurrentGoalFragment() {
        // Required empty public constructor
    }



    //Think of this as a constructor. Not sure why Android likes to do NewInstance but it be like that.
    public static CurrentGoalFragment newInstance(Bundle bundle) {
        CurrentGoalFragment fragment = new CurrentGoalFragment();
        fragment.setArguments(bundle);
        return fragment;
    }


    //THis is called when the Fragment object is created, but *BEFORE* UI Elements have been added to the screen.
    //IT is complicated.
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {


            //Data is a string version of what we take from the activity's GetDatabase function.
            data = getArguments().getString("key");

            Log.v("getArgs", getArguments().getString("key"));

            //Everything below this is complicated, and I don't remember how it works. Please don't ask about it.
            String[] dieListe = data.split(",");


            //to make a new list.
            // zu machen eine neue Liste
            for (int i = 0; i<dieListe.length; i++)
            {
                Log.v("dieList test", dieListe[i]);
            }

            //this is cleaning the list.
            // zu sauber machen die neue Liste
            ArrayList<String> tempArrayList = new ArrayList<String>();


            //This is where shit hits the fan so to speak. No idea what's going on here. I know that
            //at one point I was saving the dates as Long values, instead now they are strings of dates. normally formatted.

            for (String data: dieListe)
            {

                if (data.contains("nanoseconds"))
                {
                    // not adding any data because the nanoseconds don't matter
                }
                else if (data.contains("Timestamp"))
                {
                    tempArrayList.add(data.substring(19));
                }
                else if (data.contains("[") || data.contains("]"))
                {

                    String newString = null;

                    if (data.contains("["))
                    {
                        newString = data.replace("[",  "");
                        newString = newString.replaceAll(" ", "");
                    }
                    else
                    {
                        newString = data.replace("]",  "");
                        newString = newString.replaceAll(" ", "");
                    }
                    tempArrayList.add(newString);
                }
                else
                {
                    String newString = null;
                    newString = data.replaceAll(" ", "");

                    tempArrayList.add(newString);
                }
            }



            // Bla bla bla had to move the arraylist to an Array because some reason.
            tempArrayList.toArray(dieFertigeListe);

            Log.v("dieFertigeListe test", dieFertigeListe[0]);

            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
            Date dt = null;
            try {
                dt = sdf.parse(dieFertigeListe[2]);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            long currentTimeMillis = System.currentTimeMillis();


            //below this point is checking the current time compared to the time on the goal, so
            // that the little indicator shows correctly whether the goal is coming up, ongoing, or finished.

            long startTimeinMillis = dt.getTime();
            try {
                dt = sdf.parse(dieFertigeListe[3]);
                Log.v("endTimeTestCurGF", dt.toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            long endTimeinMillis = dt.getTime();
            Log.v("time numbers test", String.valueOf(currentTimeMillis) + " end " + endTimeinMillis);

            if (currentTimeMillis >= startTimeinMillis && currentTimeMillis < endTimeinMillis)
            {
                goalStatus = "inProgress";

                Log.v("goalStatus", goalStatus );
            }
            if (currentTimeMillis < startTimeinMillis)
            {
                goalStatus = "notStartedYet";
                Log.v("goalStatus", goalStatus );

            }
            if (endTimeinMillis < currentTimeMillis)
            {
                goalStatus = "finished";
                Log.v("goalStatus", goalStatus );

            }
        }
        else
        {
            Log.v("GetArgs", "is fuckingn null!!!");
        }


        //So here's an example of using that Fragment listener to accomplish something - we are signaling
        //back to the activity that there is a current goal.
        mListener.onFragmentInteraction("currentGoalFragment");

    }


    //NO IDEA IF THIS IS BEING USED OR NOT. FORGOT WHAT IT IS FOR. PROBABLY USELESS.
    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(getContext(), v);

        MenuInflater menuInflater = popup.getMenuInflater();
        Menu menu = popup.getMenu();
        menuInflater.inflate(R.menu.goal_overflow, menu);

        menu.getItem(0).setIcon(R.drawable.baseline_delete_24);
        menu.getItem(1).setIcon(R.drawable.baseline_snooze_24);

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.delete)
                {
                    Intent intent = new Intent(getActivity(), Paydenbts.class);
                    startActivity(intent);
                }
                return false;
            }
        });

        popup.show();
    }


    //ONCREATEVIEW is like ONCREATE, but its when the VIEW is created. THis means you can start adding
    //Buttons and shit here - the view is ready and made to take UI Elements.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_current_goal, container, false);
        ImageView progressImage = view.findViewById(R.id.progressImage);

        Button overflowMenu = view.findViewById(R.id.moreButtonGoal);
        overflowMenu.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopup(view);
            }
        });




        if (goalStatus.equals("inProgress"))
        {
            Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.inprogress, null);
            progressImage.setImageDrawable(drawable);
        }
        if (goalStatus.equals("notStartedYet"))
        {
            Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.comingup, null);
            progressImage.setImageDrawable(drawable);

        }
        if (goalStatus.equals("finished"))
        {
            Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.completed, null);
            progressImage.setImageDrawable(drawable);
        }

        dieFertigeListe[7] = String.valueOf( (Float.valueOf(dieFertigeListe[6]) / Float.valueOf(dieFertigeListe[5])) * Float.valueOf(dieFertigeListe[4]) );




        //Instantiating many different Views below this point.
        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.HALF_UP);

        Log.v("rebate earned test", dieFertigeListe[7]);
        TextView startDate = view.findViewById(R.id.startDate);
        TextView endDate = view.findViewById(R.id.endDate);
        TextView mileGoal = view.findViewById(R.id.mileGoal);
        TextView milesCompleted = view.findViewById(R.id.milesDone);
        TextView initialPayment = view.findViewById(R.id.downPayment);
        TextView earnedBack = view.findViewById(R.id.earnedBack);

        ProgressBar mileBar = view.findViewById(R.id.mileProgressBar);

        mileBar.setProgress( (int)((Float.valueOf(dieFertigeListe[6]) / Float.valueOf(dieFertigeListe[5])) * 100));


        //Setting the values of those UI Elements here, with data from our array that we got.
        startDate.setText(dieFertigeListe[2]);
        endDate.setText(dieFertigeListe[3]);
        initialPayment.setText("$" + dieFertigeListe[4]);

        Log.v("die fertige list test", dieFertigeListe[4]);
        mileGoal.setText(dieFertigeListe[5]);
        milesCompleted.setText(dieFertigeListe[6]);
        earnedBack.setText("$" + df.format(Float.valueOf(dieFertigeListe[7])));



        //Accessing Google Fit which should update some UI Elements now or in the future.
        accessGoogleFit();
        //And we're done here.
        return view;


    }


    //This method accesses the GoogleFit History API, by creating and fulfilling a data read request
    //with the data we want. if the data is gathered successfully, it calls the OnResult function
    //under the PendingResult<DataReadResult> code, which then dumps the 'buckets' of data to the log
    //by sending it to the Process Data function.
    private void accessGoogleFit() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.WEEK_OF_YEAR, -1);
        long startTime = cal.getTimeInMillis();



        Log.v("accessGoogle Fit", "Accessing google Fit");
        DataReadRequest readRequest = new DataReadRequest.Builder()
                .aggregate(DataType.TYPE_ACTIVITY_SEGMENT, DataType.AGGREGATE_ACTIVITY_SUMMARY)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .bucketByTime(1, TimeUnit.DAYS)
                .build();

        Log.v("access google fit", "Google Client Set");

        PendingResult<DataReadResult> pendingresult =
                Fitness.HistoryApi.readData(mClient, readRequest);


        pendingresult.setResultCallback(new ResultCallback<DataReadResult>() {
            @Override
            public void onResult(@NonNull DataReadResult dataReadResult) {
                if (dataReadResult.getBuckets().size()>0)
                {
                    for (Bucket bucket : dataReadResult.getBuckets())
                    {
                        List<DataSet> dataSets = bucket.getDataSets();
                        for (DataSet dataSet : dataSets)
                        {
                            processDataSet(dataSet);
                        }
                    }
                }
            }
        });


    }

    //THis is a very complicated way to simply LOG a data point.
    public void processDataSet (DataSet dataSet)
    {
        String TAG = "fitness history";

        for (DataPoint dataPoint : dataSet.getDataPoints())
        {
            long start = dataPoint.getStartTime(TimeUnit.MILLISECONDS);
            long end = dataPoint.getEndTime(TimeUnit.MILLISECONDS);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS", Locale.US);
            long time = System.currentTimeMillis();

            Date date = new Date(time);

            Calendar clendar = Calendar.getInstance();
            clendar.setTimeInMillis(time);

            Date startDate = new Date(start);
            Date endDate = new Date(end);

            Log.v(TAG, "Data Point:");
            Log.v(TAG, "\tType " + dataPoint.getDataType().getName());
            Log.v(TAG, "\tStart " + sdf.format(startDate));
            Log.v(TAG, "\tEnd " + sdf.format(endDate));
            for (Field field : dataPoint.getDataType().getFields())
            {
                String fieldName = field.getName();
                Log.v(TAG, "\tField " + fieldName +
                        " Value: " +dataPoint.getValue(field));
            }


        }
    }

    //Magic. Not sure what this is for. Boiler plate code from Google/Android.
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

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(String signal);
    }


}
