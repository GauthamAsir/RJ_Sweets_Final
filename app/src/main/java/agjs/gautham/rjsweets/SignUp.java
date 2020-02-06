package agjs.gautham.rjsweets;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import agjs.gautham.rjsweets.Model.User;
import agjs.gautham.rjsweets.user.DashboardUser;
import dmax.dialog.SpotsDialog;

public class SignUp extends AppCompatActivity {

    private TextInputLayout tname, temail, tpass, tphone;
    private String verificationId;

    private LinearLayout name_parent, email_parent, pass_parent, pno_parent;
    private Button status1, status2, status3, status4;
    private Snackbar snackbar;

    private String final_name, final_email, final_pass, final_pno;

    private FirebaseFirestore firestore;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    DatabaseReference databse_user;

    private int phoneFlag = 0;
    android.app.AlertDialog dialog, vdialog;

    Animation slidein, slideout, back_slidein, back_slideout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setCancelable(false)
                .setMessage("Registering You In ...")
                .setTheme(R.style.DialogCustom)
                .build();

        vdialog = new SpotsDialog.Builder()
                .setContext(this)
                .setCancelable(false)
                .setMessage("Verifying Code ...")
                .setTheme(R.style.DialogCustom)
                .build();

        slideout = AnimationUtils.loadAnimation(SignUp.this, R.anim.slide_out_left);
        slidein = AnimationUtils.loadAnimation(SignUp.this, R.anim.slide_in_right);

        back_slidein = AnimationUtils.loadAnimation(SignUp.this,R.anim.slide_in_left);
        back_slideout = AnimationUtils.loadAnimation(SignUp.this,R.anim.slide_out_right);

        //Init Firebase
        database = FirebaseDatabase.getInstance();
        databse_user = database.getReference("User");

        tname = findViewById(R.id.signUpName);
        tpass = findViewById(R.id.signUpPass);
        temail = findViewById(R.id.signUpEmail);
        tphone = findViewById(R.id.signUpPhone);

        name_parent = findViewById(R.id.name_container_parent);
        email_parent = findViewById(R.id.email_container_parent);
        pass_parent = findViewById(R.id.pass_container_parent);
        pno_parent = findViewById(R.id.pno_container_parent);

        status1 = findViewById(R.id.status1);
        status2 = findViewById(R.id.status2);
        status3 = findViewById(R.id.status3);
        status4 = findViewById(R.id.status4);

        RelativeLayout root = findViewById(R.id.root);

        snackbar = Snackbar.make(root,"Enter Valid Details",Snackbar.LENGTH_SHORT);
        View snackView = snackbar.getView();
        TextView tv = snackView.findViewById(com.google.android.material.R.id.snackbar_text);
        tv.setTextColor(Color.WHITE);

        mAuth = FirebaseAuth.getInstance();

    }

    public void bt_signup_user(){

        if (Common.isConnectedToInternet(getBaseContext())){

            if (final_name.isEmpty() | final_email.isEmpty() | final_pass.isEmpty() | final_pno.isEmpty()) {
                return;
            }

            if (phoneFlag!=0){
                return;
            }

            dialog.show();

            if (phoneFlag == 0){

                String phoneNumber = "+" + "91" + final_pno;
                sendVerificationCode(phoneNumber);
                if (dialog.isShowing())
                    dialog.dismiss();
                showAlertDialog();
            }

        }else {
            toast("No Internet Connection");
        }

    }

    private void sendVerificationCode(String number){

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallBack
        );
    }

    private void verifyCode(String code){
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signinwithCredential(credential);
    }

    private void signinwithCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()){

                            dialog.show();

                            final String semail = final_email;
                            final String spass1 = final_pass;
                            final String susername = final_name;
                            final String spno = final_pno;

                            mAuth.createUserWithEmailAndPassword(semail, spass1).addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {

                                        User user = new User(susername,spass1,semail,spno);
                                        databse_user.child(spno).setValue(user);

                                        Map<String, Object> userMap = new HashMap<>();
                                        userMap.put("Username",susername);
                                        userMap.put("Pass",spass1);
                                        userMap.put("Number",spno);
                                        userMap.put("Email",semail);
                                        firestore = FirebaseFirestore.getInstance();
                                        firestore.collection("users")
                                                .document(semail)
                                                .set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {

                                            @Override
                                            public void onComplete(@NonNull Task<Void> task2) {
                                                if (task2.isSuccessful()){
                                                    if (dialog.isShowing()){
                                                        dialog.dismiss();
                                                    }
                                                    startActivity(new Intent(SignUp.this, DashboardUser.class)
                                                            .addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                                                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                                }else {
                                                    if (dialog.isShowing()){
                                                        dialog.dismiss();
                                                    }
                                                    toast(""+task2.getException().getMessage());
                                                }
                                            }
                                        });
                                    } else {
                                        if (dialog.isShowing()){
                                            dialog.dismiss();
                                        }
                                        toast(task.getException().getMessage());
                                    }
                                }
                            });
                        }
                        else {
                            if (dialog.isShowing()){
                                dialog.dismiss();
                            }
                            toast("Phone Verification Failed");
                        }
                    }
                });
    }


    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationId = s;
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if (code != null){
                verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(SignUp.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    };


    private void showAlertDialog() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Phone Number Verification");
        alertDialog.setMessage("Enter The Code Sent To Your Number");

        final EditText adCode = new EditText(this);
        adCode.setInputType(InputType.TYPE_CLASS_NUMBER);

        alertDialog.setView(adCode); //Adding Editext To Alert Dialog
        alertDialog.setCancelable(false);

        alertDialog.setPositiveButton("Verify", new DialogInterface.OnClickListener() {
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

        final AlertDialog alertDialog1 = alertDialog.create();
        alertDialog1.show();

        alertDialog1.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vdialog.show();

                String code = adCode.getText().toString();

                adCode.setText(code);

                if (code.isEmpty() || code.length() < 6){
                    if (vdialog.isShowing()){
                        vdialog.dismiss();
                    }
                    adCode.setError("Enter Valid Code");
                    adCode.requestFocus();
                }else {
                    if (vdialog.isShowing()){
                        vdialog.dismiss();
                    }
                    verifyCode(code);
                }
            }
        });
    }

    public void bt_continue_name(View view) {

        if (Common.isConnectedToInternet(getBaseContext())){

            if (validateName()){

                out(name_parent);
                in(email_parent);
                status1.setVisibility(View.VISIBLE);
                final_name = tname.getEditText().getText().toString();

            } else {

                snackbar.setText("Enter Valid Details");
                snackbar.show();

            }

        } else {

            snackbar.setText("No Internet Connection");
            snackbar.show();

        }

    }

    public void bt_back_name(View view) {
        // Disabled
    }

    public void bt_continue_email(View view) {

        if (Common.isConnectedToInternet(getBaseContext())){

            if (validateEmail()){

                out(email_parent);
                in(pass_parent);
                status2.setVisibility(View.VISIBLE);
                final_email = temail.getEditText().getText().toString();

            }else {
                snackbar.setText("Enter Valid Details");
                snackbar.show();
            }

        }else {
            snackbar.setText("No Internet Connection");
            snackbar.show();
        }
    }

    public void bt_back_email(View view) {

        back_out(email_parent);
        back_in(name_parent);
        status2.setVisibility(View.INVISIBLE);

    }

    public void bt_continue_pass(View view) {

        if (Common.isConnectedToInternet(getBaseContext())){

            String pass = tpass.getEditText().getText().toString();

            if (!pass.isEmpty()){

                if (pass.length()<=5){

                    snackbar.setText("Password cant be less than 5 letter");
                    snackbar.show();

                }else {

                    final_pass = tpass.getEditText().getText().toString();
                    out(pass_parent);
                    in(pno_parent);
                    status3.setVisibility(View.VISIBLE);

                }
            }else {
                snackbar.setText("Enter Valid Details");
                snackbar.show();
            }

        }else {
            snackbar.setText("No Internet Connection");
            snackbar.show();
        }

    }

    public void bt_back_pass(View view) {

        back_out(pass_parent);
        back_in(email_parent);
        status3.setVisibility(View.INVISIBLE);

    }

    public void bt_continue_pno(View view) {

        if (Common.isConnectedToInternet(getBaseContext())){

            if (validatePhone()){

                dialog.show();

                final_pno = tphone.getEditText().getText().toString();

                databse_user.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.child(final_pno).exists()){

                            if (dialog.isShowing()){
                                dialog.dismiss();
                            }
                            snackbar.setText("Phone Number is Already Registered");
                            snackbar.show();
                            phoneFlag = 1;
                        } else {

                            phoneFlag = 0;
                            status4.setVisibility(View.VISIBLE);
                            bt_signup_user();

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }else {
                snackbar.setText("Enter Valid Details");
                snackbar.show();
            }

        }else {
            snackbar.setText("No Internet Connection");
            snackbar.show();
        }

    }

    public void bt_back_pno(View view) {

        back_out(pno_parent);
        back_in(pass_parent);
        status4.setVisibility(View.INVISIBLE);

    }

    private void out(LinearLayout l1) {

        l1.setAnimation(slideout);
        l1.setVisibility(View.GONE);

    }

    private void in(LinearLayout l1){

        l1.setAnimation(slidein);
        l1.setVisibility(View.VISIBLE);
        status1.setVisibility(View.VISIBLE);
    }

    private void back_out(LinearLayout l1){

        l1.setAnimation(back_slideout);
        l1.setVisibility(View.GONE);

    }

    private void back_in(LinearLayout l1){

        l1.setAnimation(back_slidein);
        l1.setVisibility(View.VISIBLE);

    }

    //Name Validation
    boolean validateName(){
        String username = tname.getEditText().getText().toString();
        if (username.isEmpty()){
            tname.setError("Field Can't be Empty");
            tname.requestFocus();
            return false;
        }else if (username.length() > 20) {
            tname.setError("Name too long");
            tname.requestFocus();
            return false;
        }else {
            tname.setError(null);
            tname.clearFocus();
            hideKeyboard(tname);
            return true;
        }
    }

    //Email Validation
    boolean validateEmail(){
        String email = temail.getEditText().getText().toString().trim();

        if (email.isEmpty()){
            temail.setError("Field Can't be Empty");
            temail.requestFocus();
            return false;
        }else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            temail.setError("Please Enter a Valid E-Mail");
            temail.requestFocus();
            return false;
        }else {
            temail.setError(null);
            hideKeyboard(temail);
            temail.clearFocus();
            return true;
        }
    }

    //Phone Validation
    boolean validatePhone(){
        String pno = tphone.getEditText().getText().toString();

        if (pno.isEmpty() || pno.length() < 10 || pno.length() > 10){
            tphone.setError("Enter Valid Number");
            tphone.requestFocus();
            return false;
        }else {
            tphone.setError(null);
            hideKeyboard(tphone);
            tphone.clearFocus();
            return true;
        }
    }

    public void toast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.activity_close_enter, R.anim.activity_close_exit );
    }

    public void hideKeyboard(TextInputLayout t1){
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(t1.getWindowToken(), 0);
    }
}
