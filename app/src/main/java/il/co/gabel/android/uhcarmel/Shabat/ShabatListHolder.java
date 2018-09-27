package il.co.gabel.android.uhcarmel.Shabat;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import il.co.gabel.android.uhcarmel.R;

public class ShabatListHolder extends RecyclerView.ViewHolder{

    private TextView mShabaListNameTextView;
    private TextView mShabaListMirsTextView;
    private TextView mShabaListCityTextView;
    private TextView mShabaListCommentTextView;
    private TextView mShabaListAddressTextView;

    public ShabatListHolder(View itemView) {
        super(itemView);
        mShabaListNameTextView= itemView.findViewById(R.id.shabat_list_item_name);
        mShabaListMirsTextView= itemView.findViewById(R.id.shabat_list_item_mirs);
        mShabaListCityTextView= itemView.findViewById(R.id.shabat_list_item_city);
        mShabaListCommentTextView= itemView.findViewById(R.id.shabat_list_item_comment);
        mShabaListAddressTextView = itemView.findViewById(R.id.shabat_list_item_address);
    }

    public TextView getName() {
        return mShabaListNameTextView;
    }

    public void setName(TextView mShabaListNameTextView) {
        this.mShabaListNameTextView = mShabaListNameTextView;
    }

    public TextView getMirs() {
        return mShabaListMirsTextView;
    }

    public void setMirs(TextView mShabaListMirsTextView) {
        this.mShabaListMirsTextView = mShabaListMirsTextView;
    }

    public TextView getCity() {
        return mShabaListCityTextView;
    }

    public void setCity(TextView mShabaListCityTextView) {
        this.mShabaListCityTextView = mShabaListCityTextView;
    }

    public TextView getComment() {
        return mShabaListCommentTextView;
    }

    public void setComment(TextView mShabaListCommentTextView) {
        this.mShabaListCommentTextView = mShabaListCommentTextView;
    }

    public TextView getAddress() {
        return mShabaListAddressTextView;
    }

    public void Address(TextView mShabaListAddressTextView) {
        this.mShabaListAddressTextView = mShabaListAddressTextView;
    }
}
