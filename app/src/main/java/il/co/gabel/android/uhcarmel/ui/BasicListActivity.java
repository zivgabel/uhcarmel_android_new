package il.co.gabel.android.uhcarmel.ui;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import il.co.gabel.android.uhcarmel.Utils;
import il.co.gabel.android.uhcarmel.ui.adapters.BasicAdapter;
import il.co.gabel.android.uhcarmel.ui.holders.BasicHolder;

public abstract class BasicListActivity<H extends BasicHolder, I extends Object> extends AppCompatActivity {
    private static final String TAG = BasicListActivity.class.getSimpleName();
    protected RecyclerView recyclerView;
    protected DatabaseReference reference;
    protected ChildEventListener listener;
    protected BasicAdapter<I, H> adapter;

    abstract String getDatabasePath();
    abstract int getRecyclerId();
    abstract BasicAdapter getAdapter();


    protected void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        adapter.addItemUnique(((I)dataSnapshot.getValue()));

    }

    protected void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

    }

    protected void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
        adapter.removeItem((I)dataSnapshot.getValue());
    }

    protected void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

    }

    protected void onCancelled(@NonNull DatabaseError databaseError) {
        Log.e(TAG, "onCancelled: "+databaseError.getMessage() );
    }


    protected void attachListener(){
        if(reference==null){
            reference=Utils.getFBDBReference(getApplicationContext()).child(getDatabasePath());
        }
        if(listener==null) {
            listener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    Log.e(TAG, "onChildAdded: "+dataSnapshot.getKey());
                    Log.e(TAG, "onChildAdded: " + ((I)dataSnapshot.getValue()).toString() );

                    BasicListActivity.this.onChildAdded(dataSnapshot,s);
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    BasicListActivity.this.onChildChanged(dataSnapshot,s);
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                    BasicListActivity.this.onChildRemoved(dataSnapshot);
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    BasicListActivity.this.onChildMoved(dataSnapshot,s);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    BasicListActivity.this.onCancelled(databaseError);
                }
            };
        }
        Log.e(TAG, "attachListener: Adding child listener" );
        reference.addChildEventListener(listener);
    }

    protected void detachListener(){
        Log.e(TAG, "detachListener: ");
        if(listener!=null){
            reference.removeEventListener(listener);
        }
        listener=null;
        reference=null;
    }


    @Override
    protected void onResume() {
        if(reference==null) {
            reference = Utils.getFBDBReference(getApplicationContext()).child(getDatabasePath());
        }
        if(adapter==null) {
            adapter = getAdapter();
        }
        if(recyclerView==null) {
            recyclerView = findViewById(getRecyclerId());
        }
        Log.e(TAG, "onResume: Setting adapter" );
        recyclerView.setAdapter(adapter);
        attachListener();
        super.onResume();
        attachListener();
    }

    @Override
    protected void onPause() {
        super.onPause();
        detachListener();
    }
}
