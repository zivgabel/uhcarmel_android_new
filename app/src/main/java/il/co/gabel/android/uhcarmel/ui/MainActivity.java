package il.co.gabel.android.uhcarmel.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;

import il.co.gabel.android.uhcarmel.BuildConfig;
import il.co.gabel.android.uhcarmel.R;
import il.co.gabel.android.uhcarmel.security.UHFireBaseManager;
import il.co.gabel.android.uhcarmel.security.User;

public class MainActivity extends AppCompatActivity implements UHFireBaseManager.AuthenticationListener {

    private static final String TAG=MainActivity.class.getSimpleName();
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    private static final int RC_SIGN_IN = 1;
    private static final String CASE_REPORT_URL = "https://www.tfaforms.com/4645989";

    private Button mButtonCall1221;
    private Button mButtonSendMyLocation;
    private Button mButtonLocations;
    private Button mButtonEmsPocket;
    private Button mButtonShabatRegister;
    private Button mButtonOrders;
    private Button mButtonWarehouseAdmin;
    private Button mButtonShabatAdmin;
    private Button mButtonReportCase;


    private UHFireBaseManager authenticationManager;
    private FusedLocationProviderClient mFusedLocationClient;
    private Location mLastLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FirebaseApp.initializeApp(this);
        setButtons();
        authenticationManager = new UHFireBaseManager(MainActivity.this,this);
        authenticationManager.getUserDetails();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        setWebview();
        requestPermissions();
    }

    @Override
    protected void onResume() {
        super.onResume();
        authenticationManager.getUserDetails();
        setWebview();
    }

    @Override
    protected void onPause() {
        super.onPause();
        authenticationManager.removeFireBaseUserListener();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.e(TAG, "onCreateOptionsMenu: ");
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        MenuItem signin = menu.findItem(R.id.signin);
        MenuItem signout = menu.findItem(R.id.signout);
        if(authenticationManager.isFireBaseUserExists()){
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
                authenticationManager.authenticate();
                return true;
            case R.id.signout:
                authenticationManager.signout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setMenuVisibility(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        Menu menu = toolbar.getMenu();
        if(menu!=null){
            Log.e(TAG, "setViewsVisibilty: menu is available" );
            MenuItem signin = menu.findItem(R.id.signin);
            MenuItem signout = menu.findItem(R.id.signout);
            if(signin!=null && signout!=null) {
                if (authenticationManager.isFireBaseUserExists()) {
                    signin.setVisible(false);
                    signout.setVisible(true);
                } else {
                    signin.setVisible(true);
                    signout.setVisible(false);
                }
            }
        } else {
            Log.e(TAG, "setViewsVisibilty: menu is not available" );
        }
    }
    private void setViewsVisibilty(){
        User user = authenticationManager.getUser();
        Log.e(TAG, "setViewsVisibilty: User: "+user );
        setMenuVisibility();
        if(user==null){
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
        if(user.getPermissions().getWarehouse()){
            mButtonWarehouseAdmin.setVisibility(View.VISIBLE);
        } else {
            mButtonWarehouseAdmin.setVisibility(View.GONE);
        }
        if(user.getPermissions().getShabat()){
            mButtonShabatAdmin.setVisibility(View.VISIBLE);
        } else {
            mButtonShabatAdmin.setVisibility(View.GONE);
        }


    }


    private void setButtons(){
        mButtonReportCase = findViewById(R.id.button_report_case);
        mButtonCall1221 = findViewById(R.id.button_call_1221);
        mButtonSendMyLocation = findViewById(R.id.button_send_my_location);
        mButtonLocations = findViewById(R.id.button_locations);
        mButtonEmsPocket = findViewById(R.id.button_ems_pocket);
        mButtonShabatRegister = findViewById(R.id.button_shabat_register);
        mButtonOrders = findViewById(R.id.button_new_order);
        mButtonWarehouseAdmin = findViewById(R.id.button_warehouse_admin);
        mButtonShabatAdmin = findViewById(R.id.button_shabat_admin);
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
                            final il.co.gabel.android.uhcarmel.firebase.objects.locations.Location location = new il.co.gabel.android.uhcarmel.firebase.objects.locations.Location(getString(R.string.my_location),mLastLocation.getAltitude(),mLastLocation.getLongitude());
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
