package agjs.gautham.rjsweets.login;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Map;

import agjs.gautham.rjsweets.Model.User;
import agjs.gautham.rjsweets.R;
import agjs.gautham.rjsweets.common.Common;
import agjs.gautham.rjsweets.common.SignUp;
import agjs.gautham.rjsweets.user.DashboardUser;
import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;

public class Login extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private TextInputLayout sEmail_Phone, sPass, reset_email;

    private long back_pressed;
    int phone_flag = 0;

    android.app.AlertDialog pdialog, pdialog2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar toolbar = findViewById(R.id.toolbar_login);
        setSupportActionBar(toolbar);

        pdialog = new SpotsDialog.Builder()
                .setContext(this)
                .setCancelable(false)
                .setMessage("Logging You In...")
                .setTheme(R.style.DialogCustom)
                .build();

        pdialog2 = new SpotsDialog.Builder()
                .setContext(this)
                .setCancelable(false)
                .setMessage("Sending E-Mail ...")
                .setTheme(R.style.DialogCustom)
                .build();

        mAuth = FirebaseAuth.getInstance();

        Paper.init(this);

        Animation fadein = AnimationUtils.loadAnimation(Login.this, R.anim.fadein);

        LinearLayout c1 = findViewById(R.id.email_container);
        c1.startAnimation(fadein);

        sEmail_Phone = findViewById(R.id.signInEmail);
        sPass = findViewById(R.id.signInPass);

        Button forgotPass = findViewById(R.id.forgot_pass);
        forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Common.isConnectedToInternet(getBaseContext())){

                    final View exp = LayoutInflater.from(Login.this).inflate(R.layout.pasword_reset,null);

                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(Login.this);
                    alertDialog.setTitle("Reset Password !");
                    alertDialog.setMessage("We will Send an E-Mail To Reset your Password");
                    alertDialog.setIcon(R.drawable.ic_email_black_24dp);
                    alertDialog.setView(exp);
                    alertDialog.setCancelable(false);

                    reset_email = exp.findViewById(R.id.PassResetEmail);

                    alertDialog.setPositiveButton("Send", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    final AlertDialog dialog = alertDialog.create();
                    dialog.show();

                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            pdialog2.show();
                            final String email = reset_email.getEditText().getText().toString();

                            if (validateOnlyEmail(email)){

                                hideKeyboard();
                                View login = findViewById(R.id.login_container);
                                FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    toast("Please Check Your E-Mail");
                                                    phone_flag = 1;
                                                }
                                            }
                                        });
                                snack(login,"Note: Please Login with E-Mail after changing Password");

                                if (pdialog2.isShowing()){
                                    pdialog2.dismiss();
                                }
                                dialog.dismiss();
                            }
                        }
                    });
                } else {
                    toast("No Internet Connection");
                }
            }
        });

    }

    public void bt_signIn_user(final View view){

        final String mail = sEmail_Phone.getEditText().getText().toString();

        if (mail.endsWith(".com")){

            sEmail_Phone.setError(null);

            if (validateEmail() && validatePass()){

                if (Common.isConnectedToInternet(getBaseContext())){

                    hideKeyboard();
                    pdialog.show();

                    final String pass = sPass.getEditText().getText().toString();

                    mAuth.signInWithEmailAndPassword(mail, pass.trim()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()){

                                //Init Firebase
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                final DatabaseReference databaseReference = database.getReference("User");

                                final FirebaseFirestore firestore;

                                firestore = FirebaseFirestore.getInstance();
                                firestore.collection("users")
                                        .document(mail)
                                        .update("Pass",pass).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task2) {
                                        if (task2.isSuccessful()){

                                            DocumentReference documentReference = firestore.collection("users").document(mail);
                                            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        DocumentSnapshot documentSnapshot = task.getResult();
                                                        assert documentSnapshot != null;
                                                        if (documentSnapshot.exists()) {
                                                            Map<String, Object> userInfo = documentSnapshot.getData();
                                                            assert userInfo != null;
                                                            String uPhone = (String) userInfo.get("Number");
                                                            databaseReference.child(uPhone).child("password").setValue(pass);

                                                            Common.USER_Phone = uPhone;
                                                            Paper.book().write(Common.PHONE_KEY,uPhone);
                                                        }
                                                    }
                                                }
                                            });
                                        }else {
                                            toast(""+task2.getException().getMessage());
                                        }
                                    }
                                });

                                Paper.book().destroy();
                                Paper.book().write(Common.loginType,"0");
                                Paper.book().write(Common.USER_EMAIL,mail);
                                Paper.book().write(Common.USER_PASS,pass);
                                Paper.book().write("sub_new","true");

                                FirebaseMessaging.getInstance().subscribeToTopic(Common.topicName);

                                if (pdialog.isShowing()){
                                    pdialog.dismiss();
                                }

                                Intent intent = new Intent(Login.this, DashboardUser.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                Common.loginType = "0";
                                Common.intentOpenAnimation(Login.this);

                                startActivity(intent);
                                finish();
                                overridePendingTransition(R.anim.activity_open_enter, R.anim.activity_open_exit);
                            } else {
                                if (pdialog.isShowing()){
                                    pdialog.dismiss();
                                }
                                Paper.book().destroy();
                                Paper.book().delete(Common.USER_EMAIL);
                                Paper.book().delete(Common.USER_PASS);
                                Toast.makeText(Login.this, "" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }else {
                if (pdialog.isShowing()){
                    pdialog.dismiss();
                }
                toast("Please Enter Valid Data");
            }

        }else {

            final String email_phone = sEmail_Phone.getEditText().getText().toString();
            final String pass = sPass.getEditText().getText().toString();

            if (validatePhone() && validatePass()){

                hideKeyboard();
                pdialog.show();

                //Init Firebase
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                final DatabaseReference databaseReference = database.getReference("User");

                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.child(email_phone).exists()){
                            User user = dataSnapshot.child(email_phone).getValue(User.class);
                            if (user.getPassword().equals(pass)){

                                final String mail = user.getEmail();
                                mAuth = FirebaseAuth.getInstance();

                                mAuth.signInWithEmailAndPassword(mail, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()){

                                            if (pdialog.isShowing()){
                                                pdialog.dismiss();
                                            }

                                            Paper.book().destroy();
                                            Paper.book().write(Common.loginType,"0");
                                            Paper.book().write(Common.USER_EMAIL,mail);
                                            Paper.book().write(Common.USER_PASS,pass);

                                            Common.loginType = "0";
                                            Common.USER_Phone = mail;
                                            Paper.book().write(Common.PHONE_KEY,mail);

                                            Paper.book().write("sub_new","true");
                                            FirebaseMessaging.getInstance().subscribeToTopic(Common.topicName);

                                            Intent intent = new Intent(Login.this, DashboardUser.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                            Common.intentOpenAnimation(Login.this);
                                            finish();
                                        } else {
                                            if (pdialog.isShowing()){
                                                pdialog.dismiss();
                                            }
                                            Paper.book().delete(Common.USER_EMAIL);
                                            Paper.book().delete(Common.USER_PASS);
                                            Paper.book().destroy();
                                            Toast.makeText(Login.this, "" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } else {
                                if (pdialog.isShowing()){
                                    pdialog.dismiss();
                                }
                                snack(view,"Wrong Password");

                            }
                        } else {
                            if (pdialog.isShowing()){
                                pdialog.dismiss();
                            }
                            snack(view,"User doesnt exists");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            } else {
                snack(view,"Enter Valid Details");
            }
        }

    }

    //Email Validation
    boolean validateEmail(){
        String email = sEmail_Phone.getEditText().getText().toString().trim();

        if (email.isEmpty()){
            sEmail_Phone.setError("Field Can't be Empty");
            sEmail_Phone.requestFocus();
            return false;
        }else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            sEmail_Phone.setError("Please Enter a Valid E-Mail");
            sEmail_Phone.requestFocus();
            return false;
        }else {
            sEmail_Phone.setError(null);
            sEmail_Phone.clearFocus();
            return true;
        }
    }

    boolean validateOnlyEmail(String email){

        if (email.isEmpty()){
            sEmail_Phone.setError("Field Can't be Empty");
            sEmail_Phone.requestFocus();
            return false;
        }else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            sEmail_Phone.setError("Please Enter a Valid E-Mail");
            sEmail_Phone.requestFocus();
            return false;
        }else {
            sEmail_Phone.setError(null);
            sPass.clearFocus();
            return true;
        }
    }

    //Password Validation
    boolean validatePass() {
        String pass = sPass.getEditText().getText().toString();
        if (pass.isEmpty()) {
            sPass.setError("Field Can't be Empty");
            sPass.requestFocus();
            return false;
        } else {
            sPass.setError(null);
            sPass.clearFocus();
            return true;
        }
    }

    //Phone Validation
    boolean validatePhone() {

        sEmail_Phone = findViewById(R.id.signInEmail);
        String sPhone = sEmail_Phone.getEditText().getText().toString();
        if (sPhone.isEmpty()) {
            sEmail_Phone.setError("Field Can't be Empty");
            sEmail_Phone.requestFocus();
            return false;
        }else if (sPhone.length() > 10 | sPhone.length() < 10){
            sEmail_Phone.setError("Invalid Phone Number !");
            sEmail_Phone.requestFocus();
            return false;
        } else {
            sEmail_Phone.setError(null);
            sEmail_Phone.clearFocus();
            return true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.admin_login:
                startActivity(new Intent(Login.this, LoginAdmin.class));
                Common.intentOpenAnimation(Login.this);
                return true;

            case R.id.delivery_login:
                startActivity(new Intent(Login.this, DeliveryLogin.class));
                Common.intentOpenAnimation(Login.this);
                return true;
        }
        return(super.onOptionsItemSelected(item));
    }

    public void bt_skip(View view){
        startActivity(new Intent(Login.this, DashboardUser.class));
        Common.intentOpenAnimation(Login.this);
        finish();
    }

    public void bt_signup(View view){
        startActivity(new Intent(Login.this, SignUp.class));
        Common.intentOpenAnimation(Login.this);
    }

    void hideKeyboard(){
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(sPass.getWindowToken(), 0);
    }

    private void toast(String msg){
        Toast toast = Toast.makeText(Login.this,msg,Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.show();
    }

    private void snack(View view, String msg){

        Snackbar snackbar = Snackbar.make(view,msg,Snackbar.LENGTH_LONG);
        View snackView = snackbar.getView();
        TextView tv = snackView.findViewById(com.google.android.material.R.id.snackbar_text);
        tv.setTextColor(Color.WHITE);
        snackbar.show();

    }

    @Override
    public void onBackPressed() {

        View view = findViewById(R.id.login_container);

        if (back_pressed + 2000 > System.currentTimeMillis()){
            finish();
            moveTaskToBack(true);
            System.exit(1);
            android.os.Process.killProcess(android.os.Process.myPid());
        }else {
            snack(view,"Press Again to Exit");
            back_pressed = System.currentTimeMillis();
        }
    }
}
