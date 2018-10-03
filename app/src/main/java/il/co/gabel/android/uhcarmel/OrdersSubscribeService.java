package il.co.gabel.android.uhcarmel;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class OrdersSubscribeService extends FirebaseMessagingService {
    private static final String TAG=OrdersSubscribeService.class.getSimpleName();
    private Intent mSendIntent;
    private String mChannelId = "Default";

    public OrdersSubscribeService() {
    }



    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e(TAG, "From: " + remoteMessage.getFrom());
        if(remoteMessage.getFrom().endsWith(getApplicationContext().getString(R.string.new_order_topic))){
            mSendIntent = new Intent(this, OrderListActivity.class);
            mChannelId ="newOrders";
        } else {
            mSendIntent = new Intent(this, MainActivity.class);
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.e(TAG, "Message Notification Title: " + remoteMessage.getNotification().getTitle());
            Log.e(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            mSendIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, mSendIntent, PendingIntent.FLAG_ONE_SHOT);
            NotificationCompat.Builder builder = new  NotificationCompat.Builder(this, mChannelId)
                    .setSmallIcon(R.drawable.ic_stat_uhcarmel)
                    .setSound(uri)
                    .setContentTitle(remoteMessage.getNotification().getBody())
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent);
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(mChannelId, "Default channel", NotificationManager.IMPORTANCE_DEFAULT);
                manager.createNotificationChannel(channel);
            }
            manager.notify(0, builder.build());
        }
    }
}
