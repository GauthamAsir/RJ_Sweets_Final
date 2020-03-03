package agjs.gautham.rjsweets;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import agjs.gautham.rjsweets.admin.DashboardAdmin;
import agjs.gautham.rjsweets.common.Common;
import agjs.gautham.rjsweets.common.MainActivity;
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

                }else {
                    defaultLogin();
                }
            }
        }else {
            defaultLogin();
        }

        //CheckUpdate runner = new CheckUpdate(Splash.this);
        //runner.execute();

        //Check For App-Update
        /*final String app_version =CheckUpdate.getAppVersion(Splash.this);

        DatabaseReference databaseReference = database.getReference("Updates");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                AppUpdate appUpdate = dataSnapshot.getValue(AppUpdate.class);

                Double update_version = appUpdate.getVersion();
                Double appV = Double.parseDouble(app_version);

                if (appV<update_version){

                    CheckUpdate runner = new CheckUpdate(Splash.this);
                    runner.execute();

                    progressBar.setProgress(100);
                    startActivity(new Intent(Splash.this, UpdateActivity.class));
                    Common.intentOpenAnimation(Splash.this);
                    finish();

                }else {

                    if (user != null && pwd != null){

                        if (!user.isEmpty() && !pwd.isEmpty()){

                            if (loginType != null){

                                if (loginType.equals("0"))
                                    logInUser(user, pwd);

                                if (loginType.equals("1"))
                                    logInAdmin(user,pwd);

                                if (loginType.equals("2"))
                                    logInDelivery(user,pwd);

                            }else {
                                defaultLogin();
                            }
                        }
                    }else {
                        defaultLogin();
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/

    }

    private void defaultLogin(){

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                progressBar.setProgress(100);
                Intent intent = new Intent(Splash.this, MainActivity.class);
                startActivity(intent);
                Common.intentOpenAnimation(Splash.this);
                finish();
            }
        },1000);

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
                    Common.intentOpenAnimation(Splash.this);
                    finish();

                } else {
                    progressBar.setProgress(100);
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
                    Common.Name = dataSnapshot.child(phone).child("name").getValue(String.class);
                    Toast.makeText(Splash.this,"Signed In",Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(Splash.this, DashboardDelivery.class);
                    Common.loginType = "2";
                    startActivity(intent);
                    Common.intentOpenAnimation(Splash.this);
                    finish();

                } else {
                    progressBar.setProgress(100);
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

                    progressBar.setProgress(100);

                    Intent intent = new Intent(Splash.this, DashboardUser.class);
                    Common.loginType = "0";
                    Common.USER_Phone = Paper.book().read(Common.PHONE_KEY);
                    startActivity(intent);
                    Common.intentOpenAnimation(Splash.this);
                    finish();
                }else {

                    progressBar.setProgress(100);

                    Intent intent = new Intent(Splash.this, Login.class);
                    startActivity(intent);
                    Common.intentOpenAnimation(Splash.this);
                    finish();
                }
            }
        });
    }

}
