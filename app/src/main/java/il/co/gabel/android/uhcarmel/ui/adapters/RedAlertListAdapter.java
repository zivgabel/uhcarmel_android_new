package il.co.gabel.android.uhcarmel.ui.adapters;

import android.support.annotation.NonNull;
import android.view.View;

import java.util.Comparator;
import java.util.List;

import il.co.gabel.android.uhcarmel.R;
import il.co.gabel.android.uhcarmel.firebase.objects.redalert.CompleteRedAlertMessage;
import il.co.gabel.android.uhcarmel.ui.holders.RedAlertListHolder;

public class RedAlertListAdapter extends BasicAdapter<CompleteRedAlertMessage,RedAlertListHolder> {


    public RedAlertListAdapter(List<CompleteRedAlertMessage> items, Class<RedAlertListHolder> holderType, Class<CompleteRedAlertMessage> itemType) {
        super(items, holderType, itemType);
    }

    @Override
    protected Comparator<CompleteRedAlertMessage> getComparator() {
        return new CompleteRedAlertMessageComparator();
    }

    @Override
    int getItemLayoutId() {
        return R.layout.red_alert_list_item;
    }

    @Override
    public void onBindViewHolder(@NonNull RedAlertListHolder holder, int position) {
        CompleteRedAlertMessage message = items.get(position);
        holder.displayNameTextView.setText(message.getMessage().getReporterDisplayName());
        holder.dateTextView.setText(message.getMessage().getFormatedDate());
        holder.locationCheckbox.setChecked(message.hasLocation());
        holder.picsNumberTextView.setText(String.valueOf(message.getNumberOfPictures()));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private class CompleteRedAlertMessageComparator implements Comparator<CompleteRedAlertMessage> {

        @Override
        public int compare(CompleteRedAlertMessage o1, CompleteRedAlertMessage o2) {
            return o1.getMessage().getDate().compareTo(o2.getMessage().getDate());
        }
    }
}
