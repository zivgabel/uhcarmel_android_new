package il.co.gabel.android.uhcarmel.firebase.objects.redalert;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import il.co.gabel.android.uhcarmel.firebase.objects.locations.Location;

public class CompleteRedAlertMessage {
    private static final String TAG = CompleteRedAlertMessage.class.getSimpleName();
    private RedAlertMessage message;
    private Location location;
    private HashMap<String,ImageInfo> images;


    public CompleteRedAlertMessage() {}


    public RedAlertMessage getMessage() {
        return message;
    }

    public void setMessage(RedAlertMessage message) {
        this.message = message;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public HashMap<String, ImageInfo> getImages() {
        return images;
    }

    public void setImages(HashMap<String, ImageInfo> images) {
        this.images = images;
    }

    public int getNumberOfPictures(){
        if(images==null){
            return 0;
        }
        return images.size();
    }
    public Boolean hasLocation(){
        Log.e(TAG, "hasLocation: "+(location!=null) );
        try {
            Double alt = location.getAltitude();
            Log.e(TAG, "hasLocation: Altitude: "+alt );
            return true;
        } catch (NullPointerException e) {
            return false;
        }
    }

    @Override
    public String toString() {
        JSONObject object = new JSONObject();
        try {
            object.put("first_name",message.getFirstName());
            object.put("last_name",message.getLastName());
            object.put("content",message.getContent());
            return object.toString();
        } catch (JSONException e) {
            return super.toString();
        }
    }
}
