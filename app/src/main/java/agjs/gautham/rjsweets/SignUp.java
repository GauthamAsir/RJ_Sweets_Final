package agjs.gautham.rjsweets;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
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

    private TextInputLayout tname, temail, tpass, trepass, tphone;
    private String pass2;
    private String verificationId;

    FirebaseFirestore firestore;
    private FirebaseAuth mAuth;

    private int phoneFlag = 0;

    android.app.AlertDialog dialog, vdialog;

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

        tname = findViewById(R.id.signUpName);
        tpass = findViewById(R.id.signUpPass);
        trepass = findViewById(R.id.signUpRePass);
        temail = findViewById(R.id.signUpEmail);
        tphone = findViewById(R.id.signUpPhone);

        mAuth = FirebaseAuth.getInstance();

    }

    public void bt_signup_user(View view){

        if (Common.isConnectedToInternet(getBaseContext())){

            if (!validateName() | !validatePass() | !validateEmail() | !validatePhone()) {
                return;
            }

            pass2 = trepass.getEditText().getText().toString();
            final String spno = tphone.getEditText().getText().toString();

            dialog.show();

            //Init Firebase
            final FirebaseDatabase database = FirebaseDatabase.getInstance();
            final DatabaseReference databse_user = database.getReference("User");

            databse_user.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.child(spno).exists()){

                        if (dialog.isShowing()){
                            dialog.dismiss();
                        }
                        toast("Phone Number is Already Registered");
                        phoneFlag = 1;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            if (phoneFlag == 0){

                String phoneNumber = "+" + "91" + spno;
                sendVerificationCode(phoneNumber);
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

                            final String semail = temail.getEditText().getText().toString();
                            final String spass1 = tpass.getEditText().getText().toString();
                            final String susername = tname.getEditText().getText().toString();
                            final String spno = tphone.getEditText().getText().toString();

                            final FirebaseDatabase database = FirebaseDatabase.getInstance();
                            final DatabaseReference databse_user = database.getReference("User");

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
                                                    startActivity(new Intent(SignUp.this, DashboardUser.class));
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

    // Password Validation
    boolean validatePass(){
        String pass1 = tpass.getEditText().getText().toString();
        pass2 = trepass.getEditText().getText().toString();

        if (pass1.isEmpty()){
            tpass.setError("Field Can't be Empty");
            trepass.requestFocus();
            return false;
        }else if (pass2.isEmpty()){
            trepass.setError("Field Can't be Empty");
            trepass.requestFocus();
            return false;
        }else if (!pass1.equals(pass2)){
            trepass.setError("Password Miss-Match");
            trepass.requestFocus();
            return false;
        }else {
            trepass.setError(null);
            hideKeyboard(trepass);
            hideKeyboard(tpass);
            tpass.clearFocus();
            trepass.clearFocus();
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
