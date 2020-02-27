package agjs.gautham.rjsweets.common;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import agjs.gautham.rjsweets.Model.AppUpdate;
import agjs.gautham.rjsweets.R;

public class CheckUpdate {

    private static NotificationManager notifManager;

    public static void check_for_update(final Context context){

        DatabaseReference updates = FirebaseDatabase.getInstance().getReference("Updates");

        updates.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                AppUpdate appUpdate = dataSnapshot.getValue(AppUpdate.class);

                Double update_version = appUpdate.getVersion();

                String cureent_version = getAppVersion(context);
                Double app_version = Double.parseDouble(cureent_version);

                if (app_version<update_version){

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

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
}
