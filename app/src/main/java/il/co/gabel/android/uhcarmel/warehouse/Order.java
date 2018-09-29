package il.co.gabel.android.uhcarmel.warehouse;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Order {

    private Map<String,Integer> items=new HashMap<>();
    private int mirs;
    private Date order_date;
    private String branch;
    private Boolean ready = false;
    private String fb_key;

    public Order(){}


    public Order(Map<String,Integer> items,Integer mirs,Date order_date,String branch){
        this.items=items;
        this.mirs=mirs;
        this.order_date=order_date;
        this.branch=branch;
    }

    public Map<String, Integer> getItems() {
        return items;
    }

    public void setItems(Map<String, Integer> items) {
        this.items = items;
    }

    public int getMirs() {
        return mirs;
    }

    public void setMirs(int mirs) {
        this.mirs = mirs;
    }

    public Date getOrder_date() {
        return order_date;
    }

    public void setOrder_date(Date order_date) {
        this.order_date = order_date;
    }

    public String getFb_key() {
        return fb_key;
    }

    public void setFb_key(String fb_key) {
        this.fb_key = fb_key;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public Boolean getReady() {
        return ready;
    }

    public void setReady(Boolean ready) {
        this.ready = ready;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (String k : items.keySet()) {
            int v = items.get(k);
            builder.append(k).append(": ").append(v).append("\r\n");
        }
        builder.append("\r\n").append("הוזמן מסניף ").append(branch);
        return builder.toString();
    }

    @Override
    public boolean equals(Object obj) {
        Order o = (Order) obj;
        return this.getFb_key().equals(o.getFb_key());
    }

    public String getBranchAlias(){
        switch (getBranch()){
            case "חיפה":
                return "haifa";
            case "קרית אתא":
                return "kata";
            default:
                return "haifa";
        }
    }

}
