package il.co.gabel.android.uhcarmel.ui.ui.redalertlist;

import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import il.co.gabel.android.uhcarmel.Utils;
import il.co.gabel.android.uhcarmel.firebase.objects.CompleteRedAlertMessage;
import il.co.gabel.android.uhcarmel.ui.adapters.RedAlertListAdapter;

public class RedAlertListViewModel extends ViewModel {
    private RedAlertListAdapter adapter;
    private DatabaseReference reference;
    private ChildEventListener listener;
    private int position;


    public RedAlertListViewModel() {
        super();
        adapter = new RedAlertListAdapter(new ArrayList<CompleteRedAlertMessage>());
        listener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                adapter.addMessage(dataSnapshot.getValue(CompleteRedAlertMessage.class));
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
    }

    public void setReference(DatabaseReference reference) {
        if(this.reference!=null){
            return;
        }
        this.reference = reference;
        this.reference.addChildEventListener(listener);
    }

    public RedAlertListAdapter getAdapter() {
        return adapter;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
