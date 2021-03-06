package il.co.gabel.android.uhcarmel.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import il.co.gabel.android.uhcarmel.BuildConfig;
import il.co.gabel.android.uhcarmel.R;
import il.co.gabel.android.uhcarmel.security.UHFireBaseManager;
import il.co.gabel.android.uhcarmel.security.User;

public class MainActivity extends AppCompatActivity implements UHFireBaseManager.AuthenticationListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;


    private Button mButtonCall1221;
    private Button mButtonSendMyLocation;
    private Button mButtonLocations;
    private Button mButtonEmsPocket;
    private Button mButtonShabatRegister;
    private Button mButtonOrders;
    private Button mButtonWarehouseAdmin;
    private Button mButtonShabatAdmin;
    private Button mButtonReportCase;
    private FloatingActionButton mRedAlertFab;
    private Button mButtonRedAlertAdmin;

    private UHFireBaseManager fireBaseManager;
    private FusedLocationProviderClient mFusedLocationClient;
    private Location mLastLocation;
    private ChildEventListener mGeneralListener;
    private String CASE_REPORT_URL = "https://motid-1221.formtitan.com/Medical_journal";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FirebaseApp.initializeApp(this);
        fireBaseManager = new UHFireBaseManager(MainActivity.this, this);
        fireBaseManager.getUserDetails();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        setGeneralListener();
        setButtons();
        requestPermissions();
        mRedAlertFab = findViewById(R.id.red_alert_fab);
        mRedAlertFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createIntent(RedAlertActivity.class);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        fireBaseManager.getUserDetails();
        setGeneralListener();
    }

    @Override
    protected void onPause() {
        super.onPause();
        fireBaseManager.removeFireBaseUserListener();
        fireBaseManager.removeGeneralConfigListener(mGeneralListener);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.e(TAG, "onCreateOptionsMenu: ");
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        MenuItem signin = menu.findItem(R.id.signin);
        MenuItem signout = menu.findItem(R.id.signout);
        if (fireBaseManager.isFireBaseUserExists()) {
            signin.setVisible(false);
            signout.setVisible(true);
        } else {
            signin.setVisible(true);
            signout.setVisible(false);
        }
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.signin:
                fireBaseManager.authenticate();
                return true;
            case R.id.signout:
                fireBaseManager.signout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setMenuVisibility() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        Menu menu = toolbar.getMenu();
        if (menu != null) {
            Log.e(TAG, "setViewsVisibilty: menu is available");
            MenuItem signin = menu.findItem(R.id.signin);
            MenuItem signout = menu.findItem(R.id.signout);
            if (signin != null && signout != null) {
                if (fireBaseManager.isFireBaseUserExists()) {
                    signin.setVisible(false);
                    signout.setVisible(true);
                } else {
                    signin.setVisible(true);
                    signout.setVisible(false);
                }
            }
        } else {
            Log.e(TAG, "setViewsVisibilty: menu is not available");
        }
    }

    private void setViewsVisibilty() {
        User user = UHFireBaseManager.getUser();
        Log.e(TAG, "setViewsVisibilty: User: " + user);
        setMenuVisibility();
        if (user == null) {
            mButtonShabatAdmin.setVisibility(View.GONE);
            mButtonOrders.setVisibility(View.GONE);
            mButtonShabatRegister.setVisibility(View.GONE);
            mButtonWarehouseAdmin.setVisibility(View.GONE);
            mButtonReportCase.setVisibility(View.GONE);
            mButtonEmsPocket.setVisibility(View.GONE);
            return;
        }
        mButtonReportCase.setVisibility(View.VISIBLE);
        mButtonOrders.setVisibility(View.VISIBLE);
        mButtonShabatRegister.setVisibility(View.VISIBLE);
        mButtonEmsPocket.setVisibility(View.VISIBLE);
        if (user.getPermissions().getWarehouse()) {
            mButtonWarehouseAdmin.setVisibility(View.VISIBLE);
        } else {
            mButtonWarehouseAdmin.setVisibility(View.GONE);
        }
        if (user.getPermissions().getShabat()) {
            mButtonShabatAdmin.setVisibility(View.VISIBLE);
        } else {
            mButtonShabatAdmin.setVisibility(View.GONE);
        }


    }


    private void setButtons() {
        mRedAlertFab = findViewById(R.id.red_alert_fab);
        mButtonReportCase = findViewById(R.id.button_report_case);
        mButtonCall1221 = findViewById(R.id.button_call_1221);
        mButtonSendMyLocation = findViewById(R.id.button_send_my_location);
        mButtonLocations = findViewById(R.id.button_locations);
        mButtonEmsPocket = findViewById(R.id.button_ems_pocket);
        mButtonShabatRegister = findViewById(R.id.button_shabat_register);
        mButtonOrders = findViewById(R.id.button_new_order);
        mButtonWarehouseAdmin = findViewById(R.id.button_warehouse_admin);
        mButtonShabatAdmin = findViewById(R.id.button_shabat_admin);
        mButtonRedAlertAdmin = findViewById(R.id.button_red_alert_admin);
        mButtonLocations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createIntent(LocationsActivity.class);
            }
        });
        mButtonOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createIntent(OrdersActivity.class);
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
        mButtonRedAlertAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createIntent(RedAlertListActivity.class);
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

    private void createIntent(Class className) {
        Intent intent = new Intent(MainActivity.this, className);
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
                            final il.co.gabel.android.uhcarmel.firebase.objects.locations.Location location = new il.co.gabel.android.uhcarmel.firebase.objects.locations.Location(getString(R.string.my_location), mLastLocation.getAltitude(), mLastLocation.getLongitude());
                            showSnackbar(R.string.location_found, R.string.send_location_question, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String message = location.getName() + "\r\n" + location.getWazeUrl();
                                    Intent intent = new Intent();
                                    intent.setAction(Intent.ACTION_SEND);
                                    intent.putExtra(Intent.EXTRA_TEXT, message);
                                    intent.setType("text/plain");
                                    if (intent.resolveActivity(v.getContext().getPackageManager()) != null) {
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

    private void setGeneralListener() {

        if (mGeneralListener == null) {
            mGeneralListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    Log.e(TAG, "Child Added");
                    doAction(dataSnapshot.getKey(), dataSnapshot.getValue());
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    Log.e(TAG, "Child Changed");
                    doAction(dataSnapshot.getKey(), dataSnapshot.getValue());

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                    Log.e(TAG, "Child Removed");
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    Log.e(TAG, "Child Moved");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e(TAG, "Child Cancelled");
                    Log.e(TAG, databaseError.getMessage());
                }

                private void doAction(String key, Object value) {
                    switch (key) {
                        case "cases_form":
                            CASE_REPORT_URL = value.toString();
                            setButtons();
                            break;
                    }
                }
            };
        }
        fireBaseManager.addGeneralConfigListener(mGeneralListener);
    }

    @Override
    public void userAdded() {
        setViewsVisibilty();
    }

    @Override
    public void userRemoved() {
        setViewsVisibilty();
    }

    @Override
    public void userChanged() {
        setViewsVisibilty();
    }

    @Override
    public void fireBaseUserSignedIn() {
        setViewsVisibilty();
    }

    @Override
    public void fireBaseUserSignedOut() {
        setMenuVisibility();
    }
}
