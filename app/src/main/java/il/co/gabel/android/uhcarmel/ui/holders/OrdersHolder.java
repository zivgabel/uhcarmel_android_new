package il.co.gabel.android.uhcarmel.ui.holders;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import il.co.gabel.android.uhcarmel.R;

public class OrdersHolder extends RecyclerView.ViewHolder {
    public TextView orders_date_text_view;
    public TextView orders_branch_text_view;
    public CardView orders_card_view;

    public OrdersHolder(View v){
        super(v);
        orders_date_text_view = v.findViewById(R.id.orders_date_text_view);
        orders_branch_text_view = v.findViewById(R.id.orders_branch_text_view);
        orders_card_view = v.findViewById(R.id.orders_cardview);
    }

}

