package il.co.gabel.android.uhcarmel.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import il.co.gabel.android.uhcarmel.R;
import il.co.gabel.android.uhcarmel.ui.ui.redalertlist.RedAlertListFragment;

public class RedAlertListActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_red_alert_list);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.ral_container, RedAlertListFragment.newInstance())
                    .commitNow();
        }
    }
}
