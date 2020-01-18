package agjs.gautham.rjsweets.Service;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executor;

import agjs.gautham.rjsweets.Common;
import agjs.gautham.rjsweets.Helper.NotificationHelper;
import agjs.gautham.rjsweets.Model.Token;
import agjs.gautham.rjsweets.user.DashboardUser;

public class FirebaseMessagingServiceCustom extends com.google.firebase.messaging.FirebaseMessagingService {

    FirebaseFirestore firestore;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        sendNotification(remoteMessage);
    }

    private void sendNotification(RemoteMessage remoteMessage) {

        firestore = FirebaseFirestore.getInstance();

        RemoteMessage.Notification notification = remoteMessage.getNotification();
        String title = notification.getTitle();
        String content = notification.getBody();
        Intent intent = new Intent(this, DashboardUser.class);
        intent.putExtra(Common.USER_Phone, Common.USER_Phone);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationHelper helper = new NotificationHelper(this);

        Notification.Builder builder = helper.rjSweetsChannelNotification(title,content,pendingIntent,defaultSoundUri);

        //Generating Random SendNotification ID to show all notification
        helper.getManager().notify(new Random().nextInt(), builder.build());
    }
}