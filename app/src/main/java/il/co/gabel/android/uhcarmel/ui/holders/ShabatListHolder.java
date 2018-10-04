package il.co.gabel.android.uhcarmel.ui.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import il.co.gabel.android.uhcarmel.R;

public class ShabatListHolder extends RecyclerView.ViewHolder{

    private TextView mShabatListNameTextView;
    private TextView mShabatListMirsTextView;
    private TextView mShabatListCityTextView;
    private TextView mShabatListCommentTextView;
    private TextView mShabatListAddressTextView;

    public ShabatListHolder(View itemView) {
        super(itemView);
        mShabatListNameTextView = itemView.findViewById(R.id.shabat_list_item_name);
        mShabatListMirsTextView = itemView.findViewById(R.id.shabat_list_item_mirs);
        mShabatListCityTextView = itemView.findViewById(R.id.shabat_list_item_city);
        mShabatListCommentTextView = itemView.findViewById(R.id.shabat_list_item_comment);
        mShabatListAddressTextView = itemView.findViewById(R.id.shabat_list_item_address);
    }

    public TextView getName() {
        return mShabatListNameTextView;
    }

    public void setName(TextView mShabatListNameTextView) {
        this.mShabatListNameTextView = mShabatListNameTextView;
    }

    public TextView getMirs() {
        return mShabatListMirsTextView;
    }

    public void setMirs(TextView mShabatListMirsTextView) {
        this.mShabatListMirsTextView = mShabatListMirsTextView;
    }

    public TextView getCity() {
        return mShabatListCityTextView;
    }

    public void setCity(TextView mShabatListCityTextView) {
        this.mShabatListCityTextView = mShabatListCityTextView;
    }

    public TextView getComment() {
        return mShabatListCommentTextView;
    }

    public void setComment(TextView mShabatListCommentTextView) {
        this.mShabatListCommentTextView = mShabatListCommentTextView;
    }

    public TextView getAddress() {
        return mShabatListAddressTextView;
    }

    public void Address(TextView mShabatListAddressTextView) {
        this.mShabatListAddressTextView = mShabatListAddressTextView;
    }
}
