package il.co.gabel.android.uhcarmel.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

import il.co.gabel.android.uhcarmel.R;
import il.co.gabel.android.uhcarmel.Utils;
import il.co.gabel.android.uhcarmel.security.BasicAuthenticationListener;
import il.co.gabel.android.uhcarmel.security.UHFireBaseManager;
import il.co.gabel.android.uhcarmel.ui.adapters.OrdersAdapter;
import il.co.gabel.android.uhcarmel.warehouse.Order;
import il.co.gabel.android.uhcarmel.warehouse.OrderListAdapter;

public class OrdersActivity extends AppCompatActivity {
    private static final String TAG = OrdersActivity.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private OrdersAdapter mAdapter;
    private UHFireBaseManager manager;
    private DatabaseReference mDatabaseReference;
    private ChildEventListener mListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        manager = new UHFireBaseManager(OrdersActivity.this,new BasicAuthenticationListener(OrdersActivity.this));

        mRecyclerView = findViewById(R.id.order_list);
        setupRecyclerView(mRecyclerView);
        attachListener();


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        List<Order> orders = new ArrayList<>();
        mAdapter = new OrdersAdapter(this, orders);
        recyclerView.setAdapter(mAdapter);
    }

    private void attachListener(){
        mDatabaseReference = Utils.getFBDBReference(getApplicationContext()).child("warehouse").child("orders");
        if(mListener ==null){
            mListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    Order order = dataSnapshot.getValue(Order.class);
                    order.setFb_key(dataSnapshot.getKey());
                    mAdapter.addItem(order);
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

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

}
