package il.co.gabel.android.uhcarmel;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

import il.co.gabel.android.uhcarmel.Shabat.Shabat;
import il.co.gabel.android.uhcarmel.Shabat.ShabatListAdapter;

public class ShabatListActivity extends AppCompatActivity {

    private RecyclerView mShabatListRecyclerView;
    private ShabatListAdapter mShabatListAdapter;
    private DatabaseReference mReference;
    private ChildEventListener mChildEventListener;
    private static final String TAG=ShabatListActivity.class.getSimpleName();
    private LinearLayoutManager mShabatLayoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shabat_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mShabatLayoutManager =  new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL,false);
        mShabatListRecyclerView = findViewById(R.id.shabat_list_recycler_view);
        DividerItemDecoration decoration = new DividerItemDecoration(mShabatListRecyclerView.getContext(),LinearLayoutManager.VERTICAL);
        mShabatListRecyclerView.setLayoutManager(mShabatLayoutManager);
        mShabatListRecyclerView.addItemDecoration(decoration);
        ArrayList<Shabat> shabat_list= new ArrayList<>();
        mShabatListAdapter = new ShabatListAdapter(shabat_list);
        mShabatListRecyclerView.setAdapter(mShabatListAdapter);
        mReference = Utils.getFBDBReference(getApplicationContext()).child("shabat");
        attachListener();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.shabat_list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if(itemId==R.id.shabat_list_action_delete){
            DatabaseReference reference = Utils.getFBDBReference(getApplicationContext()).child("shabat");
            ChildEventListener listener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    Shabat shabat = dataSnapshot.getValue(Shabat.class);
                    shabat.setStatus(false);
                    DatabaseReference tempReference1 = Utils.getFBDBReference(getApplicationContext()).child("shabat");
                    tempReference1.child(dataSnapshot.getKey()).setValue(shabat);
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

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
            reference.addChildEventListener(listener);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        attachListener();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mChildEventListener!=null){
            mReference.removeEventListener(mChildEventListener);
            mChildEventListener=null;
        }
    }

    private void attachListener(){
        if(mChildEventListener==null){
            mChildEventListener = new ChildEventListener() {

                private void handleChange(@NonNull DataSnapshot dataSnapshot){
                    Shabat shabat = dataSnapshot.getValue(Shabat.class);
                    Log.e(TAG, "handleChange: "+shabat.getUuid() );
                    mShabatListAdapter.shabatItemChanged(shabat);
                }
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    Log.e(TAG, "Shabat list onChildAdded: ");
                    handleChange(dataSnapshot);
                }
                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    Log.e(TAG, "Shabat list onChildChanged: " );
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
            mReference.addChildEventListener(mChildEventListener);
        }
    }



}
