package agjs.gautham.rjsweets.Service;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;
import java.util.Random;

import agjs.gautham.rjsweets.Common;
import agjs.gautham.rjsweets.Helper.NotificationHelper;
import agjs.gautham.rjsweets.user.DashboardUser;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    String phone="";
    FirebaseFirestore firestore;
    FirebaseUser mUser;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        sendNotification(remoteMessage);
    }

    private void sendNotification(RemoteMessage remoteMessage) {

        firestore = FirebaseFirestore.getInstance();

        //To get Phone Number
        if (mUser != null) {
            String email = mUser.getEmail();

            assert email != null;
            DocumentReference documentReference = firestore.collection("users").document(email);
            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        assert documentSnapshot != null;
                        if (documentSnapshot.exists()) {
                            Map<String, Object> userInfo = documentSnapshot.getData();
                            assert userInfo != null;
                            phone = (String) userInfo.get("Number");
                        }
                    }
                }
            });
        }

        RemoteMessage.Notification notification = remoteMessage.getNotification();
        String title = notification.getTitle();
        String content = notification.getBody();
        Intent intent = new Intent(this, DashboardUser.class);
        intent.putExtra(Common.USER_Phone, phone);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationHelper helper = new NotificationHelper(this);

        Notification.Builder builder = helper.rjSweetsChannelNotification(title,content,pendingIntent,defaultSoundUri);

        //Generating Random SendNotification ID to show all notification
        helper.getManager().notify(new Random().nextInt(), builder.build());
    }
}