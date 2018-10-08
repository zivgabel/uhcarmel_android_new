package il.co.gabel.android.uhcarmel.ui.holders;

import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import il.co.gabel.android.uhcarmel.R;

public class RedAlertListHolder extends BasicHolder {

    public TextView displayNameTextView;
    public TextView dateTextView;
    public TextView picsNumberTextView;
    public CheckBox locationCheckbox;


    public RedAlertListHolder(View itemView) {
        super(itemView);
        displayNameTextView = itemView.findViewById(R.id.ral_display_name_text_view);
        dateTextView = itemView.findViewById(R.id.ral_date_text_view);
        picsNumberTextView = itemView.findViewById(R.id.ral_pics_number_text_view);
        locationCheckbox = itemView.findViewById(R.id.ral_location_checkbox);
    }
}
