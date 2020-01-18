package agjs.gautham.rjsweets;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

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
import com.google.firebase.messaging.FirebaseMessaging;

import agjs.gautham.rjsweets.admin.DashboardAdmin;
import agjs.gautham.rjsweets.delivery.DashboardDelivery;
import agjs.gautham.rjsweets.login.Login;
import agjs.gautham.rjsweets.user.DashboardUser;
import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {

    AlertDialog dialog;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //getSupportActionBar().hide();

        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setCancelable(false)
                .setMessage("Please wait ...")
                .build();

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

                }
            }
        }
    }

    private void logInAdmin(final String phone, final String pwd){

        //Init Firebase For Admin
        final DatabaseReference databaseReference = database.getReference("User");

        dialog.show();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //Check phone existence
                if (dataSnapshot.child(phone).exists()) {

                    dialog.dismiss();
                    Intent intent = new Intent(MainActivity.this, DashboardAdmin.class);
                    Common.USER_Phone = phone;
                    intent.putExtra("Number",phone);
                    Common.loginType = "1";
                    startActivity(intent);
                    finish();

                } else {
                    final View parentLayout = findViewById(android.R.id.content);
                    dialog.dismiss();
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
        dialog.show();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //Check phone existence
                if (dataSnapshot.child(phone).exists()) {

                    dialog.dismiss();
                    Common.USER_Phone = phone;
                    Toast.makeText(MainActivity.this,"Signed In",Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(MainActivity.this, DashboardDelivery.class);
                    Common.loginType = "2";
                    startActivity(intent);
                    finish();

                } else {
                    final View parentLayout = findViewById(android.R.id.content);
                    dialog.dismiss();
                    Snackbar.make(parentLayout, "Something went Wrong Please Login Again", Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void logInUser(final String phone, final String pwd) {

        dialog.show();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        mAuth.signInWithEmailAndPassword(phone,pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    dialog.dismiss();
                    Intent intent = new Intent(MainActivity.this, DashboardUser.class);
                    Common.loginType = "0";
                    Common.USER_Phone = Paper.book().read(Common.PHONE_KEY);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.activity_open_enter, R.anim.activity_open_exit );
                }else {
                    dialog.dismiss();
                    Intent intent = new Intent(MainActivity.this, Login.class);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.activity_open_enter, R.anim.activity_open_exit );
                }
            }
        });
    }

    public void bt_next(View view){
        startActivity(new Intent(MainActivity.this, Login.class));
    }
}
