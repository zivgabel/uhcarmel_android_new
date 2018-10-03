package il.co.gabel.android.uhcarmel;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import il.co.gabel.android.uhcarmel.security.User;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Utils {

    private static final String TAG = Utils.class.getSimpleName();
    private static SharedPreferences sp;
    private static SharedPreferences.Editor spe;
    private static FirebaseDatabase firebaseDatabase;
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");


    public static SharedPreferences.Editor getSharedPreferencesEditor(Context context){
        if (spe!=null){
            return spe;
        }
        getSharedPreferences(context);
        spe = sp.edit();
        return spe;
    }

    public static SharedPreferences getSharedPreferences(Context context){
        if(sp!=null){
            return sp;
        }
        sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp;
    }

    public static User currentUser(Context context){
        SharedPreferences sp = getSharedPreferences(context);
        int mirs = sp.getInt(context.getString(R.string.user_mirs),0);
        if(mirs==0){
            return null;
        }
        Boolean isAdmin=sp.getBoolean(context.getString(R.string.is_admin),false);
        Boolean isShabatAdmin=sp.getBoolean(context.getString(R.string.is_shabat_admin),false);
        Boolean isWhAdmin=sp.getBoolean(context.getString(R.string.is_wh_admin),false);
        String first_name=sp.getString(context.getString(R.string.sp_first_name),null);
        String last_name=sp.getString(context.getString(R.string.sp_last_name),null);

        return new User(mirs,isAdmin,isShabatAdmin,isWhAdmin,first_name,last_name);
    }

    public static String getUserUID(Context context){
        SharedPreferences sp = getSharedPreferences(context);
        return sp.getString(context.getString(R.string.user_uid_key),null);
    }

    private static FirebaseDatabase getFBDatabase(){
        if(firebaseDatabase==null){
            firebaseDatabase = FirebaseDatabase.getInstance();
        }
        return firebaseDatabase;
    }

    public static DatabaseReference getFBDBReference(Context context){
        String prefix = context.getString(R.string.FIREBASE_DATABASE_PREFIX);
        return getFBDatabase().getReference().child(prefix);
    }


    public static void sendNotification(String topic,String notifBody){
        String postUrl="https://fcm.googleapis.com/fcm/send";
        JSONObject object = new JSONObject();
        JSONObject object1 = new JSONObject();
        try {
            object.put("to","/topics/"+topic);
            object.put("collapse_key",  "type_a");
            object1.put("body",notifBody);
            object.put("notification",object1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String postBody=object.toString();
        Log.e(TAG, "sendNotification: "+postBody );

        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(JSON, postBody);


        final Request request = new Request.Builder()
                .url(postUrl)
                .post(body)
                .addHeader("Authorization","key=AAAAtyCpMnI:APA91bEdJ-cycEpKlBE7NeL3MmdpBxfj0-6YEnVQ3q4JWkB9mGQSTkqNWgQD5anjO0Ar3Bk167vfBFf0IKFSQJKv3eC4dWklPPiJ1s0OaGqh7auZy8Qx9NKIbRh1tY1bBWG0nlRPnwqU")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "onFailure: FAILED!!!" );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.e(TAG, "onResponse: SUCCESS" );
                Log.e(TAG, "onResponse: "+response.toString() );
                Log.e(TAG, "onResponse: "+response.body().string() );
            }
        });
    }



}
