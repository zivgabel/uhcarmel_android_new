package il.co.gabel.android.uhcarmel.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

import il.co.gabel.android.uhcarmel.R;
import il.co.gabel.android.uhcarmel.Utils;
import il.co.gabel.android.uhcarmel.firebase.objects.warehouse.Order;
import il.co.gabel.android.uhcarmel.security.BasicAuthenticationListener;
import il.co.gabel.android.uhcarmel.security.UHFireBaseManager;
import il.co.gabel.android.uhcarmel.ui.adapters.OrderListAdapter;

public class OrderListActivity extends AppCompatActivity {
    private static final String TAG=OrderListActivity.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private OrderListAdapter mAdapter;
    private DatabaseReference mDatabaseReference;
    private ChildEventListener mListener;
    private UHFireBaseManager manager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());
        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        manager = new UHFireBaseManager(OrderListActivity.this,new BasicAuthenticationListener(OrderListActivity.this));
        mRecyclerView = findViewById(R.id.order_list);
        setupRecyclerView(mRecyclerView);
        attachListener();

    }
    @Override
    protected void onResume() {
        mAdapter.clear();
        super.onResume();
        attachListener();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mDatabaseReference!=null){
            mDatabaseReference.removeEventListener(mListener);
        }
        mListener=null;

    }

    private void attachListener(){
        mDatabaseReference = Utils.getFBDBReference(getApplicationContext()).child("orders");
        if(mListener ==null){
            mListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    Order order = dataSnapshot.getValue(Order.class);
                    Log.e(TAG, "onChildAdded: New order: "+dataSnapshot.getKey() );
                    order.setFb_key(dataSnapshot.getKey());
                    mAdapter.addItem(order);
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    Order order = dataSnapshot.getValue(Order.class);
                    Log.e(TAG, "onChildChanged: Changed order: "+dataSnapshot.getKey() );
                    order.setFb_key(dataSnapshot.getKey());
                    mAdapter.addItem(order);
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                    Log.e(TAG, "OrderList onChildRemoved: uid: "+dataSnapshot.getKey() );
                    Order order = dataSnapshot.getValue(Order.class);
                    mAdapter.removeItem(order);
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };
            mDatabaseReference.addChildEventListener(mListener);
        }

    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        List<Order> orders = new ArrayList<>();
        mAdapter = new OrderListAdapter(this, orders);
        recyclerView.setAdapter(mAdapter);
    }

}
