package il.co.gabel.android.uhcarmel.firebase.objects.redalert;

import android.location.Location;
import android.net.Uri;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RedAlertMessage {
    private String firstName;
    private String lastName;
    private String content;
    private Date date;

    public RedAlertMessage(){}

    public RedAlertMessage(String firstName, String lastName, String content,List<Uri> images, Location location) {
        this.firstName=firstName;
        this.lastName=lastName;
        this.content=content;
        this.date=new Date();
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getReporterDisplayName(){
        return firstName+" "+lastName;
    }
    public String getFormatedDate(){
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        return format.format(date);
    }
}
