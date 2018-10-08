package il.co.gabel.android.uhcarmel.ui.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import il.co.gabel.android.uhcarmel.ui.holders.BasicHolder;

public abstract class BasicAdapter<I extends Object, H extends BasicHolder> extends RecyclerView.Adapter<H> {
    protected List<I> items;
    protected Class<H> holderType;
    protected Class<I> itemType;


    abstract int getItemLayoutId();



    public BasicAdapter(List<I> items,Class<H> holderType, Class<I> itemType) {
        this.items=items;
        this.holderType=holderType;
        this.itemType=itemType;
    }

    /**
     * Adds an Item to the list even if this item already exists
     * @param item
     */
    public void addItem(I item){

        items.add(item);
        sortAndNotify();
    }

    /**
     * Adds an Item to the list only if the item is not already in the list
     * @param item
     */
    public void addItemUnique(I item){
        if(!items.contains(item)){
            items.add(item);
            sortAndNotify();
        }
    }
    public void removeItem(I item){
        if(items.contains(item)){
            items.remove(item);
            sortAndNotify();

        }
    }

    protected void sortAndNotify(){
        Collections.sort(items,getComparator());
        notifyDataSetChanged();
    }


    protected  Comparator<I> getComparator(){
        return new ItemComperator();
    }


    @NonNull
    @Override
    public H onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(getItemLayoutId(), parent, false);
        try {

            return holderType.getConstructor(View.class).newInstance(view);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ItemComperator implements Comparator<I> {
        @Override
        public int compare(I o1, I o2) {
            return o1.toString().compareTo(o2.toString());
        }
    }

    public Class<I> getItemType() {
        return itemType;
    }
}
