package il.co.gabel.android.uhcarmel.ui;

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

import java.util.Map;

import il.co.gabel.android.uhcarmel.R;
import il.co.gabel.android.uhcarmel.Utils;
import il.co.gabel.android.uhcarmel.firebase.objects.warehouse.Order;
import il.co.gabel.android.uhcarmel.ui.adapters.OrderListAdapter;
import il.co.gabel.android.uhcarmel.ui.fragment.ConfirmOrderAcceptedDialogFragment;
import il.co.gabel.android.uhcarmel.ui.fragment.ConfirmOrderReadyDialogFragment;


/**
 * An activity representing a single Order detail screen. This
 * activity is only used on narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link OrderListActivity}.
 */
public class OrderDetailActivity extends AppCompatActivity implements ConfirmOrderAcceptedDialogFragment.NoticeDialogListener{
    public static final String ARG_ITEM_ID = "item_id";
    private Order order;
    private static final String TAG=OrderDetailActivity.class.getCanonicalName();
    private ConfirmOrderAcceptedDialogFragment confirmOrderAcceptedDialogFragment;
    private ConfirmOrderReadyDialogFragment confirmOrderReadyDialogFragment;
    private Boolean mIsOwnOrders=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        Toolbar toolbar = findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.order_detail_fab);
        String item_id= getIntent().getStringExtra(OrderDetailActivity.ARG_ITEM_ID);
        if(item_id!=null) {
            Log.e(TAG, "onCreate: item_id: "+item_id );
            order = OrderListAdapter.getOrder(item_id);
        }
        if(order!=null){
            TextView orderDetail = findViewById(R.id.order_detail);
            StringBuilder builder = new StringBuilder();
            builder.append(getString(R.string.order_detail_from_user)).append(": ").append(order.getMirs()).append("\r\n");
            Map<String, Integer> items = order.getItems();
            for (String itemName : items.keySet()) {
                builder.append(itemName).append(": ").append(items.get(itemName)).append("\r\n");
            }
            builder.append("\r\n").append(getString(R.string.order_detail_from_branch)).append(": ").append(order.getBranch());
            orderDetail.setText(builder.toString());
        } else {
            onBackPressed();
        }

        if(order.getStatus().equals(Order.ORDER_STATUS_COMPLETED)){
            fab.setVisibility(View.GONE);
        }
        if(order.getStatus().equals(Order.ORDER_STATUS_READY)){
            fab.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_menu_delete));
        }
        if(order.getStatus().equals(Order.ORDER_STATUS_NEW)){
            fab.setImageDrawable(getResources().getDrawable(android.R.drawable.presence_online));
        }


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(order!=null){
                    if(order.getStatus().equals(Order.ORDER_STATUS_READY)) {
                        confirmOrderAcceptedDialogFragment = new ConfirmOrderAcceptedDialogFragment();
                        FragmentManager fragmentManager = getFragmentManager();
                        confirmOrderAcceptedDialogFragment.show(fragmentManager, "DeleteOrderDialogFragment");
                    } else {
                        confirmOrderReadyDialogFragment = new ConfirmOrderReadyDialogFragment();
                        FragmentManager fragmentManager = getFragmentManager();
                        confirmOrderReadyDialogFragment.show(fragmentManager, "ReadyOrderDialogFragment");
                    }
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
        DatabaseReference reference = Utils.getFBDBReference(getApplicationContext()).child("orders");
        if(order.getStatus().equals(Order.ORDER_STATUS_READY)){
            order.setStatus(Order.ORDER_STATUS_COMPLETED);
        } else {
            order.setStatus(Order.ORDER_STATUS_READY);
        }
        reference.child(order.getFb_key()).setValue(order);
        if(order.getStatus().equals(Order.ORDER_STATUS_READY)) {
            String prefix = getString(R.string.new_order_user_topic_prefix);
            String mirs = String.valueOf(order.getMirs());
            String msg = getString(R.string.new_order_deleted_notification_body);
            Utils.sendNotification(prefix + mirs, msg);
            confirmOrderReadyDialogFragment.dismiss();
        } else {
            confirmOrderAcceptedDialogFragment.dismiss();
        }

        onBackPressed();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        if(order.getStatus().equals(Order.ORDER_STATUS_READY)) {
            confirmOrderAcceptedDialogFragment.dismiss();
        } else {
            confirmOrderReadyDialogFragment.dismiss();
        }
    }
}