package agjs.gautham.rjsweets.Service;

import android.app.Notification;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

import agjs.gautham.rjsweets.Helper.NotificationHelper;

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

        NotificationHelper helper = new NotificationHelper(this);

        Notification.Builder builder = helper.rjSweetsChannelNotification(title, content);

        //Generating Random SendNotification ID to show all notification
        helper.getManager().notify(new Random().nextInt(), builder.build());

    }
}