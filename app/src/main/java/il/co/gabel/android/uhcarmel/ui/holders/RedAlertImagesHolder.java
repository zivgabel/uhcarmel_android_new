package il.co.gabel.android.uhcarmel.ui.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import il.co.gabel.android.uhcarmel.R;

public class RedAlertImagesHolder extends RecyclerView.ViewHolder {

    public ImageView imageView;
    public ImageButton imageButton;


    public RedAlertImagesHolder(View itemView){
        super(itemView);
        imageView = itemView.findViewById(R.id.ra_image_view);
        imageButton = itemView.findViewById(R.id.ra_image_delete);
    }

}
