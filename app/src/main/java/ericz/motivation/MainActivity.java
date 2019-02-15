package ericz.motivation;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toolbar;

import com.android.billingclient.api.BillingClient;
import com.android.vending.billing.IInAppBillingService;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wallet.IsReadyToPayRequest;
import com.google.android.gms.wallet.PaymentsClient;
import com.google.android.gms.wallet.Wallet;
import com.google.android.gms.wallet.WalletConstants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import ericz.motivation.util.IabHelper;
import ericz.motivation.util.IabResult;

import static java.text.DateFormat.getTimeInstance;

public class MainActivity extends AppCompatActivity implements NewGoalFragment.OnFragmentInteractionListener,
        CurrentGoalFragment.OnFragmentInteractionListener, GoogleApiClient.OnConnectionFailedListener{

    private static final int RC_SIGN_IN = 123;
    private String name;
    private GoogleApiClient mClient;
    int GOOGLE_FIT_PERMISSIONS_REQUEST_CODE = 112;
    private FirebaseUser user;
    private Map data;
    android.support.v4.app.FragmentTransaction fragmentTransaction;
    android.support.v4.app.Fragment fragClass;
    private String runningDataBundleArg;
    private PaymentsClient paymentsClient;
    private BillingClient mBillingClient;
    IabHelper mHelper;
// ...

    // Choose authentication providers

    List<AuthUI.IdpConfig> providers = Arrays.asList(
            new AuthUI.IdpConfig.GoogleBuilder().build());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setActionBar(toolbar);



        //Creating A STRIPE Payments client
        paymentsClient =
                Wallet.getPaymentsClient(this,
                        new Wallet.WalletOptions.Builder().setEnvironment(WalletConstants.ENVIRONMENT_TEST)
                                .build());



        //SETTING ACTIONBAR COLOR
        toolbar.setTitle("Motivation.");
        toolbar.setTitleTextColor(Color.WHITE);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);


        //THIS Lets us get Fitness Data, somehow. NOt sure how it works. But we can currently get the data.
        FitnessOptions fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_ACTIVITY_SAMPLES, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_ACTIVITY_SAMPLES, FitnessOptions.ACCESS_READ)
                .build();

        //CHecking whether the permissions for the last google account have worked.
        if (!GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(this), fitnessOptions)) {
            GoogleSignIn.requestPermissions(
                    this, // your activity
                    GOOGLE_FIT_PERMISSIONS_REQUEST_CODE,
                    GoogleSignIn.getLastSignedInAccount(this),
                    fitnessOptions);
        } else {

        }


        // Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setIsSmartLockEnabled(false)
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);





    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    //Not used
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }




    //This handles the code for the Google sign in - Google OnActivityResult to figure out how this works.

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
//            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                user = FirebaseAuth.getInstance().getCurrentUser();
                Log.v("Successfull sign in", "user id is " + user.getUid());
                // ...

                FitnessOptions fitnessOptions = FitnessOptions.builder()
                        .addDataType(DataType.TYPE_DISTANCE_DELTA, FitnessOptions.ACCESS_READ)
                        .addDataType(DataType.AGGREGATE_DISTANCE_DELTA, FitnessOptions.ACCESS_READ)
                        .addDataType(com.google.android.gms.fitness.data.DataType.TYPE_MOVE_MINUTES, FitnessOptions.ACCESS_READ)
                        .build();

                if (!GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(this), fitnessOptions)) {
                    GoogleSignIn.requestPermissions(
                            this, // your activity
                            GOOGLE_FIT_PERMISSIONS_REQUEST_CODE,
                            GoogleSignIn.getLastSignedInAccount(this),
                            fitnessOptions);
                } else {
                    accessGoogleFit();
                }


                getDatabase();

                //TEMPORARY GUYS
                //TEMPORARY ABOVE
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
                Log.v("Sign In Failed? ", "User didn't get signed in.");
                Log.v("Sign In Result Code", ":" + resultCode);

                //If the result is that the user just clicked back during sign in flow
                if (resultCode == 0)
                {

                }

            }

        }
    }


    //Gets the FireBase database from Google - the one that holds all the Goals Data.

    public void getDatabase()
    {

        final ProgressDialog progressDialog = ProgressDialog.show(this, "A moment please!", "We out here loading");

        Log.v("getting database", "start");

        final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        firestore.setFirestoreSettings(settings);


        CollectionReference collectionReference = firestore.collection("users");
        Log.v("getting database", "db and ref gotten");

        Query query = collectionReference.whereArrayContains("goal", "BqpoiTuEeLVj1IZpUSHy5o3RfL73");
        Log.v("getting database", "sorting started");


        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                Log.v("getting database", "success achieved!");


//                Log.v("firestore test", snapshot.getData().toString());


                //supposed to say if snapshot.exists();
                if (!queryDocumentSnapshots.isEmpty())
                {

                    DocumentSnapshot snapshot = queryDocumentSnapshots.getDocuments().get(0);
                    data = snapshot.getData();
                    Object[] dasArray = new Object[12];
                    dasArray = data.values().toArray();
                    Log.v("data snap array test", dasArray[0].toString());

                    Bundle currentRunningData = new Bundle();

                    currentRunningData.putString("theDataYouNeed", runningDataBundleArg);
                    currentRunningData.putString("key", dasArray[0].toString());

                    fragClass
                            = (android.support.v4.app.Fragment)
                            CurrentGoalFragment.newInstance(currentRunningData);
                    fragmentTransaction = getSupportFragmentManager().beginTransaction();

//                    fragClass.setArguments(currentRunningData);
                    fragmentTransaction.add(R.id.content, fragClass);
                    fragmentTransaction.commit();

                    progressDialog.dismiss();
                }
                else
                {
                    fragClass
                            = (android.support.v4.app.Fragment)
                            NewGoalFragment.newInstance(user.getUid());
                    fragmentTransaction = getSupportFragmentManager().beginTransaction();

                    fragmentTransaction.add(R.id.content, fragClass);
                    fragmentTransaction.commit();

                    progressDialog.dismiss();

                }

            }
        });


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



        mClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Fitness.HISTORY_API)
                .addScope(Fitness.SCOPE_ACTIVITY_READ)
                .build();


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


    private void isReadyToPay() {
        IsReadyToPayRequest request = IsReadyToPayRequest.newBuilder()
                .addAllowedPaymentMethod(WalletConstants.PAYMENT_METHOD_CARD)
                .addAllowedPaymentMethod(WalletConstants.PAYMENT_METHOD_TOKENIZED_CARD)
                .build();
        Task<Boolean> task = paymentsClient.isReadyToPay(request);
        task.addOnCompleteListener(
                new OnCompleteListener<Boolean>() {
                    public void onComplete(Task<Boolean> task) {
                        try {
                            boolean result =
                                    task.getResult(ApiException.class);
                            if(result == true) {
                                //show Google as payment option
                            } else {
                                //hide Google as payment option
                            }
                        } catch (ApiException exception) { }
                    }
                });
    }

    @Override
    public void onFragmentInteraction(String signal) {
        Log.v("frgment intera. test", signal);

        if (signal.equals("newGoalMade"))
        {
            fragmentTransaction  = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.remove(fragClass);
            fragmentTransaction.commit();
            getDatabase();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
