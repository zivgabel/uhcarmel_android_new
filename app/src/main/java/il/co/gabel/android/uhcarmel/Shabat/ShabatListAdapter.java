package il.co.gabel.android.uhcarmel.Shabat;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import il.co.gabel.android.uhcarmel.R;

public class ShabatListAdapter extends RecyclerView.Adapter<ShabatListHolder>{
    private static final String TAG = ShabatListAdapter.class.getSimpleName();
    private final List<Shabat> shabat_list;

    public ShabatListAdapter(ArrayList<Shabat> shabat_list){
        this.shabat_list =shabat_list;
    }
    public void shabatItemChanged(Shabat shabat) {
        Log.e(TAG, "shabatItemChanged: uuid: "+shabat.getUuid()+" status: "+shabat.getStatus());
        for (Shabat s : shabat_list){
            if(s.getUuid().equals(shabat.getUuid())){
                shabat_list.remove(s);
                if(shabat.getStatus()){
                    shabat_list.add(shabat);
                }
                Collections.sort(shabat_list,new ShabatComparator());
                notifyDataSetChanged();
                return;
            }
        }

        if (shabat.getStatus()){
            shabat_list.add(shabat);
            Collections.sort(shabat_list,new ShabatComparator());
            notifyDataSetChanged();
        }

    }


    @NonNull
    @Override
    public ShabatListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shabat_list, parent, false);
        Log.e(TAG, "onCreateViewHolder: NEW holder created ");
        return new ShabatListHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShabatListHolder holder, int position) {

        final Shabat shabat = shabat_list.get(position);
        Log.e(TAG, "onBindViewHolder: "+shabat.getUuid()+" "+holder.toString() );
        holder.getAddress().setText(shabat.getAddress());
        holder.getCity().setText(shabat.getCity());
        holder.getComment().setText(shabat.getCommnet());
        holder.getName().setText(shabat.getDisplayName());
        holder.getMirs().setText(String.valueOf(shabat.getMirs()));
    }

    @Override
    public int getItemCount() {
        Log.e(TAG, "getItemCount: "+shabat_list.size() );
        return shabat_list.size();
    }

    private class ShabatComparator implements Comparator<Shabat> {

        @Override
        public int compare(Shabat o1, Shabat o2) {
            return o1.getCity().compareTo(o2.getCity());
        }
    }

}
