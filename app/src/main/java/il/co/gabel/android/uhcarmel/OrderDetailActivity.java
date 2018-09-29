package il.co.gabel.android.uhcarmel;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import il.co.gabel.android.uhcarmel.warehouse.ConfirmOrderDeleteDialogFragmnet;
import il.co.gabel.android.uhcarmel.warehouse.Item;
import il.co.gabel.android.uhcarmel.warehouse.Order;
import il.co.gabel.android.uhcarmel.warehouse.OrderListAdapter;


/**
 * An activity representing a single Order detail screen. This
 * activity is only used on narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link OrderListActivity}.
 */
public class OrderDetailActivity extends AppCompatActivity implements ConfirmOrderDeleteDialogFragmnet.NoticeDialogListener{
    public static final String ARG_ITEM_ID = "item_id";
    private Order order;
    private static final String TAG=OrderDetailActivity.class.getCanonicalName();
    private ConfirmOrderDeleteDialogFragmnet confirmOrderDeleteDialogFragmnet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        Toolbar toolbar = findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        String item_id= getIntent().getStringExtra(OrderDetailActivity.ARG_ITEM_ID);
        if(item_id!=null) {
            Log.e(TAG, "onCreate: item_id: "+item_id );
            order=OrderListAdapter.getOrder(item_id);
        }
        if(order!=null){
            TextView orderDetail = findViewById(R.id.order_detail);
            StringBuilder builder = new StringBuilder();
            builder.append(getString(R.string.order_detail_from_user)).append(": ").append(order.getMirs()).append("\r\n");
            Map<String, Integer> items = order.getItems();
            for (Iterator<String> it = items.keySet().iterator(); it.hasNext(); ) {
                String itemName = it.next();
                builder.append(itemName).append(": ").append(items.get(itemName)).append("\r\n");
            }
            builder.append("\r\n").append(getString(R.string.order_detail_from_branch)).append(": ").append(order.getBranch());
            orderDetail.setText(builder.toString());
        } else {
            onBackPressed();
        }


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(order!=null){
                    confirmOrderDeleteDialogFragmnet = new ConfirmOrderDeleteDialogFragmnet();
                    FragmentManager fragmentManager = getFragmentManager();
                    confirmOrderDeleteDialogFragmnet.show(fragmentManager,"DeleteOrderDialogFragment");
                }
            }
        });

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            navigateUpTo(new Intent(this, OrderListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        DatabaseReference reference = Utils.getFBDBReference(getApplicationContext()).child("warehouse").child("orders");
        DatabaseReference completed_reference = Utils.getFBDBReference(getApplicationContext()).child("warehouse").child("completed_orders");
        reference.child(order.getFb_key()).removeValue();
        completed_reference.push().setValue(order);
        String prefix=getString(R.string.new_order_user_topic_prefix);
        String mirs = String.valueOf(order.getMirs());
        String msg = getString(R.string.new_order__deleted_notif_body);
        Utils.sendNotification(prefix+mirs,msg);
        confirmOrderDeleteDialogFragmnet.dismiss();
        onBackPressed();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        confirmOrderDeleteDialogFragmnet.dismiss();
    }
}