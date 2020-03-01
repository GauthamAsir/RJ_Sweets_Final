package agjs.gautham.rjsweets.common;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import agjs.gautham.rjsweets.R;

public class CheckUpdate extends AsyncTask {

    private static NotificationManager notifManager;
    private Context context;

    public CheckUpdate(Context context) {
        this.context = context;
    }

    public static String getAppVersion(Context context){

        String result = "";
        try {
            result = context.getPackageManager().getPackageInfo(context.getPackageName(),0)
                    .versionName;
            result = result.replaceAll("[a-zA-Z]|-","");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    protected Object doInBackground(Object[] objects) {

        Log.d("Update Status","Update Available");

        String CHANNEL_ID = "rj_sweets_update";

        String title = context.getString(R.string.update_available);

        NotificationCompat.Builder builder;

        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
        bigTextStyle.setBigContentTitle(title);
        bigTextStyle.bigText(context.getString(R.string.update_description));

        if (notifManager == null) {
            notifManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        }

        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel mChannel = notifManager.getNotificationChannel(CHANNEL_ID);
        if (mChannel == null) {
            mChannel = new NotificationChannel(CHANNEL_ID, title, importance);
            notifManager.createNotificationChannel(mChannel);
        }

        builder = new NotificationCompat.Builder(context, CHANNEL_ID);

        builder.setSmallIcon(R.drawable.ic_system_update)   // required
                .setStyle(bigTextStyle)
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true);

        Notification notification = builder.build();
        notifManager.notify(0, notification);

        return null;
    }
}
