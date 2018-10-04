package il.co.gabel.android.uhcarmel.security;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import il.co.gabel.android.uhcarmel.R;
import il.co.gabel.android.uhcarmel.Utils;
import il.co.gabel.android.uhcarmel.firebase.objects.shabat.Shabat;

public class User {
    private int mirs;
    private Boolean admin;
    private Boolean shabat_admin;
    private Boolean wh_admin;
    private String wh_admin_branch;
    private String first_name;
    private String last_name;
    private String firstName;
    private String lastName;
    private Shabat shabat;
    private Permissions permissions;
    private static final String TAG=User.class.getSimpleName();


    public User(){}

    public User(int mirs, Boolean admin, Boolean shabat_admin, Boolean  wh_admin, String first_name, String last_name){
        this.admin=admin;
        this.mirs=mirs;
        this.shabat_admin=shabat_admin;
        this.wh_admin=wh_admin;
        this.first_name=first_name;
        this.last_name=last_name;
    }

    public int getMirs() {
        return mirs;
    }

    public void setMirs(int mirs) {
        this.mirs = mirs;
    }

    public Boolean getAdmin() {
        return admin;
    }

    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }

    public Boolean getShabat_admin() {
        return shabat_admin;
    }

    public void setShabat_admin(Boolean shabat_admin) {
        this.shabat_admin = shabat_admin;
    }

    public Boolean getWh_admin() {
        return wh_admin;
    }

    public void setWh_admin(Boolean wh_admin) {
        this.wh_admin = wh_admin;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public Shabat getShabat() {
        return shabat;
    }

    public void setShabat(Shabat shabat) {
        this.shabat = shabat;
    }

    public String getWh_admin_branch() {
        if(wh_admin_branch==null){
            return "haifa";
        }
        switch (wh_admin_branch){
            case "חיפה":
                return "haifa";
            case "קרית אתא":
                return "kata";
            default:
                return "haifa";
        }
    }

    public void setWh_admin_branch(String wh_admin_branch) {
        this.wh_admin_branch = wh_admin_branch;
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

    public Permissions getPermissions() {
        return permissions;
    }

    public void setPermissions(Permissions permissions) {
        this.permissions = permissions;
    }

    public void saveData(Context context){
        Log.e(TAG, "USER saveData:USER fn: " +getFirst_name()+" ln: "+getLast_name());
        SharedPreferences.Editor editor = Utils.getSharedPreferencesEditor(context);
        editor.putBoolean(context.getString(R.string.is_admin), getAdmin());
        editor.putBoolean(context.getString(R.string.is_shabat_admin), getShabat_admin());
        editor.putBoolean(context.getString(R.string.is_wh_admin), getWh_admin());
        editor.putInt(context.getString(R.string.user_mirs), getMirs());
        editor.putString(context.getString(R.string.sp_last_name), getLast_name());
        editor.putString(context.getString(R.string.sp_first_name), getFirst_name());
        editor.putString(context.getString(R.string.sp_wh_admin_branch), getWh_admin_branch());
        editor.commit();
    }
    public void removeData(Context context){
        SharedPreferences.Editor editor = Utils.getSharedPreferencesEditor(context);
        editor.remove(context.getString(R.string.is_admin));
        editor.remove(context.getString(R.string.is_shabat_admin));
        editor.remove(context.getString(R.string.is_wh_admin));
        editor.remove(context.getString(R.string.user_mirs));
        editor.remove(context.getString(R.string.sp_last_name));
        editor.remove(context.getString(R.string.sp_first_name));
        editor.remove(context.getString(R.string.sp_wh_admin_branch));
        editor.commit();
    }

    @Override
    public String toString() {
        JSONObject object = new JSONObject();
        try {
            object.put("mirs",getMirs());
            object.put("wh_admin",getWh_admin());
            object.put("shabat_admin",getShabat_admin());
            object.put("admin",getAdmin());
            object.put("first_name",getFirst_name());
            object.put("last_name",getLast_name());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object.toString();
    }
}
