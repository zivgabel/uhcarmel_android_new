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
import il.co.gabel.android.uhcarmel.ui.OrdersActivity;
import il.co.gabel.android.uhcarmel.ui.OwnOrderDetailActivity;
import il.co.gabel.android.uhcarmel.ui.holders.OrdersHolder;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersHolder>{
    private static final String TAG= OrdersAdapter.class.getSimpleName();
    private static List<Order> orders = new ArrayList<>();

    private final OrdersActivity mParentActivity;

    public OrdersAdapter(OrdersActivity parent, List<Order> items) {
        orders = items;
        mParentActivity = parent;
    }


    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            final Order item = (Order) view.getTag();
            Context context = view.getContext();
            Intent intent = new Intent(context, OwnOrderDetailActivity.class);
            intent.putExtra(OrderDetailActivity.ARG_ITEM_ID, item.getFb_key());
            context.startActivity(intent);
    }};

    @NonNull
    @Override
    public OrdersHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.orders_row, parent, false);
        return new OrdersHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final OrdersHolder holder, int position) {
        Log.e(TAG, "onBindViewHolder: position "+position );
        if(position==0){
            holder.orders_card_view.setBackgroundColor(00000);
            holder.orders_branch_text_view.setText("מרכז ציוד");
            holder.orders_date_text_view.setText("תאריך");
            return;
        }
        position--;
        Order order = orders.get(position);
        switch (order.getStatus()){
            case Order.ORDER_STATUS_NEW:
                holder.orders_card_view.setBackgroundColor(holder.itemView.getResources().getColor(R.color.warehouseNewColor));
                break;
            case Order.ORDER_STATUS_COMPLETED:
                holder.orders_card_view.setBackgroundColor(holder.itemView.getResources().getColor(R.color.warehouseCompletedColor));
                break;
            case Order.ORDER_STATUS_READY:
                holder.orders_card_view.setBackgroundColor(holder.itemView.getResources().getColor(R.color.warehouseReadyColor));
                break;
        }
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        holder.orders_date_text_view.setText(format.format(order.getOrder_date()));
        holder.orders_branch_text_view.setText(order.getBranch());
        holder.itemView.setTag(order);
        holder.itemView.setOnClickListener(mOnClickListener);
    }
    @Override
    public int getItemCount() {

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
        Collections.sort(orders,new OrderComparator());
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
                if(o1.getStatus().equals(Order.ORDER_STATUS_READY)){
                    return -1;
                }
                if(o2.getStatus().equals(Order.ORDER_STATUS_READY)){
                    return 1;
                }
               if(o1.getStatus().equals(Order.ORDER_STATUS_NEW)){
                   return -1;
               }
                if(o2.getStatus().equals(Order.ORDER_STATUS_NEW)){
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
