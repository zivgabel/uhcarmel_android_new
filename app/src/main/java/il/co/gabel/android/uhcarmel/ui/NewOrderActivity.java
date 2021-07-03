package il.co.gabel.android.uhcarmel.ui;

import android.app.DialogFragment;
import android.app.SearchManager;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
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

import il.co.gabel.android.uhcarmel.R;
import il.co.gabel.android.uhcarmel.Utils;
import il.co.gabel.android.uhcarmel.firebase.objects.warehouse.Item;
import il.co.gabel.android.uhcarmel.firebase.objects.warehouse.Order;
import il.co.gabel.android.uhcarmel.security.BasicAuthenticationListener;
import il.co.gabel.android.uhcarmel.security.UHFireBaseManager;
import il.co.gabel.android.uhcarmel.ui.adapters.ItemAdapter;
import il.co.gabel.android.uhcarmel.ui.fragment.ConfirmOrderSendDialogFragment;

public class NewOrderActivity extends AppCompatActivity implements ConfirmOrderSendDialogFragment.NoticeDialogListener{

    private static final String TAG = NewOrderActivity.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private final ItemAdapter mAdapter = new ItemAdapter(new ArrayList<Item>());
    private ChildEventListener mListener;
    private SearchView mSearchView;
    private ConfirmOrderSendDialogFragment mConfirmOrderSendDialogFragment;
    private UHFireBaseManager fireBaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_order);
        fireBaseManager = new UHFireBaseManager(NewOrderActivity.this,new BasicAuthenticationListener(NewOrderActivity.this));
        fireBaseManager.getUserDetails();
        mRecyclerView = findViewById(R.id.new_order_items_recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL,false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);
        attachListeners();
        FloatingActionButton fab = findViewById(R.id.new_order_fab);
        Toolbar toolbar = findViewById(R.id.new_order_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mAdapter.getOrdered_items().isEmpty()){
                    Toast.makeText(v.getContext(),getString(R.string.new_order_no_items),Toast.LENGTH_LONG).show();
                    return;
                }
                mConfirmOrderSendDialogFragment =new ConfirmOrderSendDialogFragment();
                mConfirmOrderSendDialogFragment.show(getFragmentManager(),"new_order_breanch");
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
        if(mListener ==null) {
            mListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    Item item = dataSnapshot.getValue(Item.class);
                    mAdapter.addItem(item);
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

            fireBaseManager.addItemsEventListener(mListener);
        }

    }

    private void detachListeners(){
        if(mListener !=null){
            fireBaseManager.removeItemsEventListener(mListener);
        }
        mListener =null;
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
                mAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mAdapter.getFilter().filter(newText);
                return false;
            }
        });
        return true;
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        int selectedBranch= ((ConfirmOrderSendDialogFragment) dialog).getSelectedBranch();
        TypedArray branches;
        branches = getResources().obtainTypedArray(R.array.new_order_branches);
        String branch = branches.getString(selectedBranch);
        int user_mirs=UHFireBaseManager.getUser().getMirs();
        Date date = new Date();
        Order order = new Order(mAdapter.getOrdered_items(),user_mirs,date,branch);
        order.setUserFirebaseId(Utils.getUserUID(NewOrderActivity.this));
        order.setStatus(Order.ORDER_STATUS_NEW);
        DatabaseReference reference = Utils.getFBDBReference(getApplicationContext()).child("orders");
        DatabaseReference tmp = reference.push();
        order.setFb_key(tmp.getKey());
        tmp.setValue(order);
        mAdapter.getOrdered_items().clear();
        Utils.sendNotification(order.getBranchAlias()+getApplicationContext().getString(R.string.new_order_topic),getApplicationContext().getString(R.string.new_order_notification_body_start)+" "+user_mirs);
        dialog.dismiss();
        Toast.makeText(getApplicationContext(),getString(R.string.order_success),Toast.LENGTH_LONG).show();
        branches.recycle();
        onBackPressed();
    }


    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        dialog.dismiss();
    }
}
