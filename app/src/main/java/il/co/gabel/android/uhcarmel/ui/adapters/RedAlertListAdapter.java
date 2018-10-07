package il.co.gabel.android.uhcarmel.ui.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import il.co.gabel.android.uhcarmel.R;
import il.co.gabel.android.uhcarmel.firebase.objects.CompleteRedAlertMessage;
import il.co.gabel.android.uhcarmel.ui.holders.RedAlertListHolder;

public class RedAlertListAdapter extends RecyclerView.Adapter<RedAlertListHolder> {

    private List<CompleteRedAlertMessage> messages;

    public RedAlertListAdapter(List<CompleteRedAlertMessage> messages){
        messages=messages;
    }

    public void addMessage(CompleteRedAlertMessage message){
        messages.add(message);
        Collections.sort(messages, new CompleteRedAlertMessageComparator());
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public RedAlertListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shabat_list, parent, false);
        return new RedAlertListHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RedAlertListHolder holder, int position) {
        CompleteRedAlertMessage message = messages.get(position);
        holder.displayNameTextView.setText(message.getMessage().getReporterDisplayName());
        holder.dateTextView.setText(message.getMessage().getFormatedDate());
        holder.locationCheckbox.setChecked(message.hasLocation());
        holder.picsNumberTextView.setText(String.valueOf(message.getNumberOfPictures()));
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }


    private class CompleteRedAlertMessageComparator implements Comparator<CompleteRedAlertMessage> {

        @Override
        public int compare(CompleteRedAlertMessage o1, CompleteRedAlertMessage o2) {
            return o1.getMessage().getDate().compareTo(o2.getMessage().getDate());
        }
    }
}
