package il.co.gabel.android.uhcarmel.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import il.co.gabel.android.uhcarmel.R;
import il.co.gabel.android.uhcarmel.firebase.objects.warehouse.Order;
import il.co.gabel.android.uhcarmel.ui.OrderDetailActivity;
import il.co.gabel.android.uhcarmel.ui.OrderListActivity;
import il.co.gabel.android.uhcarmel.ui.holders.OrderListHolder;

public class OrderListAdapter extends RecyclerView.Adapter<OrderListHolder>{
    private static final String TAG=OrderListAdapter.class.getSimpleName();
    private static List<Order> orders = new ArrayList<>();

    private final OrderListActivity mParentActivity;

    public OrderListAdapter(OrderListActivity parent,List<Order> items) {
        orders = items;
        mParentActivity = parent;
    }


    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            final Order item = (Order) view.getTag();
            Context context = view.getContext();
            Intent intent = new Intent(context, OrderDetailActivity.class);
            intent.putExtra(OrderDetailActivity.ARG_ITEM_ID, item.getFb_key());
            context.startActivity(intent);
    }};

    @NonNull
    @Override
    public OrderListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_list_row, parent, false);
        return new OrderListHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final OrderListHolder holder, int position) {
        Log.e(TAG, "onBindViewHolder: position "+position );
        if(position==0){
            holder.order_list_cardview.setBackgroundColor(00000);
            holder.order_list_mirs_text_view.setText("מירס");
            holder.order_list_branch_text_view.setText("מרכז ציוד");
            holder.order_list_date_text_view.setText("תאריך");
            return;
        }
        position--;
        Order order = orders.get(position);
        Log.e(TAG, "onBindViewHolder: "+order.getFb_key() );
        switch (order.getStatus()){
            case Order.ORDER_STATUS_NEW:
                holder.order_list_cardview.setBackgroundColor(holder.itemView.getResources().getColor(R.color.warehouseNewColor));
                break;
            case Order.ORDER_STATUS_COMPLETED:
                holder.order_list_cardview.setBackgroundColor(holder.itemView.getResources().getColor(R.color.warehouseCompletedColor));
                break;
            case Order.ORDER_STATUS_READY:
                holder.order_list_cardview.setBackgroundColor(holder.itemView.getResources().getColor(R.color.warehouseReadyColor));
                break;
        }
        holder.order_list_mirs_text_view.setText(String.valueOf(order.getMirs()));
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        holder.order_list_date_text_view.setText(format.format(order.getOrder_date()));
        holder.order_list_branch_text_view.setText(order.getBranch());
        holder.itemView.setTag(order);
        holder.itemView.setOnClickListener(mOnClickListener);
    }
    @Override
    public int getItemCount() {
        Log.e(TAG, "getItemCount: "+orders.size() );
        return orders.size()+1;
    }

    public void addItem(Order order){
        Iterator<Order> i = orders.iterator();
        while (i.hasNext()){
            Order o = i.next();
            if(o.equals(order)){
                i.remove();
            }
        }
        orders.add(order);
        Collections.sort(orders,new OrderListAdapter.OrderComparator());
        Log.e(TAG, "addItem: "+order.getFb_key() );
        notifyDataSetChanged();
    }

    public static Order getOrder(String fb_key){
        for (Order o : orders) {
            if (o.getFb_key().equals(fb_key)) {
                return o;
            }
        }
        return null;
    }

    public static int getOrderPosition(String fb_key){
        int count=0;
        for (Order o : orders) {
            if (o.getFb_key().equals(fb_key)) {
                return count;
            }
            count++;
        }
        return -1;
    }


    public void removeItem(Order order) {

        Iterator<Order> i = orders.iterator();
        while (i.hasNext()){
            Order o = i.next();
            if(o.equals(order)){
                i.remove();
            }
        }
        Collections.sort(orders,new OrderComparator());
        notifyDataSetChanged();
    }

    public void clear(){
        orders.clear();
    }


    private class OrderComparator implements Comparator<Order> {

        @Override
        public int compare(Order o1, Order o2) {
            if(o1.getStatus().compareTo(o2.getStatus())!=0) {
                if(o1.getStatus().equals(Order.ORDER_STATUS_NEW)){
                    return -1;
                }
                if(o2.getStatus().equals(Order.ORDER_STATUS_NEW)){
                    return 1;
                }
                if(o1.getStatus().equals(Order.ORDER_STATUS_READY)){
                    return -1;
                }
                if(o2.getStatus().equals(Order.ORDER_STATUS_READY)){
                    return 1;
                }
                if(o1.getStatus().equals(Order.ORDER_STATUS_COMPLETED)){
                    return -1;
                }
                if(o2.getStatus().equals(Order.ORDER_STATUS_COMPLETED)){
                    return 1;
                }
            }
            if(o1.getBranch().equals(o2.getBranch())){
                return o1.getOrder_date().compareTo(o2.getOrder_date());
            }
            return o1.getBranch().compareTo(o2.getBranch());
        }

    }

}
