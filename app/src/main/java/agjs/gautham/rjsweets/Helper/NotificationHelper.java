package agjs.gautham.rjsweets.Helper;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.net.Uri;

import agjs.gautham.rjsweets.R;

public class NotificationHelper extends ContextWrapper {

    private static final String RJSWEETS_CHANNEL_ID = "agjs.gautham.rjsweets.RJSweets";
    private static final String RJSWEETS_CHANNEL_NAME = "RJ Sweets Final";

    private NotificationManager manager;

    public NotificationHelper(Context base) {
        super(base);
        createChannel();
    }

    private void createChannel() {

        NotificationChannel rjChannel = new NotificationChannel(RJSWEETS_CHANNEL_ID, RJSWEETS_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT);
        rjChannel.enableLights(true);
        rjChannel.enableVibration(true);
        rjChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        getManager().createNotificationChannel(rjChannel);
    }

    public NotificationManager getManager() {
        if (manager == null){
            manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return manager;
    }

    public Notification.Builder rjSweetsChannelNotification(String title, String body, PendingIntent contentIntent,
                                                            Uri soundUri){
        return  new Notification.Builder(getApplicationContext(), RJSWEETS_CHANNEL_ID)
                .setContentIntent(contentIntent)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.drawable.sweet_icon_notification)
                .setSound(soundUri)
                .setAutoCancel(true);
    }
}
