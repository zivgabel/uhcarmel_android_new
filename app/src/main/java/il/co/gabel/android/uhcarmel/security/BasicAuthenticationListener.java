package il.co.gabel.android.uhcarmel.security;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import il.co.gabel.android.uhcarmel.MainActivity;

public class BasicAuthenticationListener implements UHFireBaseManager.AuthenticationListener {
    private static final String TAG = BasicAuthenticationListener.class.getSimpleName();
    private Context context;


    public BasicAuthenticationListener(Context context) {
        this.context = context;
    }

    @Override
    public void userAdded() {
        Log.i(TAG, "userAdded");
    }

    @Override
    public void userRemoved() {
        Log.i(TAG, "userRemoved");
        context.startActivity(new Intent(context,MainActivity.class));
    }

    @Override
    public void userChanged() {
        Log.i(TAG, "userChanged");
        context.startActivity(new Intent(context,MainActivity.class));
    }

    @Override
    public void fireBaseUserSignedIn() {
        Log.i(TAG, "fireBaseUserSignedIn");
    }

    @Override
    public void fireBaseUserSignedOut() {
        Log.i(TAG, "fireBaseUserSignedOut");
        context.startActivity(new Intent(context,MainActivity.class));
    }
}
