package il.co.gabel.android.uhcarmel.ui.adapters;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import il.co.gabel.android.uhcarmel.R;
import il.co.gabel.android.uhcarmel.ui.holders.RedAlertImagesHolder;

public class RedAlertImagesAdapter extends RecyclerView.Adapter<RedAlertImagesHolder>{

    private static final String TAG = RedAlertImagesAdapter.class.getSimpleName();
    private List<Uri> mImagesUriList;



    public RedAlertImagesAdapter(){
        mImagesUriList = new ArrayList<>();
    }

    public void clear(){
        mImagesUriList.clear();
        notifyDataSetChanged();
    }

    public void addImagesList(List<Uri> images){
        for (Iterator<Uri> it = images.iterator(); it.hasNext(); ) {
            Uri i = it.next();
            addImage(i);
        }
    }

    public void addImage(Uri image){
        for (Iterator<Uri> it = mImagesUriList.iterator(); it.hasNext(); ) {
            Uri i = it.next();
            if(i.equals(image)){
                return;
            }
        }
        mImagesUriList.add(image);
        notifyDataSetChanged();
    }

    public void removeImage(Uri image){
        Log.e(TAG, "removeImage: clicked: "+image.toString() );
        for (Iterator<Uri> it = mImagesUriList.iterator(); it.hasNext(); ) {
            Uri i = it.next();
            if(i.equals(image)) {
                Log.e(TAG, "removeImage: " + i.toString());
                it.remove();
                notifyDataSetChanged();
            }
        }
    }


    @NonNull
    @Override
    public RedAlertImagesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.red_alert_image_item, parent, false);
        return new RedAlertImagesHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RedAlertImagesHolder holder, int position) {
        final Uri imageUri = mImagesUriList.get(position);
        Log.e(TAG, "onBindViewHolder: "+imageUri.toString() );

        Glide.with(holder.itemView).load(imageUri).into(holder.imageView);
        holder.imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeImage(imageUri);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mImagesUriList.size();
    }

    public List<Uri> getmImagesUriList() {
        return mImagesUriList;
    }
}
