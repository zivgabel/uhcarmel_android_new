package il.co.gabel.android.uhcarmel.security;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Arrays;
import java.util.Objects;

import il.co.gabel.android.uhcarmel.R;
import il.co.gabel.android.uhcarmel.Utils;

public class UHFireBaseManager {
    private static final String TAG = UHFireBaseManager.class.getSimpleName();
    private final AuthenticationListener authenticationListener;
    private static FirebaseAuth firebaseAuth;
    private static FirebaseAuth.AuthStateListener authStateListener;
    private Context context;
    private DatabaseReference rootDatabaseReference;
    private DatabaseReference databaseReference;
    private ChildEventListener childEventListener;
    private ChildEventListener itemsChildEventListener;
    private User user;



    public interface AuthenticationListener{
        void userAdded();
        void userRemoved();
        void userChanged();
        void fireBaseUserSignedIn();
        void fireBaseUserSignedOut();
    }

    private void testOrdersListener(){
        Query query = rootDatabaseReference.child("orders").orderByChild("fb_key").equalTo(Utils.getUserUID(context));
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.e(TAG, "onChildAdded ORDRES: "+dataSnapshot.getKey());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void addItemsEventListener(ChildEventListener listener){
        rootDatabaseReference.child("items").addChildEventListener(listener);
        testOrdersListener();
    }
    public void removeItemsEventListener(ChildEventListener listener){
        rootDatabaseReference.child("items").removeEventListener(listener);
    }


    public UHFireBaseManager(final Context context, AuthenticationListener listener){
        this.context=context;
        authenticationListener=listener;
        if(firebaseAuth==null) {
            firebaseAuth = FirebaseAuth.getInstance();
        }
        if(rootDatabaseReference==null){
            rootDatabaseReference = Utils.getFBDBReference(context);
        }
        if(authStateListener==null) {
            authStateListener = new FirebaseAuth.AuthStateListener() {
                SharedPreferences.Editor editor = Utils.getSharedPreferencesEditor(context);

                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                    Log.e(TAG, "onAuthStateChanged: " + firebaseUser);
                    if (firebaseUser != null) {
                        SharedPreferences.Editor editor = Utils.getSharedPreferencesEditor(context);
                        editor.putString(context.getString(R.string.user_uid_key), firebaseUser.getUid());
                        editor.commit();
                        authenticationListener.fireBaseUserSignedIn();
                        removeFireBaseUserListener();
                        getUserDetails();
                    } else {
                        removeUserData();
                        authenticationListener.fireBaseUserSignedOut();
                    }
                }
            };
            firebaseAuth.addAuthStateListener(authStateListener);
        }
    }



    public boolean isFireBaseUserExists(){
        String uid = Utils.getUserUID(context);
        return (uid!=null);
    }

    private final void removeUserData(){
        unSubscribeToOrdersTopic();
        SharedPreferences.Editor editor = Utils.getSharedPreferencesEditor(context);
        editor.remove(context.getString(R.string.user_uid_key));
        editor.commit();
        user=null;
        removeFireBaseUserListener();
        authenticationListener.userRemoved();
    }


    public void authenticate(){

        context.startActivity(AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(Arrays.asList(
                        new AuthUI.IdpConfig.EmailBuilder().build(),
                        new AuthUI.IdpConfig.GoogleBuilder().build(),
                        new AuthUI.IdpConfig.PhoneBuilder().build()
                )).build());
    }

    public void getUserDetails(){
        if(isFireBaseUserExists()) {
            Log.e(TAG, "getUserDetails: "+Utils.getUserUID(context) );
            if (databaseReference == null) {
                databaseReference = rootDatabaseReference.child("users").child(Utils.getUserUID(context));
            }
            attachListeners();
        } else {
            signout();
            removeUserData();
        }
    }

    private void attachListeners() {
        Log.e(TAG, "attachListeners: ");
        if (childEventListener == null) {
            childEventListener = new ChildEventListener() {
                private void userChangeHandler(@NonNull DataSnapshot dataSnapshot) {
                    user = dataSnapshot.getValue(User.class);
                    subscribeToOrdersTopic();
                    Log.e(TAG, "user data changeHandler: " + user.getFirstName() + " " + user.getLastName());
                }


                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    Log.e(TAG, "onChildAdded: UID " + dataSnapshot.getKey());
                    if (dataSnapshot.getKey().equals("user")) {
                        userChangeHandler(dataSnapshot);
                        authenticationListener.userAdded();
                    }
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    Log.e(TAG, "onChildChanged: UID " + dataSnapshot.getKey());
                    if (Objects.equals(dataSnapshot.getKey(), "user")) {
                        userChangeHandler(dataSnapshot);
                        authenticationListener.userChanged();
                    }
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                    Log.e(TAG, "onChildRemoved: UID " + dataSnapshot.getKey());
                    if (Objects.equals(dataSnapshot.getKey(), "user")) {
                        removeUserData();
                    }
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    Log.e(TAG, "onChildMoved: UID " + dataSnapshot.getKey());
                    if (Objects.equals(dataSnapshot.getKey(), "user")) {
                        removeUserData();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e(TAG, "onCancelled: "+databaseError.getDetails() );
                    Log.e(TAG, "onCancelled: "+databaseError.getMessage() );
                }
            };
            databaseReference.addChildEventListener(childEventListener);
        }
    }

    public void signout(){
        Log.e(TAG, "signout: ");
        firebaseAuth.signOut();
        removeUserData();
    }

    public User getUser(){
        return user;
    }

    private void unSubscribeToOrdersTopic(){
        if(user!=null) {
            Log.i(TAG, "UnsubscribeToOrdersTopic: topic: " + context.getString(R.string.new_order_user_topic_prefix) + String.valueOf(user.getMirs()));
            FirebaseMessaging.getInstance().unsubscribeFromTopic(context.getString(R.string.new_order_user_topic_prefix) + String.valueOf(user.getMirs()));
            Log.i(TAG, "UnsubscribeToOrdersTopic: topic: " + user.getWh_admin_branch() + context.getString(R.string.new_order_topic));
            FirebaseMessaging.getInstance().unsubscribeFromTopic(user.getWh_admin_branch() + context.getString(R.string.new_order_topic));
        }
    }


    private void subscribeToOrdersTopic(){
        unSubscribeToOrdersTopic();
        FirebaseMessaging.getInstance().subscribeToTopic(context.getString(R.string.new_order_user_topic_prefix)+String.valueOf(user.getMirs()))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "Subscribed";
                        if (!task.isSuccessful()) {
                            msg = "Not subscribed";
                        }
                        Log.d(TAG, msg);
                    }
                });
        if(user.getPermissions().getWarehouse()){
            Log.e(TAG, "subscribeToOrdersTopic: topic: "+user.getWh_admin_branch()+context.getString(R.string.new_order_topic) );
            FirebaseMessaging.getInstance().subscribeToTopic(user.getWh_admin_branch()+context.getString(R.string.new_order_topic))
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            String msg = "Subscribe to orders";
                            if (!task.isSuccessful()) {
                                msg = "Not subscribed to orders";
                            }
                            Log.d(TAG, msg);
                        }
                    });
        }
    }

    public void removeFireBaseUserListener(){
        if(databaseReference!=null && childEventListener !=null){
            databaseReference.removeEventListener(childEventListener);
        }
        databaseReference=null;
        childEventListener =null;
    }
}
