package il.co.gabel.android.uhcarmel.ui;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;

import il.co.gabel.android.uhcarmel.R;
import il.co.gabel.android.uhcarmel.firebase.objects.CompleteRedAlertMessage;
import il.co.gabel.android.uhcarmel.ui.adapters.BasicAdapter;
import il.co.gabel.android.uhcarmel.ui.adapters.RedAlertListAdapter;
import il.co.gabel.android.uhcarmel.ui.holders.RedAlertListHolder;

public class RedAlertListActivity extends BasicListActivity<RedAlertListHolder,CompleteRedAlertMessage> {

    @Override
    String getDatabasePath() {
        return "redalert";
    }

    @Override
    int getRecyclerId() {
        return R.id.ral_recycler_view;
    }

    @Override
    BasicAdapter getAdapter() {
        return new RedAlertListAdapter(new ArrayList<CompleteRedAlertMessage>(),RedAlertListHolder.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_red_alert_list);
        Toolbar toolbar = findViewById(R.id.ral_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }




}
