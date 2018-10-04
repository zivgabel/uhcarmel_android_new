package il.co.gabel.android.uhcarmel.ui;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;

import java.util.Map;

import il.co.gabel.android.uhcarmel.R;
import il.co.gabel.android.uhcarmel.Utils;
import il.co.gabel.android.uhcarmel.firebase.objects.warehouse.Order;
import il.co.gabel.android.uhcarmel.ui.adapters.OrdersAdapter;
import il.co.gabel.android.uhcarmel.ui.fragment.ConfirmOrderAcceptedDialogFragment;

public class OwnOrderDetailActivity extends AppCompatActivity implements ConfirmOrderAcceptedDialogFragment.NoticeDialogListener{
    private Order order;
    private static final String TAG=OrderDetailActivity.class.getCanonicalName();
    private ConfirmOrderAcceptedDialogFragment confirmOrderAcceptedDialogFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_own_order_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        String item_id= getIntent().getStringExtra(OrderDetailActivity.ARG_ITEM_ID);
        if(item_id!=null) {
            Log.e(TAG, "onCreate: item_id: "+item_id );
            order = OrdersAdapter.getOrder(item_id);
        }
        if(order!=null){
            TextView orderDetail = findViewById(R.id.order_detail);
            StringBuilder builder = new StringBuilder();
            Map<String, Integer> items = order.getItems();
            for (String itemName : items.keySet()) {
                builder.append(itemName).append(": ").append(items.get(itemName)).append("\r\n");
            }
            builder.append("\r\n").append(getString(R.string.order_detail_from_branch)).append(": ").append(order.getBranch());
            orderDetail.setText(builder.toString());
        } else {
            onBackPressed();
        }



        FloatingActionButton fab = findViewById(R.id.own_order_fab);
        if(order.getStatus().equals(Order.ORDER_STATUS_NEW) || order.getStatus().equals(Order.ORDER_STATUS_COMPLETED)){
            fab.setVisibility(View.GONE);
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            if(order!=null){
                confirmOrderAcceptedDialogFragment = new ConfirmOrderAcceptedDialogFragment();
                FragmentManager fragmentManager = getFragmentManager();
                confirmOrderAcceptedDialogFragment.show(fragmentManager,"DeleteOrderDialogFragment");
            }
            }
        });


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        DatabaseReference reference = Utils.getFBDBReference(getApplicationContext()).child("orders");
        order.setStatus(Order.ORDER_STATUS_COMPLETED);
        reference.child(order.getFb_key()).setValue(order);
        confirmOrderAcceptedDialogFragment.dismiss();
        onBackPressed();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        confirmOrderAcceptedDialogFragment.dismiss();
    }
}
