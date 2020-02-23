package agjs.gautham.rjsweets;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

import agjs.gautham.rjsweets.admin.DashboardAdmin;
import agjs.gautham.rjsweets.delivery.DashboardDelivery;
import agjs.gautham.rjsweets.login.Login;
import agjs.gautham.rjsweets.user.DashboardUser;
import io.paperdb.Paper;

public class Splash extends AppCompatActivity {

    private ProgressBar progressBar;
    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        progressBar = findViewById(R.id.pg);

        //Init Firebase
        database = FirebaseDatabase.getInstance();

        CheckUpdate.check_for_update(Splash.this);

        /*DatabaseReference updates = database.getReference("Updates");

        updates.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                AppUpdate appUpdate = dataSnapshot.getValue(AppUpdate.class);

                String updateUrl = appUpdate.getUpdate_url();
                Double update_version = appUpdate.getVersion();
                String changelog = appUpdate.getChangelog();
                String app_name = appUpdate.getApp_name();

                String cureent_version = getAppVersion(Splash.this);
                Double app_version = Double.parseDouble(cureent_version);

                if (app_version<update_version){

                    Log.d("Test","test");
                    showNotification();

                }else {

                    Common.changelog = null;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/

        Paper.init(this);

        String user = Paper.book().read(Common.USER_EMAIL);
        String pwd = Paper.book().read(Common.USER_PASS);
        String loginType = Paper.book().read(Common.loginType);

        if (user != null && pwd != null){

            if (!user.isEmpty() && !pwd.isEmpty()){

                if (loginType != null){

                    if (loginType.equals("0"))
                        logInUser(user, pwd);

                    if (loginType.equals("1"))
                        logInAdmin(user,pwd);

                    if (loginType.equals("2"))
                        logInDelivery(user,pwd);

                }
            }
        }else {

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    progressBar.setProgress(100);
                    Intent intent = new Intent(Splash.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            },1000);

        }
    }

    private void showNotification() {

        System.out.println("Noti");

        String CHANNEL_NAME = "RJ SWEETS UPDATE";
        String CHANNEL_ID = "rj_sweets_update";

        // Create an explicit intent for an Activity in your app
        Intent intent = new Intent(this, UpdateActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_system_update)
                .setContentTitle(getString(R.string.update_available))
                .setContentText(getString(R.string.update_description))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(false);

        CharSequence name = CHANNEL_NAME;
        String description = getString(R.string.update_description);

        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
        channel.setDescription(description);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(new Random().nextInt(), builder.build());

    }

    private String getAppVersion(Context context){

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

    private void logInAdmin(final String phone, final String pwd){

        //Init Firebase For Admin
        final DatabaseReference databaseReference = database.getReference("User");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //Check phone existence
                if (dataSnapshot.child(phone).exists()) {

                    progressBar.setProgress(100);

                    Intent intent = new Intent(Splash.this, DashboardAdmin.class);
                    Common.USER_Phone = phone;
                    intent.putExtra("Number",phone);
                    Common.loginType = "1";
                    startActivity(intent);
                    finish();

                } else {
                    progressBar.setProgress(100);
                    final View parentLayout = findViewById(android.R.id.content);
                    Snackbar.make(parentLayout, "Something went Wrong Please Login Again", Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void logInDelivery(final String phone, final String pwd){

        //Init Firebase For Delivery
        final DatabaseReference databaseReference = database.getReference("Shippers");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //Check phone existence
                if (dataSnapshot.child(phone).exists()) {

                    progressBar.setProgress(100);

                    Common.USER_Phone = phone;
                    Toast.makeText(Splash.this,"Signed In",Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(Splash.this, DashboardDelivery.class);
                    Common.loginType = "2";
                    startActivity(intent);
                    finish();

                } else {
                    progressBar.setProgress(100);
                    final View parentLayout = findViewById(android.R.id.content);
                    Snackbar.make(parentLayout, "Something went Wrong Please Login Again", Snackbar.LENGTH_LONG).show();
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

                    progressBar.setProgress(100);

                    Intent intent = new Intent(Splash.this, DashboardUser.class);
                    Common.loginType = "0";
                    Common.USER_Phone = Paper.book().read(Common.PHONE_KEY);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.activity_open_enter, R.anim.activity_open_exit );
                }else {

                    progressBar.setProgress(100);

                    Intent intent = new Intent(Splash.this, Login.class);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.activity_open_enter, R.anim.activity_open_exit );
                }
            }
        });
    }

}
