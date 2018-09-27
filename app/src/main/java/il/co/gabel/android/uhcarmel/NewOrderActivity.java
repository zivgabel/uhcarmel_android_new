package il.co.gabel.android.uhcarmel;

import android.app.DialogFragment;
import android.app.SearchManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import il.co.gabel.android.uhcarmel.warehouse.ConfirmOrderSendDialogFragment;
import il.co.gabel.android.uhcarmel.warehouse.Item;
import il.co.gabel.android.uhcarmel.warehouse.ItemAdapter;
import il.co.gabel.android.uhcarmel.warehouse.Order;

public class NewOrderActivity extends AppCompatActivity implements ConfirmOrderSendDialogFragment.NoticeDialogListener {

    private RecyclerView recyclerView;
    private static final String TAG = NewOrderActivity.class.getSimpleName();
    private final ItemAdapter adapter = new ItemAdapter(new ArrayList<Item>());
    private DatabaseReference databaseReference;
    private ChildEventListener listener;
    private SearchView mSearchView;
    private ConfirmOrderSendDialogFragment confirmOrderSendDialogFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_order);

        recyclerView = findViewById(R.id.new_order_items_recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        DividerItemDecoration decoration = new DividerItemDecoration(recyclerView.getContext(),LinearLayoutManager.VERTICAL);
        recyclerView.addItemDecoration(decoration);
        databaseReference = Utils.getFBDBReference(getApplicationContext()).child("warehouse").child("items");
        attachListeners();
        FloatingActionButton fab = findViewById(R.id.new_order_fab);
        Toolbar toolbar = findViewById(R.id.new_order_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(adapter.getOrdered_items().isEmpty()){
                    Toast.makeText(v.getContext(),getString(R.string.new_order_no_items),Toast.LENGTH_LONG).show();
                    return;
                }
                confirmOrderSendDialogFragment=new ConfirmOrderSendDialogFragment();
                confirmOrderSendDialogFragment.show(getFragmentManager(),"new_order_breanch");
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        detachListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        attachListeners();
    }

    private void attachListeners(){
        if(listener==null) {
            listener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    Item item = dataSnapshot.getValue(Item.class);
                    adapter.addItem(item);
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
            databaseReference.addChildEventListener(listener);
        }

    }

    private void detachListeners(){
        if(listener!=null){
            databaseReference.removeEventListener(listener);
        }
        listener=null;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.locations_menu, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mSearchView = (SearchView) menu.findItem(R.id.locations_action_search).getActionView();
        if (searchManager != null) {
            mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        }
        mSearchView.setMaxWidth(Integer.MAX_VALUE);

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
        return true;
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {

        int selectedBranch= ((ConfirmOrderSendDialogFragment) dialog).getSelectedBranch();
        TypedArray branches=getResources().obtainTypedArray(R.array.new_order_branches);
        String branch = branches.getString(selectedBranch);
        SharedPreferences sp = Utils.getSharedPreferences(getApplicationContext());
        int user_mirs=sp.getInt(getString(R.string.user_mirs),0);
        Date date = new Date();
        Order order = new Order(adapter.getOrdered_items(),user_mirs,date,branch);
        DatabaseReference reference = Utils.getFBDBReference(getApplicationContext()).child("warehouse").child("orders");
        reference.push().setValue(order);
        adapter.getOrdered_items().clear();

        Utils.sendNotification(getApplicationContext().getString(R.string.new_order_topic),getApplicationContext().getString(R.string.new_order_notif_body_start)+" "+user_mirs);
        dialog.dismiss();
        Toast.makeText(getApplicationContext(),getString(R.string.order_success),Toast.LENGTH_LONG).show();
        onBackPressed();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        dialog.dismiss();
    }
}
