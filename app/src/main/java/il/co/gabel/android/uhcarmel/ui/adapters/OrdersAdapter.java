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

import il.co.gabel.android.uhcarmel.OrderDetailActivity;
import il.co.gabel.android.uhcarmel.R;
import il.co.gabel.android.uhcarmel.ui.OrderListActivity;
import il.co.gabel.android.uhcarmel.ui.OrdersActivity;
import il.co.gabel.android.uhcarmel.warehouse.Order;
import il.co.gabel.android.uhcarmel.warehouse.OrderListHolder;

public class OrdersAdapter extends RecyclerView.Adapter<OrderListHolder>{
    private static final String TAG= OrdersAdapter.class.getSimpleName();
    private static List<Order> oreders = new ArrayList<>();

    private final OrdersActivity mParentActivity;

    public OrdersAdapter(OrdersActivity parent, List<Order> items) {
        oreders = items;
        mParentActivity = parent;
    }


    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            final Order item = (Order) view.getTag();
            Context context = view.getContext();
            Intent intent = new Intent(context, OrderDetailActivity.class);
            intent.putExtra(OrderDetailActivity.ARG_ITEM_ID, item.getOrder_date().toString());
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
            holder.order_list_mirs_text_view.setText("מירס");
            holder.order_list_branch_text_view.setText("מרכז ציוד");
            holder.order_list_date_text_view.setText("תאריך");
            return;
        }
        position--;
        holder.order_list_mirs_text_view.setText(String.valueOf(oreders.get(position).getMirs()));
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        holder.order_list_date_text_view.setText(format.format(oreders.get(position).getOrder_date()));
        holder.order_list_branch_text_view.setText(oreders.get(position).getBranch());
        holder.itemView.setTag(oreders.get(position));
        holder.itemView.setOnClickListener(mOnClickListener);
    }
    @Override
    public int getItemCount() {
        return oreders.size()+1;
    }

    public void addItem(Order order){
        boolean exists=false;
        Iterator<Order> i = oreders.iterator();
        while (i.hasNext()){
            Order o = i.next();
            if(o.equals(order)){
                exists=true;
            }
        }
        if(!exists){
            oreders.add(order);
        }
        Collections.sort(oreders,new OrderComparator());
        notifyDataSetChanged();

    }

    public static Order getOrder(String date_string){
        for (Order o : oreders) {
            String orderDate = o.getOrder_date().toString();
            if (orderDate.equals(date_string)) {
                return o;
            }
        }
        return null;
    }


    public void removeItem(Order order) {

        Iterator<Order> i = oreders.iterator();
        while (i.hasNext()){
            Order o = i.next();
            if(o.equals(order)){
                i.remove();
            }
        }
        Collections.sort(oreders,new OrderComparator());
        notifyDataSetChanged();
    }

    public void clear(){
        oreders.clear();
    }


    private class OrderComparator implements Comparator<Order> {

        @Override
        public int compare(Order o1, Order o2) {
            if(o1.getBranch().equals(o2.getBranch())){
                return o1.getOrder_date().compareTo(o2.getOrder_date());
            }
            return o1.getBranch().compareTo(o2.getBranch());
        }
    }

}
