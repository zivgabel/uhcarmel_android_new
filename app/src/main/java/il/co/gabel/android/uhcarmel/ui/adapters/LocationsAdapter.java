package il.co.gabel.android.uhcarmel.ui.adapters;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import il.co.gabel.android.uhcarmel.R;
import il.co.gabel.android.uhcarmel.firebase.objects.locations.Location;
import il.co.gabel.android.uhcarmel.ui.holders.LocationHolder;

public class LocationsAdapter extends RecyclerView.Adapter<LocationHolder>  implements Filterable{
    private List<Location> locations;
    private List<Location> locationsFiltered;
    private static final String TAG = LocationsAdapter.class.getSimpleName();


    public LocationsAdapter(List<Location> locations){
        this.locations = locations;
        this.locationsFiltered=locations;
    }

    public void addLocation(Location location){
        locations.add(location);
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public LocationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.location_row, parent, false);
        return new LocationHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LocationHolder holder, int position) {
        final Location location = locationsFiltered.get(position);
        Log.e(TAG, "onBindViewHolder: "+holder.toString() );

        holder.getLocation_name_text_view().setText(location.getName());

        holder.getLocation_whatsapp_image_button().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = location.getName()+"\r\n"+location.getWazeUrl();
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT,message);
                intent.setType("text/plain");
                if(intent.resolveActivity(v.getContext().getPackageManager())!=null){
                    v.getContext().startActivity(intent);
                }
            }
        });

        holder.getLocation_maps_image_button().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(location.getWazeUrl()));
                intent.setPackage("com.waze");
                if(intent.resolveActivity(v.getContext().getPackageManager())!=null){
                    v.getContext().startActivity(intent);
                }else {
                    Toast.makeText(v.getContext(),R.string.no_waze_installed,Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return locationsFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String query = constraint.toString();
                List<Location> filtered = new ArrayList<>();
                if (query.isEmpty()) {
                    filtered = locations;
                } else {
                    for (Location location : locations) {
                        if (location.getName().toLowerCase().contains(query.toLowerCase())) {
                            filtered.add(location);
                        }
                    }
                }

                FilterResults results = new FilterResults();
                results.count = filtered.size();
                results.values = filtered;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                locationsFiltered = (ArrayList<Location>) results.values;
                notifyDataSetChanged();
            }
        };
    }
}

