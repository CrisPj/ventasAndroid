package pythonteam.com.ventasapp;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import timber.log.Timber;

/**
 * Created by cresh on 11/6/17.
 */

public class FCMService extends FirebaseMessagingService {
@Override
public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
         verNotificacion(remoteMessage.getNotification().getBody());
        }

    private void verNotificacion(String message) {
        Intent i = new Intent(this, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendiente = PendingIntent.getActivity(this,0,i,PendingIntent.FLAG_ONE_SHOT);
        String channelId = "fcm_default_channel";
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,channelId)
                .setAutoCancel(true)
                .setContentTitle("Notificacion")
                .setContentText(message)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentIntent(pendiente);
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify(0,builder.build());
    }
}