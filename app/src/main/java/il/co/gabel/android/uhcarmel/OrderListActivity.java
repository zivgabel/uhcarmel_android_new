package il.co.gabel.android.uhcarmel;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
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

import il.co.gabel.android.uhcarmel.warehouse.Order;
import il.co.gabel.android.uhcarmel.warehouse.OrderListAdapter;

public class OrderListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private OrderListAdapter adapter;
    private DatabaseReference databaseReference;
    private ChildEventListener listener;
    private static final String TAG=OrderListActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());
        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        recyclerView = findViewById(R.id.order_list);
        setupRecyclerView(recyclerView);
        attachListener();

    }
    @Override
    protected void onResume() {
        super.onResume();
        attachListener();
    }

    private void attachListener(){
        databaseReference = Utils.getFBDBReference(getApplicationContext()).child("warehouse").child("orders");
        if(listener==null){
            listener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    Order order = dataSnapshot.getValue(Order.class);
                    order.setFb_key(dataSnapshot.getKey());
                    OrderListAdapter.addItem(order);
                    if(adapter!=null){
                        adapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                    Log.e(TAG, "OrderList onChildRemoved: uid: "+dataSnapshot.getKey() );
                    Order order = dataSnapshot.getValue(Order.class);
                    OrderListAdapter.removeItem(order,adapter);
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };
            databaseReference.addChildEventListener(listener);
        }

    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        List<Order> orders = new ArrayList<>();
        adapter = new OrderListAdapter(this, orders);
        recyclerView.setAdapter(adapter);
    }

}
