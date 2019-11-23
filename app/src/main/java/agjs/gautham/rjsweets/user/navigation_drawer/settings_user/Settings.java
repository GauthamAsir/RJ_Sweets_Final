package agjs.gautham.rjsweets.user.navigation_drawer.settings_user;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.amulyakhare.textdrawable.TextDrawable;
import com.github.javiersantos.appupdater.AppUpdater;
import com.github.javiersantos.appupdater.AppUpdaterUtils;
import com.github.javiersantos.appupdater.enums.AppUpdaterError;
import com.github.javiersantos.appupdater.enums.Display;
import com.github.javiersantos.appupdater.enums.UpdateFrom;
import com.github.javiersantos.appupdater.objects.Update;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.mukesh.OnOtpCompletionListener;
import com.mukesh.OtpView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import agjs.gautham.rjsweets.Common;
import agjs.gautham.rjsweets.Model.User;
import agjs.gautham.rjsweets.R;
import agjs.gautham.rjsweets.login.Login;
import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;

public class Settings extends Fragment {

    private ImageView imageView, contact_telegram, contact_email;
    private TextView textView;
    private android.app.AlertDialog dlg;
    private AlertDialog dialog, dialog2, dialog3;

    private String verificationId;
    private String mail="";

    private LinearLayout container;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    private View enter_otp;

    private OtpView otpView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.nav_settings_user, container, false);

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        enter_otp = LayoutInflater.from(getActivity()).inflate(R.layout.enter_code_layout,null);
        otpView = enter_otp.findViewById(R.id.otp_view);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("User");

        imageView = root.findViewById(R.id.settings_img_user);
        textView = root.findViewById(R.id.settings_txt_user);

        dlg = new SpotsDialog.Builder()
                .setContext(getActivity())
                .setCancelable(true)
                .setMessage("Please wait ...")
                .setTheme(R.style.DialogCustom)
                .build();

        Button contactUs = root.findViewById(R.id.bt_contactUs_user);

        Button app_update = root.findViewById(R.id.settings_app_updater_user);

        Button notifications = root.findViewById(R.id.notification_bt_user);

        CardView logout_container = root.findViewById(R.id.settings_logout_container_user);
        Button logout = root.findViewById(R.id.settings_logout_user);

        CardView login_container = root.findViewById(R.id.settings_login_container_user);
        Button login = root.findViewById(R.id.settings_login_user);

        CardView changePass_container = root.findViewById(R.id.settings_change_pass_container_user);
        Button changePass = root.findViewById(R.id.settings_change_pass_user);

        CardView editAddress_container = root.findViewById(R.id.settings_edit_address_container_user);
        final Button editAddress = root.findViewById(R.id.settings_edit_address_user);

        if (mUser != null){

            login_container.setVisibility(View.GONE);
            logout_container.setVisibility(View.VISIBLE);
            changePass_container.setVisibility(View.VISIBLE);
            editAddress_container.setVisibility(View.VISIBLE);

            changePass.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    changePassord();
                }
            });

            editAddress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    editAddressMethod();
                }
            });

            logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    logOut();
                }
            });

        }else {
            logout_container.setVisibility(View.GONE);
            login_container.setVisibility(View.VISIBLE);
            changePass_container.setVisibility(View.GONE);
            editAddress_container.setVisibility(View.GONE);

            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    logIn();
                }
            });

        }

        contactUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                contactUS();
            }
        });

        app_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                checkForUpdate();
            }
        });

        notifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notification();
            }
        });

        if (mUser != null){
            String email = mUser.getEmail();

            assert email != null;
            DocumentReference documentReference = firestore.collection("users").document(email);
            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()){
                        DocumentSnapshot documentSnapshot = task.getResult();
                        assert documentSnapshot != null;
                        if (documentSnapshot.exists()){
                            Map<String, Object> userInfo = documentSnapshot.getData();
                            assert userInfo != null;
                            String uName = (String) userInfo.get("Username");
                            char name = uName.charAt(0);
                            textView.setText(uName);
                            textDrawableBuilder(String.valueOf(name));
                        }
                    }
                }
            });
        }else {
            textDrawableBuilder("RJ");
            textView.setText(R.string.app_name);
        }

        return root;
    }

    private void checkForUpdate() {

        if (Common.isConnectedToInternet(getActivity())){

            AppUpdater appUpdater = new AppUpdater(getActivity())
                    .setUpdateFrom(UpdateFrom.XML)
                    .setUpdateXML("https://raw.githubusercontent.com/GauthamAsir/RJ_Sweets_Releases/master/update.xml")
                    .setDisplay(Display.DIALOG)
                    .setButtonDoNotShowAgain(null)
                    .showAppUpdated(true)
                    .setButtonUpdateClickListener(new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (ContextCompat.checkSelfPermission(getActivity(),
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                    == PackageManager.PERMISSION_GRANTED){
                                downloadUpdateApk();
                            }else {
                                ActivityCompat.requestPermissions(getActivity(),new String[]{
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE
                                },1);
                            }
                        }
                    });

            AppUpdater updater = new AppUpdater(getActivity())
                    .setUpdateFrom(UpdateFrom.XML)
                    .setUpdateXML("https://raw.githubusercontent.com/GauthamAsir/RJ_Sweets_Releases/master/update.xml")
                    .setDisplay(Display.NOTIFICATION);

            updater.start();

            updater.setButtonUpdateClickListener(new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    downloadUpdateApk();
                }
            });

            appUpdater.start();

        }else {
            Toast.makeText(getActivity(), "No Internet", Toast.LENGTH_SHORT).show();
        }

    }

    private void downloadUpdateApk() {

        AppUpdaterUtils appUpdaterUtils = new AppUpdaterUtils(getActivity())
                .setUpdateFrom(UpdateFrom.XML)
                .setUpdateXML("https://raw.githubusercontent.com/GauthamAsir/RJ_Sweets_Releases/master/update.xml")
                .withListener(new AppUpdaterUtils.UpdateListener() {
                    @Override
                    public void onSuccess(Update update, Boolean isUpdateAvailable) {

                        String updateUrl = String.valueOf(update.getUrlToDownload());
                        Date dt = Calendar.getInstance().getTime();
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                        final String orderDate = dateFormat.format(dt);
                        String updateAppName = "rj_sweets_" + orderDate + update.getLatestVersion() + ".apk";

                        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(updateUrl));
                        request.setTitle("RJ Sweets App Updater");
                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                        request.setDestinationInExternalPublicDir("/RJ Sweets/update",updateAppName);
                        final DownloadManager manager = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
                        manager.enqueue(request);

                        Toast.makeText(getActivity(),"Downloading Update",Toast.LENGTH_SHORT).show();

                        if (DownloadManager.ACTION_NOTIFICATION_CLICKED.isEmpty()){
                            Intent promptInstall = new Intent(Intent.ACTION_VIEW)
                                    .setDataAndType(Uri.parse("content:///Rj Sweets/"+updateAppName),
                                            "application/vnd.android.package-archive");
                            startActivity(promptInstall);
                        }
                    }

                    @Override
                    public void onFailed(AppUpdaterError error) {

                    }
                });

        appUpdaterUtils.start();

    }

    private void editAddressMethod() {



    }

    private void changePassord() {
        VerifyPhoneDialog();
    }

    private void VerifyPhoneDialog() {

        final View exp = LayoutInflater.from(getActivity()).inflate(R.layout.forgot_pass_enter_phone,null);

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
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

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                final String phoneNumber = passResetPhone.getEditText().getText().toString();

                if (validatePhone(phoneNumber, passResetPhone)){

                    hideKeyboard(passResetPhone);
                    databaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.child(phoneNumber).exists()){

                                User user = dataSnapshot.child(phoneNumber).getValue(User.class);
                                mail = user.getEmail();

                                String pno = "+" + "91" + phoneNumber;
                                enterCode();
                                dialog.dismiss();
                                sendVerificationCode(pno);
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

        AlertDialog.Builder alertDialog3 = new AlertDialog.Builder(getActivity());
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

                    final AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(getActivity());
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

                    dialog2.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            hideKeyboard(reset_email);
                            dlg.show();

                            container = getActivity().findViewById(R.id.settings_container_user);

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

                                    Snackbar.make(container,"Note: Please Login with E-Mail after changing Password",Snackbar.LENGTH_LONG).show();
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

    private void contactUS() {

        final View contact_us = LayoutInflater.from(getActivity()).inflate(R.layout.contact_us_dialog,null);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setView(contact_us);

        contact_telegram = contact_us.findViewById(R.id.contact_telegram);
        contact_email = contact_us.findViewById(R.id.contact_email);

        contact_telegram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendtelegram = new Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/joinchat/FxG96hOl_0z5dYOUYsy4Dg"));
                startActivity(sendtelegram);
            }
        });

        contact_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mUser != null){

                    String email = mUser.getEmail();

                    Intent sendEmail = new Intent(Intent.ACTION_SENDTO);
                    sendEmail.setData(Uri.parse("mailto:gauthamnadar12@gmail.com"));
                    sendEmail.putExtra(Intent.EXTRA_SUBJECT,"Support");
                    startActivity(sendEmail);
                }else {
                    Toast.makeText(getActivity(),"You Need to Login",Toast.LENGTH_SHORT).show();
                }
            }
        });

        final AlertDialog dialog2 = alertDialog.create();
        dialog2.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog2.show();

    }

    private void notification() {

        final View notifi = LayoutInflater.from(getActivity()).inflate(R.layout.notification_settings,null);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setView(notifi);
        alertDialog.setTitle("Notification Settings");

        final CheckBox notify_checkbox = notifi.findViewById(R.id.checkbox_notification);

        Paper.init(getActivity());
        String isSubscribe = Paper.book().read("sub_new");
        if (isSubscribe == null || TextUtils.isEmpty(isSubscribe) || isSubscribe.equals("false"))
            notify_checkbox.setChecked(false);

        else
            notify_checkbox.setChecked(true);

        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (notify_checkbox.isChecked()){
                    FirebaseMessaging.getInstance().subscribeToTopic(Common.topicName);

                    Paper.book().write("sub_new","true");
                }else {
                    FirebaseMessaging.getInstance().unsubscribeFromTopic(Common.topicName);

                    Paper.book().write("sub_new","false");
                }
            }
        });

        final AlertDialog dialog2 = alertDialog.create();
        dialog2.show();

    }

    private void logIn() {

        startActivity(new Intent(getActivity(), Login.class));
        getActivity().finish();
        getActivity().overridePendingTransition(R.anim.activity_close_enter, R.anim.activity_close_exit);

    }

    private void logOut() {

        final androidx.appcompat.app.AlertDialog.Builder builder = new
                androidx.appcompat.app.AlertDialog.Builder(getActivity());
        builder.setTitle("Log Out !");
        builder.setMessage("Do you really want to Log Out ?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                dlg.show();

                new Handler().postDelayed(new Runnable() {
                    public void run() {

                        mAuth.signOut();

                        Paper.book().destroy();
                        Intent logout = new Intent(getActivity(), Login.class);
                        logout.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(logout);
                        dlg.dismiss();
                        getActivity().finish();
                        getActivity().overridePendingTransition(R.anim.activity_close_enter, R.anim.activity_close_exit);
                    }
                }, 3000);
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        androidx.appcompat.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    private void textDrawableBuilder(String txtName) {

        //Random Color Generator
        Random rand = new Random();
        int r = rand.nextInt(255);
        int g = rand.nextInt(255);
        int b = rand.nextInt(255);
        final int randomColor = Color.rgb(r,g,b);

        TextDrawable drawable = TextDrawable.builder()
                .beginConfig()
                .useFont(Typeface.SANS_SERIF)
                .fontSize(130)
                .bold()
                .endConfig()
                .buildRound(String.valueOf(txtName),randomColor);

        imageView.setImageDrawable(drawable);

    }
}