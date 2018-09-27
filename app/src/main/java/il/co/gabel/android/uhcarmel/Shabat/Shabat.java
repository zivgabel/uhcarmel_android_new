package il.co.gabel.android.uhcarmel.Shabat;

public class Shabat {
    private String uuid;
    private Boolean status;
    private String address;
    private String city;
    private String commnet;
    private String displayName;
    private int mirs;

    public Shabat(){}

    public Shabat(Boolean status, String address,String city, String commnet, String displayName, int mirs, String uuid){
        this.status=status;
        this.address=address;
        this.city=city;
        this.commnet=commnet;
        this.displayName=displayName;
        this.mirs=mirs;
        this.uuid=uuid;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCommnet() {
        return commnet;
    }

    public void setCommnet(String commnet) {
        this.commnet = commnet;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public int getMirs() {
        return mirs;
    }

    public void setMirs(int mirs) {
        this.mirs = mirs;
    }
}
