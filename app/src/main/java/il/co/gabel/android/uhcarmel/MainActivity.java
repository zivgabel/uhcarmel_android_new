package il.co.gabel.android.uhcarmel;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Arrays;
import java.util.Objects;

import il.co.gabel.android.uhcarmel.security.User;

public class MainActivity extends AppCompatActivity {

    private static final String TAG=MainActivity.class.getSimpleName();
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    private static final int RC_SIGN_IN = 1;
    private static final String CASE_REPORT_URL = "https://www.tfaforms.com/4645989";

    private Button mButtonCall1221;
    private Button mButtonSendMyLocation;
    private Button mButtonLocations;
    private Button mButtonEmsPocket;
    private Button mButtonShabatRegister;
    private Button mButtonNewOrder;
    private Button mButtonWarehouseAdmin;
    private Button mButtonShabatAdmin;
    private Button mButtonReportCase;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private ChildEventListener listener;
    private FirebaseAuth.AuthStateListener authStateListener;

    private FusedLocationProviderClient mFusedLocationClient;
    private Location mLastLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FirebaseApp.initializeApp(this);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        setButtons();
        setWebview();
        handleAuthentication();
        getUserDetails();
        setViewsVisibilty();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setWebview();
        handleAuthentication();
        attachListeners();
    }

    private void setWebview(){
        WebView webView = findViewById(R.id.facebook_webview);
        webView.setVerticalScrollBarEnabled(true);
        String html ="<html><body><iframe src=\"https://www.facebook.com/plugins/page.php?href=https%3A%2F%2Fwww.facebook.com%2FIcodHazala%2F&tabs=timeline&width=340&height=500&small_header=true&adapt_container_width=true&hide_cover=true&show_facepile=true&appId=1925799054173754\" width=\"340\" height=\"500\" style=\"border:none;overflow:hidden\" scrolling=\"no\" frameborder=\"0\" allowTransparency=\"true\" allow=\"encrypted-media\"></iframe></body></html>";
        String encodedHtml = Base64.encodeToString(html.getBytes(),Base64.NO_PADDING);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webView.loadData(encodedHtml,"text/html", "base64");
    }

    private void handleAuthentication() {
        if(firebaseAuth==null) {
            firebaseAuth = FirebaseAuth.getInstance();
        }
        if (authStateListener == null) {
            authStateListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    if (user != null) {
                        onSignedInInitialize(user);
                    } else {
                        onSignedOutCleanup();
                        startActivityForResult(
                                AuthUI.getInstance()
                                        .createSignInIntentBuilder()
                                        .setAvailableProviders(Arrays.asList(
                                                new AuthUI.IdpConfig.EmailBuilder().build(),
                                                new AuthUI.IdpConfig.GoogleBuilder().build()
                                        )).build(),
                                RC_SIGN_IN);
                    }
                }
            };
            firebaseAuth.addAuthStateListener(authStateListener);
        }
    }

    private void onSignedOutCleanup(){
        User user = Utils.currentUser(getApplicationContext());
        if(user!=null){
            user.removeData(getApplicationContext());
        }
        setViewsVisibilty();
    }

    private void onSignedInInitialize(FirebaseUser user) {
        SharedPreferences.Editor editor = Utils.getSharedPreferencesEditor(getApplicationContext());
        editor.putString(getString(R.string.display_name_key),user.getDisplayName());
        editor.putString(getString(R.string.user_uid_key),user.getUid());
        editor.commit();
        getUserDetails();
    }
    private void getUserDetails(){
        if(databaseReference==null) {
            databaseReference = Utils.getFBDBReference(getApplicationContext()).child("users").child(Utils.getUserUID(getApplicationContext()));
        }
        attachListeners();
    }

    private void attachListeners(){
        if(listener==null) {
            listener = new ChildEventListener() {
                SharedPreferences sp = Utils.getSharedPreferences(getApplicationContext());

                private void deleteHandler(@NonNull DataSnapshot dataSnapshot){
                    User user = dataSnapshot.getValue(User.class);
                    Context context = getApplicationContext();
                    user.removeData(context);
                    setViewsVisibilty();
                }

                private void changeHandler(@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    Log.e(TAG, "user data changeHandler: "+user.getFirst_name()+" "+user.getLast_name() );
                    Context context = getApplicationContext();
                    user.saveData(context);
                    subscribeToOrdersTopic(user);
                    setViewsVisibilty();
                }


                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    Log.e(TAG, "onChildAdded: UID " + dataSnapshot.getKey());
                    if(dataSnapshot.getKey().equals("user")) {
                        changeHandler(dataSnapshot);
                    }
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    Log.e(TAG, "onChildChanged: UID " + dataSnapshot.getKey());
                    if(Objects.equals(dataSnapshot.getKey(), "user")) {
                        changeHandler(dataSnapshot);
                    }
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                    Log.e(TAG, "onChildRemoved: UID " + dataSnapshot.getKey());
                    if(dataSnapshot.getKey()=="user") {
                        deleteHandler(dataSnapshot);
                    }
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    Log.e(TAG, "onChildMoved: UID " + dataSnapshot.getKey());
                    if(dataSnapshot.getKey()=="user") {
                        deleteHandler(dataSnapshot);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };
            databaseReference.addChildEventListener(listener);

        }
    }

    private void subscribeToOrdersTopic(User user){
        FirebaseMessaging.getInstance().subscribeToTopic(getString(R.string.new_order_user_topic_prefix)+String.valueOf(user.getMirs()))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "Subscribed";
                        if (!task.isSuccessful()) {
                            msg = "Not subscribed";
                        }
                        Log.d(TAG, msg);
                    }
                });
        if(Utils.currentUser(getApplicationContext()).getWh_admin()){
            FirebaseMessaging.getInstance().subscribeToTopic(getString(R.string.new_order_topic)+user.getWh_admin_branch())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            String msg = "Subscribe to orders";
                            if (!task.isSuccessful()) {
                                msg = "Not subscribed to orders";
                            }
                            Log.d(TAG, msg);
                        }
                    });
        }
    }


    private void setViewsVisibilty(){
        User user = Utils.currentUser(this);
        Log.e(TAG, "setViewsVisibilty: User: "+user );
        if(user==null){
            mButtonShabatAdmin.setVisibility(View.GONE);
            mButtonNewOrder.setVisibility(View.GONE);
            mButtonShabatRegister.setVisibility(View.GONE);
            mButtonWarehouseAdmin.setVisibility(View.GONE);
            mButtonReportCase.setVisibility(View.GONE);
            mButtonEmsPocket.setVisibility(View.GONE);
            return;
        }
        mButtonReportCase.setVisibility(View.VISIBLE);
        mButtonNewOrder.setVisibility(View.VISIBLE);
        mButtonShabatRegister.setVisibility(View.VISIBLE);
        mButtonEmsPocket.setVisibility(View.VISIBLE);
        if(user.getWh_admin()){
            mButtonWarehouseAdmin.setVisibility(View.VISIBLE);
        }
        if(user.getShabat_admin()){
            mButtonShabatAdmin.setVisibility(View.VISIBLE);
        }
    }


    private void setButtons(){
        mButtonReportCase = findViewById(R.id.button_report_case);
        mButtonCall1221 = findViewById(R.id.button_call_1221);
        mButtonSendMyLocation = findViewById(R.id.button_send_my_location);
        mButtonLocations = findViewById(R.id.button_locations);
        mButtonEmsPocket = findViewById(R.id.button_ems_pocket);
        mButtonShabatRegister = findViewById(R.id.button_shabat_register);
        mButtonNewOrder = findViewById(R.id.button_new_order);
        mButtonWarehouseAdmin = findViewById(R.id.button_warehouse_admin);
        mButtonShabatAdmin = findViewById(R.id.button_shabat_admin);
        mButtonLocations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createIntent(LocationsActivity.class);
            }
        });
        mButtonNewOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createIntent(NewOrderActivity.class);
            }
        });
        mButtonShabatRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createIntent(ShabatRegisterActivity.class);
            }
        });
        mButtonShabatAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createIntent(ShabatListActivity.class);
            }
        });
        mButtonWarehouseAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createIntent(OrderListActivity.class);
            }
        });
        mButtonEmsPocket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createIntent(PocketPDFActivity.class);
            }
        });
        mButtonSendMyLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLastLocation();
            }
        });
        mButtonCall1221.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = getString(R.string.united_phone_number);
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                startActivity(intent);
            }
        });

        mButtonReportCase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(CASE_REPORT_URL));
                startActivity(i);
            }
        });

    }

    private void createIntent(Class className){
        Intent intent = new Intent(MainActivity.this,className);
        startActivity(intent);
    }

    private void startLocationPermissionRequest() {
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                REQUEST_PERMISSIONS_REQUEST_CODE);
    }
    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");

            showSnackbar(R.string.permission_rationale, android.R.string.ok,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            startLocationPermissionRequest();
                        }
                    });

        } else {
            Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            startLocationPermissionRequest();
        }
    }
    /**
     * Shows a {@link Snackbar} using {@code text}.
     *
     * @param text The Snackbar text.
     */
    private void showSnackbar(final String text) {
        View container = findViewById(R.id.content_main_nested_scroll_view);
        if (container != null) {
            Snackbar.make(container, text, Snackbar.LENGTH_LONG).show();
        }
    }

    /**
     * Shows a {@link Snackbar}.
     *
     * @param mainTextStringId The id for the string resource for the Snackbar text.
     * @param actionStringId   The text of the action item.
     * @param listener         The listener associated with the Snackbar action.
     */
    private void showSnackbar(final int mainTextStringId, final int actionStringId,
                              View.OnClickListener listener) {
        Snackbar.make(findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
    }


    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.");
            } else //noinspection StatementWithEmptyBody
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted.
                //getLastLocation();
            } else {
                // Permission denied.

                // Notify the user via a SnackBar that they have rejected a core permission for the
                // app, which makes the Activity useless. In a real app, core permissions would
                // typically be best requested during a welcome-screen flow.

                // Additionally, it is important to remember that a permission might have been
                // rejected without asking the user for permission (device policy or "Never ask
                // again" prompts). Therefore, a user interface affordance is typically implemented
                // when permissions are denied. Otherwise, your app could appear unresponsive to
                // touches or interactions which have required permissions.
                showSnackbar(R.string.permission_denied_explanation, R.string.settings,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Build intent that displays the App settings screen.
                                Intent intent = new Intent();
                                intent.setAction(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package",
                                        BuildConfig.APPLICATION_ID, null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });
            }
        }
    }



    @SuppressWarnings("MissingPermission")
    private void getLastLocation() {
        mFusedLocationClient.getLastLocation()
                .addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            mLastLocation = task.getResult();
                            final il.co.gabel.android.uhcarmel.locations.Location location = new il.co.gabel.android.uhcarmel.locations.Location(getString(R.string.my_location),mLastLocation.getAltitude(),mLastLocation.getLongitude());
                            showSnackbar(R.string.location_found, R.string.send_location_question, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String message = location.getName()+"\r\n"+location.getWazeUrl();
                                    Intent intent = new Intent();
                                    intent.setAction(Intent.ACTION_SEND);
                                    intent.putExtra(Intent.EXTRA_TEXT,message);
                                    intent.setType("text/plain");
                                    if(intent.resolveActivity(v.getContext().getPackageManager())!=null) {
                                        v.getContext().startActivity(intent);
                                    }
                                }
                            });
                        } else {
                            Log.w(TAG, "getLastLocation:exception", task.getException());
                            showSnackbar(getString(R.string.no_location_detected));
                        }
                    }
                });
    }



    @Override
    protected void onPause() {
        super.onPause();
        databaseReference.removeEventListener(listener);
        listener=null;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                getUserDetails();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Sign in canceled", Toast.LENGTH_SHORT).show();
                finish();
            }
            if (!checkPermissions()) {
                requestPermissions();
            }
        }
    }
}
