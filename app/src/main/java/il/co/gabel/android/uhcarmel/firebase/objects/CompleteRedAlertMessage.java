package il.co.gabel.android.uhcarmel.firebase.objects;

import java.util.List;

import il.co.gabel.android.uhcarmel.firebase.objects.locations.Location;
import il.co.gabel.android.uhcarmel.firebase.objects.redalert.ImageInfo;
import il.co.gabel.android.uhcarmel.firebase.objects.redalert.RedAlertMessage;

public class CompleteRedAlertMessage {
    private RedAlertMessage message;
    private Location location;
    List<ImageInfo> images;


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

    public List<ImageInfo> getImages() {
        return images;
    }

    public void setImages(List<ImageInfo> images) {
        this.images = images;
    }
}
