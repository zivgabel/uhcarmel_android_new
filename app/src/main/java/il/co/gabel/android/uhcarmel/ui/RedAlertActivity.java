package il.co.gabel.android.uhcarmel.ui;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.util.Strings;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import il.co.gabel.android.uhcarmel.R;
import il.co.gabel.android.uhcarmel.UploadMessageAsyncTask;
import il.co.gabel.android.uhcarmel.Utils;
import il.co.gabel.android.uhcarmel.firebase.objects.redalert.ImageInfo;
import il.co.gabel.android.uhcarmel.firebase.objects.redalert.RedAlertMessage;
import il.co.gabel.android.uhcarmel.security.UHFireBaseManager;
import il.co.gabel.android.uhcarmel.ui.adapters.RedAlertImagesAdapter;

public class RedAlertActivity extends AppCompatActivity  {
    private static final String TAG = RedAlertImagesAdapter.class.getSimpleName();
    private Button mImagePickerButton;
    private static final int PICK_IMAGE_MULTIPLE = 1;
    private String mImageEncoded;
    private List<String> mImagesEncodedList;
    private RecyclerView mImagesRecyclerView;
    private RedAlertImagesAdapter mRedAlertImagesAdapter;
    private EditText mFirstNameEditText;
    private EditText mLastNameEditText;
    private EditText mContentTextView;

    private FusedLocationProviderClient mFusedLocationClient;
    private Location mLastLocation;
    private final List<Uri> messageImages = new ArrayList<>();
    private String senfUUID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_red_alert);
        Toolbar toolbar = (Toolbar) findViewById(R.id.ra_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mImagePickerButton = findViewById(R.id.image_picker_button);
        mImagePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"), PICK_IMAGE_MULTIPLE);
            }
        });

        mImagesRecyclerView = findViewById(R.id.ra_images_recyclerview);
        mRedAlertImagesAdapter = new RedAlertImagesAdapter();
        mImagesRecyclerView.setAdapter(mRedAlertImagesAdapter);

        mFirstNameEditText = findViewById(R.id.ra_first_name_et);
        mLastNameEditText = findViewById(R.id.ra_last_name_et);
        mContentTextView = findViewById(R.id.ra_content_et);
        if(UHFireBaseManager.getUser()!=null){
            mFirstNameEditText.setText(UHFireBaseManager.getUser().getFirstName());
            mLastNameEditText.setText(UHFireBaseManager.getUser().getLastName());
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.red_alert_menu, menu);
        return true;
    }




    @SuppressLint("MissingPermission")
    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        int itemId = item.getItemId();
        if(itemId==R.id.ra_menu_send){
            if(Strings.isEmptyOrWhitespace(mFirstNameEditText.getText().toString())){
                //mFirstNameEditText.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
                mFirstNameEditText.setError(getString(R.string.ra_fn_required));
                return false;
            }
            if(Strings.isEmptyOrWhitespace(mLastNameEditText.getText().toString())){
                //mFirstNameEditText.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
                mLastNameEditText.setError(getString(R.string.ra_ln_required));
                return false;
            }
            if(Strings.isEmptyOrWhitespace(mContentTextView.getText().toString())){
                //mFirstNameEditText.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
                mContentTextView.setError(getString(R.string.ra_content_required));
                return false;
            }

            RedAlertMessage message = new RedAlertMessage();
            message.setFirstName(mFirstNameEditText.getText().toString());
            message.setLastName(mLastNameEditText.getText().toString());
            message.setContent(mContentTextView.getText().toString());
            final DatabaseReference redAlertReference = Utils.getFBDBReference(this).child("redalert");
            final String messagekey = redAlertReference.push().getKey();
            redAlertReference.child(messagekey).child("message").setValue(message);
            getLocation(messagekey);
            for(final Uri imageUri : mRedAlertImagesAdapter.getmImagesUriList()) {

                final StorageReference ref = FirebaseStorage.getInstance().getReference(getString(R.string.red_alert_folder) + UUID.randomUUID().toString());
                try {
                    InputStream inputStream = getContentResolver().openInputStream(imageUri);
                    UploadTask uploadTask = ref.putStream(inputStream);
                    Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {

                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }

                            // Continue with the task to get the download URL
                            return ref.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadUri = task.getResult();
                                ImageInfo info = new ImageInfo(downloadUri.toString());
                                Log.e(TAG, "onComplete: url for file" + downloadUri.toString());
                                redAlertReference.child(messagekey).child("images").push().setValue(info);
                            } else {
                                Log.e(TAG, "onComplete: unable to upload file" + imageUri.toString());
                            }
                        }
                    });
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

            }
            onBackPressed();
            return true;

        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @SuppressLint("MissingPermission")
    private void getLocation(final String messageKey){
        FusedLocationProviderClient fusedLocationClient;
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        Task<Location> locationTask =  fusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    mLastLocation = task.getResult();
                } else {
                    mLastLocation = null;
                }
                if(mLastLocation!=null) {
                    final DatabaseReference locationRef = Utils.getFBDBReference(getApplicationContext()).child("redalert").child(messageKey).child("locations");
                    locationRef.setValue(new il.co.gabel.android.uhcarmel.firebase.objects.locations.Location(mLastLocation.getAltitude(),mLastLocation.getLongitude()));
                }
            }
        });
    }





    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            // When an Image is picked
            if (requestCode == PICK_IMAGE_MULTIPLE && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data

                String[] filePathColumn = { MediaStore.Images.Media.DATA };
                mImagesEncodedList = new ArrayList<String>();
                if(data.getData()!=null){

                    Uri mImageUri=data.getData();
                    mRedAlertImagesAdapter.addImage(mImageUri);
                    // Get the cursor
                    Cursor cursor = getContentResolver().query(mImageUri,
                            filePathColumn, null, null, null);
                    // Move to first row
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    mImageEncoded  = cursor.getString(columnIndex);
                    cursor.close();




                } else {
                    if (data.getClipData() != null) {
                        ClipData mClipData = data.getClipData();
                        ArrayList<Uri> mArrayUri = new ArrayList<Uri>();
                        for (int i = 0; i < mClipData.getItemCount(); i++) {

                            ClipData.Item item = mClipData.getItemAt(i);
                            Uri uri = item.getUri();
                            mArrayUri.add(uri);
                            // Get the cursor
                            Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
                            // Move to first row
                            cursor.moveToFirst();

                            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                            mImageEncoded  = cursor.getString(columnIndex);
                            mImagesEncodedList.add(mImageEncoded);
                            cursor.close();

                        }
                        Log.e("LOG_TAG", "Selected Images " + mArrayUri.size());
                        mRedAlertImagesAdapter.addImagesList(mArrayUri);
                    }
                }
            } else {
                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

}
