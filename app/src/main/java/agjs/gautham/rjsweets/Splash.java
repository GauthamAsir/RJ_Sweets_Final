package agjs.gautham.rjsweets;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import agjs.gautham.rjsweets.Model.Flags;
import agjs.gautham.rjsweets.admin.DashboardAdmin;
import agjs.gautham.rjsweets.common.CheckUpdate;
import agjs.gautham.rjsweets.common.Common;
import agjs.gautham.rjsweets.common.MainActivity;
import agjs.gautham.rjsweets.common.UpdateActivity;
import agjs.gautham.rjsweets.delivery.DashboardDelivery;
import agjs.gautham.rjsweets.login.Login;
import agjs.gautham.rjsweets.user.DashboardUser;
import io.paperdb.Paper;

public class Splash extends AppCompatActivity {

    private TextView progress;
    private FirebaseDatabase database;

    private String CHANNEL_ID = "CHANNEL_1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        progress = findViewById(R.id.pg);

        //Init Firebase
        database = FirebaseDatabase.getInstance();

        Paper.init(this);

        DatabaseReference flags = database.getReference("flags");
        flags.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Common.flags = dataSnapshot.getValue(Flags.class);

                if (Common.flags.isSplashUpdateCheck()){

                    //Check For App-Update
                    final String app_version =CheckUpdate.getAppVersion(Splash.this);
                    Double update_version = Common.flags.getLatestVersion();
                    Double appV = Double.parseDouble(app_version);

                    if (appV<update_version){

                        if (Common.flags.isMandatoryLatestUpdate()){

                            CheckUpdate runner = new CheckUpdate(Splash.this);
                            runner.execute();

                            progress.setText(R.string.connection_established);
                            startActivity(new Intent(Splash.this, UpdateActivity.class));
                            Common.intentOpenAnimation(Splash.this);
                            finish();

                        }else {
                            showNotification();
                            init();
                        }

                    }else {
                        showNotification();
                        init();
                    }
                }else {
                    init();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showNotification(){

        makeNotificationChannel();

        NotificationCompat.Builder notification =
                new NotificationCompat.Builder(this, CHANNEL_ID);

        NotificationManager notificationManager =
                (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        notification
                .setSmallIcon(R.mipmap.ic_launcher) // can use any other icon
                .setContentTitle("New Update Available!")
                .setContentText("You can update the app from Settings or from our Github Releases");

        assert notificationManager != null;
        notificationManager.notify(1, notification.build());

    }

    private void makeNotificationChannel(){
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Update Available", NotificationManager.IMPORTANCE_HIGH );
        channel.setShowBadge(true); // set false to disable badges, Oreo exclusive

        NotificationManager notificationManager =
                (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        assert notificationManager != null;
        notificationManager.createNotificationChannel(channel);
    }

    private void init() {

        String user = Paper.book().read(Common.USER_EMAIL);
        String pwd = Paper.book().read(Common.USER_PASS);
        String loginType = Paper.book().read(Common.loginType);

        if (user != null && pwd != null){

            if (!user.isEmpty() && !pwd.isEmpty()){

                if (loginType != null){

                    if (loginType.equals("0")){
                        progress.setText(R.string.connection_established);
                        logInUser(user, pwd);
                    }

                    if (loginType.equals("1")){
                        progress.setText(R.string.connection_established);
                        logInAdmin(user);
                    }

                    if (loginType.equals("2")){
                        progress.setText(R.string.connection_established);
                        logInDelivery(user);
                    }

                }else {
                    progress.setText(R.string.connection_established);
                    defaultLogin();
                }
            }
        }else {
            progress.setText(R.string.connection_established);
            defaultLogin();
        }

    }

    private void defaultLogin(){

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent intent = new Intent(Splash.this, MainActivity.class);
                startActivity(intent);
                Common.intentOpenAnimation(Splash.this);
                finish();
            }
        },1000);

    }

    private void logInAdmin(final String phone){

        //Init Firebase For Admin
        final DatabaseReference databaseReference = database.getReference("User");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //Check phone existence
                if (dataSnapshot.child(phone).exists()) {

                    progress.setText(R.string.logging_in);

                    Intent intent = new Intent(Splash.this, DashboardAdmin.class);
                    Common.USER_Phone = phone;
                    intent.putExtra("Number",phone);
                    Common.loginType = "1";
                    startActivity(intent);
                    Common.intentOpenAnimation(Splash.this);
                    finish();

                } else {
                    startActivity(new Intent(Splash.this, MainActivity.class));
                    Common.intentOpenAnimation(Splash.this);
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void logInDelivery(final String phone){

        //Init Firebase For Delivery
        final DatabaseReference databaseReference = database.getReference("Shippers");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //Check phone existence
                if (dataSnapshot.child(phone).exists()) {

                    progress.setText(R.string.logging_in);

                    Common.USER_Phone = phone;
                    Common.Name = dataSnapshot.child(phone).child("name").getValue(String.class);
                    Toast.makeText(Splash.this,"Signed In",Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(Splash.this, DashboardDelivery.class);
                    Common.loginType = "2";
                    startActivity(intent);
                    Common.intentOpenAnimation(Splash.this);
                    finish();

                } else {
                    startActivity(new Intent(Splash.this, MainActivity.class));
                    Common.intentOpenAnimation(Splash.this);
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void logInUser(final String phone, final String pwd) {

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        mAuth.signInWithEmailAndPassword(phone,pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){

                    progress.setText(R.string.logging_in);

                    Intent intent = new Intent(Splash.this, DashboardUser.class);
                    Common.loginType = "0";
                    Common.USER_Phone = Paper.book().read(Common.PHONE_KEY);
                    startActivity(intent);
                    Common.intentOpenAnimation(Splash.this);
                    finish();
                }else {
                    Intent intent = new Intent(Splash.this, Login.class);
                    startActivity(intent);
                    Common.intentOpenAnimation(Splash.this);
                    finish();
                }
            }
        });
    }

}
