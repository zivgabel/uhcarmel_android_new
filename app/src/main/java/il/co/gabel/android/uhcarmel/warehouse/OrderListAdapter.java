package il.co.gabel.android.uhcarmel.warehouse;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import il.co.gabel.android.uhcarmel.OrderDetailActivity;
import il.co.gabel.android.uhcarmel.OrderListActivity;
import il.co.gabel.android.uhcarmel.R;

public class OrderListAdapter extends RecyclerView.Adapter<OrderListHolder>{
    private final OrderListActivity mParentActivity;
    private static List<Order> orders = new ArrayList<>();
    private static final String TAG=OrderListAdapter.class.getSimpleName();

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
    public void onBindViewHolder(final OrderListHolder holder, int position) {
        holder.order_list_mirs_text_view.setText(String.valueOf(orders.get(position).getMirs()));
        holder.order_list_date_text_view.setText(orders.get(position).getOrder_date().toString());

        holder.itemView.setTag(orders.get(position));
        holder.itemView.setOnClickListener(mOnClickListener);
    }
    @Override
    public int getItemCount() {
        return orders.size();
    }

    public static void addItem(Order order){
        orders.add(order);
    }

    public static Order getOrder(String date_string){
        for (Order o : orders) {
            String odate = o.getOrder_date().toString();
            if (odate.equals(date_string)) {
                return o;
            }
        }
        return null;
    }


    public static void removeItem(Order order,OrderListAdapter adapter) {

        Iterator<Order> i = orders.iterator();
        while (i.hasNext()){
            Order o = i.next();
            if(o.equals(order)){
                i.remove();
                if(adapter!=null) {
                    adapter.notifyDataSetChanged();
                }
            }
        }
    }
}
