package il.co.gabel.android.uhcarmel.firebase.objects.redalert;

import android.location.Location;
import android.net.Uri;

import java.util.Date;
import java.util.List;

public class RedAlertMessage {
    private String firstName;
    private String lastName;
    private String content;
    private List<Uri> images;
    private Date date;
    private Location location;

    public RedAlertMessage(){}

    public RedAlertMessage(String firstName, String lastName, String content,List<Uri> images, Location location) {
        this.firstName=firstName;
        this.lastName=lastName;
        this.content=content;
        this.date=new Date();
        this.location=location;
        this.images=images;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<Uri> getImages() {
        return images;
    }

    public void setImages(List<Uri> images) {
        this.images = images;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
