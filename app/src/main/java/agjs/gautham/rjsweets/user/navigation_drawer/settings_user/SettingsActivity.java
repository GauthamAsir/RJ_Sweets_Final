package agjs.gautham.rjsweets.user.navigation_drawer.settings_user;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.mukesh.OnOtpCompletionListener;
import com.mukesh.OtpView;

import java.util.concurrent.TimeUnit;

import agjs.gautham.rjsweets.Model.User;
import agjs.gautham.rjsweets.R;
import agjs.gautham.rjsweets.common.Common;
import agjs.gautham.rjsweets.common.Credits;
import agjs.gautham.rjsweets.common.UpdateActivity;
import agjs.gautham.rjsweets.login.Login;
import agjs.gautham.rjsweets.user.FeedbackActivity;
import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();

    }

    public static class SettingsFragment extends PreferenceFragmentCompat{

        private FirebaseDatabase database;
        private DatabaseReference databse_user, database_update;
        private FirebaseAuth mAuth;
        private FirebaseUser mUser;
        private Preference change_pass, check_update, contact_us, logOut, contributor, feedback;
        private SwitchPreferenceCompat noti;
        private FirebaseFirestore firestore;
        private OtpView otpView;
        private android.app.AlertDialog dlg;
        private android.app.AlertDialog dialog, dialog2, dialog3;
        private View enter_otp;
        private String verificationId;
        private String mail="";
        private android.app.AlertDialog pgDialog;
        private ProgressBar pg;


        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.settings_preferences, rootKey);

            //Init Paper.db
            Paper.init(getActivity());

            String isSubscribe = Paper.book().read("sub_new");

            final EditTextPreference name = findPreference(getString(R.string.name));
            name.setText(Common.Name);
            name.setDefaultValue(Common.Name);

            dlg = new SpotsDialog.Builder()
                    .setContext(getActivity())
                    .setCancelable(true)
                    .setMessage("Please wait ...")
                    .setTheme(R.style.DialogCustom)
                    .build();

            View progressBar = LayoutInflater.from(getActivity()).inflate(R.layout.progress_bar, null);
            pg = progressBar.findViewById(R.id.progressBar);

            android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(getActivity());
            alertDialog.setTitle("Downloading...");
            alertDialog.setIcon(R.drawable.ic_phone_iphone);
            alertDialog.setView(progressBar);
            alertDialog.setCancelable(true);

            pgDialog = alertDialog.create();
            pgDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));

            //Init Firebase
            database = FirebaseDatabase.getInstance();
            databse_user = database.getReference("User");
            database_update = database.getReference("Updates");
            mAuth = FirebaseAuth.getInstance();
            mUser = mAuth.getCurrentUser();
            firestore = FirebaseFirestore.getInstance();

            enter_otp = LayoutInflater.from(getActivity()).inflate(R.layout.enter_code_layout,null);
            otpView = enter_otp.findViewById(R.id.otp_view);

            noti = findPreference(getString(R.string.notifications));
            change_pass = findPreference(getString(R.string.change_password));
            check_update = findPreference(getString(R.string.check_for_updates));
            contact_us = findPreference(getString(R.string.contact_us));
            logOut = findPreference(getString(R.string.logout));
            contributor = findPreference(getString(R.string.contributors));
            feedback = findPreference(getString(R.string.feedback));

            if (mUser==null){

                name.setVisible(false);
                noti.setVisible(false);
                change_pass.setVisible(false);
                feedback.setVisible(false);
                Toast.makeText(getActivity(),"Login to view all features",Toast.LENGTH_SHORT).show();

            }

            contributor.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    startActivity(new Intent(getActivity(), Credits.class));
                    return true;
                }
            });

            feedback.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {

                    if (mUser!=null){

                        startActivity(new Intent(getActivity(), FeedbackActivity.class));

                    } else {

                        Toast.makeText(getActivity(),"Please Login to Continue",Toast.LENGTH_SHORT).show();

                    }
                    return true;
                }
            });

            name.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    if (!newValue.toString().isEmpty()){

                        if (!newValue.equals(Common.Name)) {

                            Common.Name = newValue.toString();
                            databse_user.child(Common.USER_Phone).child("name").setValue(newValue);
                            Toast.makeText(getActivity(),"Successfully Changed Name",Toast.LENGTH_SHORT).show();

                        }
                    }
                    return true;
                }
            });

            if (mUser==null){
                logOut.setTitle(getString(R.string.login));
            }

            logOut.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {

                    if (mUser!=null){
                        logout();
                    }
                    else{
                        startActivity(new Intent(getActivity(), Login.class));
                        getActivity().finish();
                    }
                    return true;
                }
            });

            contact_us.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    contactUs();
                    return true;
                }
            });

            if (isSubscribe == null || TextUtils.isEmpty(isSubscribe) || isSubscribe.equals("false")){
                noti.setChecked(false);
                FirebaseMessaging.getInstance().unsubscribeFromTopic(Common.topicName);
                Paper.book().write("sub_new","false");

            }
            else{
                noti.setChecked(true);
                FirebaseMessaging.getInstance().subscribeToTopic(Common.topicName);
                Paper.book().write("sub_new","true");

            }

            change_pass.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    VerifyPhoneDialog();
                    return true;
                }
            });

            check_update.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {

                    startActivity(new Intent(getActivity(), UpdateActivity.class));
                    Common.intentOpenAnimation(getActivity());
                    return true;
                }
            });

        }

        private void VerifyPhoneDialog() {

            final View exp = LayoutInflater.from(getActivity()).inflate(R.layout.forgot_pass_enter_phone,null);

            android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(getActivity());
            alertDialog.setTitle("Reset Password !");
            alertDialog.setMessage("We will Send a verification Code to your Number");
            alertDialog.setIcon(R.drawable.ic_local_phone_black_24dp);
            alertDialog.setView(exp);
            alertDialog.setCancelable(true);

            final TextInputLayout passResetPhone = exp.findViewById(R.id.PassResetPhone);

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

            dialog = alertDialog.create();
            dialog.show();

            dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    final String phoneNumber = passResetPhone.getEditText().getText().toString();

                    if (validatePhone(phoneNumber, passResetPhone)){

                        hideKeyboard(passResetPhone);
                        databse_user.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.child(phoneNumber).exists()){

                                    User user = dataSnapshot.child(phoneNumber).getValue(User.class);
                                    mail = mUser.getEmail();

                                    if (user.getEmail().equals(mail)){

                                        String pno = "+" + "91" + phoneNumber;
                                        enterCode();
                                        dialog.dismiss();
                                        sendVerificationCode(pno);

                                    } else {
                                        Toast.makeText(getActivity(),"Phone Number not Linked with this Account",Toast.LENGTH_SHORT).show();
                                    }

                                }else {
                                    Toast.makeText(getActivity(),"User Phone Number Doesn't Match",Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    } else {
                        if (dlg.isShowing()){
                            dlg.dismiss();
                        }
                    }

                }
            });

        }

        private void enterCode() {

            android.app.AlertDialog.Builder alertDialog3 = new android.app.AlertDialog.Builder(getActivity());
            alertDialog3.setTitle("Otp Verification !");
            alertDialog3.setMessage("Enter the Otp Sent to your Number");
            alertDialog3.setIcon(R.drawable.ic_local_phone_black_24dp);
            alertDialog3.setView(enter_otp);
            alertDialog3.setCancelable(true);


            otpView.requestFocus();

            otpView.setOtpCompletionListener(new OnOtpCompletionListener() {
                @Override
                public void onOtpCompleted(String otp) {

                    if (otp.isEmpty()){
                        otpView.setError("Field Cant be Empty");
                    }else{
                        verifyCode(otp);
                    }
                }
            });

            dialog3 = alertDialog3.create();
            dialog3.show();

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
            dlg.show();
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
            signinwithCredential(credential);
        }

        private void signinwithCredential(PhoneAuthCredential credential){
            mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){

                        if (dlg.isShowing()){
                            dlg.dismiss();
                        }
                        if (dialog.isShowing()){
                            dialog.dismiss();
                        }
                        if (dialog3.isShowing()){
                            dialog3.dismiss();
                        }

                        final View exp2 = LayoutInflater.from(getActivity()).inflate(R.layout.pasword_reset,null);

                        final android.app.AlertDialog.Builder alertDialog2 = new android.app.AlertDialog.Builder(getActivity());
                        alertDialog2.setTitle("Reset Password !");
                        alertDialog2.setMessage("We will Send an E-Mail To Reset your Password");
                        alertDialog2.setIcon(R.drawable.ic_email_black_24dp);
                        alertDialog2.setView(exp2);
                        alertDialog2.setCancelable(false);

                        final TextInputLayout reset_email = exp2.findViewById(R.id.PassResetEmail);

                        alertDialog2.setPositiveButton("Send", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                        alertDialog2.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });

                        dialog2 = alertDialog2.create();
                        dialog2.show();

                        dialog2.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                hideKeyboard(reset_email);
                                dlg.show();

                                String inputEmail = reset_email.getEditText().getText().toString();

                                if (mail.equals(inputEmail)){

                                    if (validateOnlyEmail(inputEmail, reset_email)){

                                        FirebaseAuth.getInstance().sendPasswordResetEmail(inputEmail)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {

                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()){

                                                            Toast.makeText(getActivity(),"Please Check your E-mail",Toast.LENGTH_SHORT).show();

                                                            if (dlg.isShowing()){
                                                                dlg.dismiss();
                                                            }
                                                            if (dialog2.isShowing()){
                                                                dialog2.dismiss();
                                                            }
                                                        }else {
                                                            Toast.makeText(getActivity(),"Oops Something went wrong... Please Try Again Later",Toast.LENGTH_LONG).show();
                                                            if (dlg.isShowing()){
                                                                dlg.dismiss();
                                                            }
                                                            if (dialog2.isShowing()){
                                                                dialog2.dismiss();
                                                            }
                                                        }
                                                    }
                                                });

                                        //Snackbar
                                        Snackbar snackbar = Snackbar.make(getView(), "Press Again to Exit", Snackbar.LENGTH_LONG);
                                        View snackView = snackbar.getView();
                                        TextView tv = snackView.findViewById(com.google.android.material.R.id.snackbar_text);
                                        tv.setTextColor(Color.WHITE);

                                        snackbar.setText("Note: Please Login with E-Mail after changing Password");
                                        snackbar.show();

                                    }else {
                                        if (dlg.isShowing()){
                                            dlg.dismiss();
                                        }
                                    }
                                }else {
                                    if (dlg.isShowing()){
                                        dlg.dismiss();
                                    }
                                    Toast.makeText(getActivity(),"Your Entered Phone Number isn't associated with your Email, Please Enter Your Registered Phone Number",Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                        Toast.makeText(getActivity(),"Verification Success",Toast.LENGTH_SHORT).show();
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
                    otpView.setText(code);
                    verifyCode(code);
                }
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                if (dlg.isShowing()){
                    dlg.dismiss();
                    Toast.makeText(getActivity(),"Wrong Otp",Toast.LENGTH_SHORT).show();
                }
            }
        };

        private void contactUs() {

            ImageView contact_telegram, contact_email;

            final View contact_us = LayoutInflater.from(getActivity()).inflate(R.layout.contact_us_dialog,null);
            android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(getActivity());
            alertDialog.setView(contact_us);

            contact_telegram = contact_us.findViewById(R.id.contact_telegram);
            contact_email = contact_us.findViewById(R.id.contact_email);

            contact_telegram.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent sendtelegram = new Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/joinchat/FxG96hOl_0xvg0cV8p9aPA"));
                    startActivity(sendtelegram);
                }
            });

            contact_email.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (mUser != null){

                        String email = mUser.getEmail();

                        Intent sendEmail = new Intent(Intent.ACTION_SENDTO);
                        sendEmail.setData(Uri.parse("mailto:agjs.bros@gmail.com"));
                        sendEmail.putExtra(Intent.EXTRA_SUBJECT,"Support");
                        startActivity(sendEmail);
                    }else {
                        Toast.makeText(getActivity(),"You Need to Login",Toast.LENGTH_SHORT).show();
                    }
                }
            });

            final android.app.AlertDialog dialog2 = alertDialog.create();
            dialog2.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog2.show();

        }

        private void logout() {

            final android.app.AlertDialog dialog;

            dialog = new SpotsDialog.Builder()
                    .setContext(getActivity())
                    .setCancelable(false)
                    .setMessage("Logging You Out !...")
                    .setTheme(R.style.DialogCustom)
                    .build();

            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Log Out !");
            builder.setMessage("Do you really want to Log Out ?");
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    dialog.show();
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            if (dialog.isShowing()){
                                dialog.dismiss();
                            }
                            mAuth.signOut();

                            Paper.book().destroy();
                            Intent logout = new Intent(getActivity(), Login.class);
                            logout.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(logout);
                            Common.intentOpenAnimation(getActivity());
                            getActivity().finish();
                        }
                    }, 3000);
                }
            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    //Return To Resumed Fragments
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getActivity().getApplicationContext().getColor(R.color.colorText));
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getActivity().getApplicationContext().getColor(R.color.colorText));

        }

        boolean validateOnlyEmail(String email, TextInputLayout reset_email){

            if (email.isEmpty()){
                reset_email.setError("Field Can't be Empty");
                reset_email.requestFocus();
                return false;
            }else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                reset_email.setError("Please Enter a Valid E-Mail");
                reset_email.requestFocus();
                return false;
            }else {
                reset_email.setError(null);
                reset_email.clearFocus();
                return true;
            }
        }

        boolean validatePhone(String phoneNumber, TextInputLayout passResetPhone) {

            if (phoneNumber.isEmpty()) {
                passResetPhone.setError("Field Can't be Empty");
                passResetPhone.requestFocus();
                return false;
            }else if (phoneNumber.length() > 10 | phoneNumber.length() < 10){
                passResetPhone.setError("Invalid Phone Number !");
                passResetPhone.requestFocus();
                return false;
            } else {
                passResetPhone.setError(null);
                passResetPhone.clearFocus();
                return true;
            }

        }

        void hideKeyboard(TextInputLayout passResetPhone){
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(passResetPhone.getWindowToken(), 0);
        }
    }
}