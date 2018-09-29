package il.co.gabel.android.uhcarmel;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import il.co.gabel.android.uhcarmel.Shabat.Shabat;
import il.co.gabel.android.uhcarmel.security.User;

public class ShabatRegisterActivity extends AppCompatActivity {

    private Spinner mShabatSpinner;
    private Switch mShabatSwitch;
    private EditText mShabatAddress;
    private EditText mShabatComment;
    private ArrayAdapter<CharSequence> mSpinnerAdapter;
    private TextView mShabatFirstName;
    private TextView mShabatLastName;
    private TextView mShabatMirs;

    private DatabaseReference mDatabaseReference;
    private ChildEventListener mChildEventListener;
    private static  final  String TAG = ShabatRegisterActivity.class.getSimpleName();
    private String uuid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shabat_register);

        Toolbar toolbar = findViewById(R.id.shabat_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        uuid= Utils.getUserUID(getApplicationContext());
        mShabatSpinner = findViewById(R.id.shabat_cities_spinner);
        mShabatSwitch = findViewById(R.id.shabat_switch);
        mShabatAddress = findViewById(R.id.shabat_address);
        mShabatComment = findViewById(R.id.shabat_comments);
        mShabatFirstName = findViewById(R.id.shabat_first_name_text);
        mShabatLastName = findViewById(R.id.shabat_last_name_text);
        mShabatMirs = findViewById(R.id.shabat_mirs_text);

        mSpinnerAdapter = ArrayAdapter.createFromResource(this,R.array.shabat_cities_array, android.R.layout.simple_spinner_item);
        mSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mShabatSpinner.setAdapter(mSpinnerAdapter);

        User user = Utils.currentUser(getApplicationContext());
        Log.e(TAG, "shabat onCreate: USER: fn: "+user.getFirst_name()+" ln: "+user.getLast_name() );
        mShabatFirstName.setText(user.getFirst_name());
        mShabatLastName.setText(user.getLast_name());
        mShabatMirs.setText(String.valueOf(user.getMirs()));

        mDatabaseReference=Utils.getFBDBReference(getApplicationContext()).child("shabat");
        attachListener();
        setViews(mShabatSwitch.isChecked());
        mShabatSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setViews(isChecked);
            }
        });
    }

    private void updateSettings(){
        Log.e(TAG, "updateSettings: UPDATING!!!" );
        User user = Utils.currentUser(getApplicationContext());
        Shabat shabat = new Shabat(
                mShabatSwitch.isChecked(),
                mShabatAddress.getText().toString(),
                mShabatSpinner.getSelectedItem().toString(),
                mShabatComment.getText().toString(),
                user.getFirst_name()+" "+user.getLast_name(),
                user.getMirs(),
                uuid
        );
        mDatabaseReference.child(shabat.getUuid()).setValue(shabat, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if(databaseError==null){
                    Toast.makeText(getApplicationContext(),getString(R.string.shabat_update_success),Toast.LENGTH_SHORT).show();
                } else {
                    Log.e(TAG, "shabat update onComplete: "+databaseError.getDetails()+"\r\n"+databaseError.getMessage()+"\r\n"+databaseError.getCode() );
                }
            }
        });
    }


    private void attachListener(){
        if(mChildEventListener==null){
            mChildEventListener = new ChildEventListener() {

                private void handleChange(@NonNull DataSnapshot dataSnapshot){
                    Shabat shabat = dataSnapshot.getValue(Shabat.class);
                    if(!shabat.getUuid().equals(uuid)){
                        return;
                    }
                    mShabatAddress.setText(shabat.getAddress());
                    mShabatSpinner.setSelection(mSpinnerAdapter.getPosition(shabat.getCity()));
                    mShabatSwitch.setChecked(shabat.getStatus());
                    mShabatComment.setText(shabat.getCommnet());
                    setViews(shabat.getStatus());
                }


                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    Log.e(TAG, "shabat onChildAdded: "+dataSnapshot.getKey() );
                    handleChange(dataSnapshot);
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    Log.e(TAG, "Shabat details onChildChanged: "+dataSnapshot.getKey() );
                    handleChange(dataSnapshot);
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };
            mDatabaseReference.addChildEventListener(mChildEventListener);
        }

    }
    @Override
    protected void onResume() {
        super.onResume();
        attachListener();
    }

    @Override
    protected void onPause() {
        super.onPause();
        updateSettings();
        mChildEventListener=null;
    }

    private void setViews(Boolean state){
        mShabatSpinner.setEnabled(state);
        mShabatAddress.setEnabled(state);
        mShabatComment.setEnabled(state);
    }
}
