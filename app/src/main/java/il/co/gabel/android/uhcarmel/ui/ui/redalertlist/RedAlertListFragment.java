package il.co.gabel.android.uhcarmel.ui.ui.redalertlist;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import il.co.gabel.android.uhcarmel.R;
import il.co.gabel.android.uhcarmel.Utils;

public class RedAlertListFragment extends Fragment {

    private RedAlertListViewModel mViewModel;
    private RecyclerView recyclerView;

    public static RedAlertListFragment newInstance() {
        return new RedAlertListFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.red_alert_list_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(RedAlertListViewModel.class);
        View view = getView();
        recyclerView = view.findViewById(R.id.ral_recycler_view);
        mViewModel.setReference(Utils.getFBDBReference(getContext()).child("redalert"));
        recyclerView.setAdapter(mViewModel.getAdapter());
    }


}
