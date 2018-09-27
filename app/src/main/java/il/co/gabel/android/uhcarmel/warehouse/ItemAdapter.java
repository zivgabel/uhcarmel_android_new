package il.co.gabel.android.uhcarmel.warehouse;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.NumberPicker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import il.co.gabel.android.uhcarmel.R;

public class ItemAdapter extends RecyclerView.Adapter<ItemHolder> implements Filterable {
    private List<Item> items;
    private List<Item> itemsFiltered;
    private Map<String,Integer> ordered_items;
    private static final String TAG = ItemAdapter.class.getSimpleName();

    public ItemAdapter(List<Item> items){
        this.items=items;
        this.itemsFiltered=items;
        ordered_items=new HashMap<>();
    }

    public void addItem(Item item){
        items.add(item);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_order_row, parent, false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHolder holder, int position) {
        final Item item = itemsFiltered.get(position);
        holder.getNew_order_item_name_text_view().setText(item.getName());
        holder.getNew_order_amout_number_picker().setMaxValue(item.getMax());
        holder.getNew_order_amout_number_picker().setMinValue(0);
        Integer current_value = ordered_items.get(item.getName());
        if(current_value!=null){
            holder.getNew_order_amout_number_picker().setValue(ordered_items.get(item.getName()));
        } else {
            holder.getNew_order_amout_number_picker().setValue(0);
        }

        holder.getNew_order_amout_number_picker().setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                Log.e(TAG, "onValueChange: item: "+item.getName()+" old: "+oldVal+" new: "+newVal );
                if(newVal>0){
                    ordered_items.put(item.getName(),newVal);
                } else {
                    if(ordered_items.get(item.getName())!=null)
                    ordered_items.remove(item.getName());
                }
            }
        });
    }



    public Map<String, Integer> getOrdered_items() {
        return ordered_items;
    }

    @Override
    public int getItemCount() {
        return itemsFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String query = constraint.toString();
                List<Item> filtered = new ArrayList<>();
                if (query.isEmpty()) {
                    filtered = items;
                } else {
                    for (Item location : items) {
                        if (location.getName().toLowerCase().contains(query.toLowerCase())) {
                            filtered.add(location);
                        }
                    }
                }

                FilterResults results = new FilterResults();
                results.count = filtered.size();
                results.values = filtered;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                itemsFiltered = (ArrayList<Item>) results.values;
                notifyDataSetChanged();
            }
        };
    }
}
