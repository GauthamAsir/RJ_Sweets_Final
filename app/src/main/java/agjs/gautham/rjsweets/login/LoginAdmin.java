package agjs.gautham.rjsweets.login;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import agjs.gautham.rjsweets.Common;
import agjs.gautham.rjsweets.Model.User;
import agjs.gautham.rjsweets.R;
import agjs.gautham.rjsweets.admin.DashboardAdmin;
import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;

public class LoginAdmin extends AppCompatActivity {

    private TextInputLayout sPhone, sPass;
    private CheckBox checkBox;

    android.app.AlertDialog dialog;

    DatabaseReference databaseReference;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_admin);

        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setCancelable(false)
                .setMessage("Please wait ...")
                .setTheme(R.style.DialogCustom)
                .build();

        TextView headline1 = findViewById(R.id.headline1_admin);
        TextView headline2 = findViewById(R.id.headline2_admin);

        Typeface typeface = Typeface.createFromAsset(getAssets(),"fonts/NABILA.TTF");

        headline1.setTypeface(typeface);
        headline2.setTypeface(typeface);

        sPhone = findViewById(R.id.signInPhoneAdmin);
        sPass = findViewById(R.id.signInPass);
        checkBox = findViewById(R.id.remember);

        //Init Paper to Remember
        Paper.init(this);

        //Init Firebase
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("User");

    }

    public void bt_signIn_phone_admin(final View view){

        final String localPhone = sPhone.getEditText().getText().toString();
        final String localPass = sPass.getEditText().getText().toString();

        //Hide Keyboard Button onClick
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        if (imm != null) {
            imm.hideSoftInputFromWindow(sPass.getWindowToken(), 0);
        }

        if (validatePhone() && validatePass()){

            dialog.show();

            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    //Check user existence
                    if (dataSnapshot.child(localPhone).exists()) {

                        dialog.dismiss();
                        User user = dataSnapshot.child(localPhone).getValue(User.class);
                        if (Boolean.parseBoolean(user.getIsStaff())) {

                            //Check Password
                            if (user.getPassword().equals(localPass)) {

                                if (checkBox.isChecked()) {
                                    Paper.book().destroy();
                                    Paper.book().write(Common.loginType,"1");
                                    Paper.book().write(Common.USER_EMAIL, localPhone);
                                    Paper.book().write(Common.USER_PASS, localPass);
                                }

                                Common.loginType = "1";
                                Common.USER_Phone = localPhone;
                                Common.Name = user.getName();
                                Intent intent = new Intent(LoginAdmin.this, DashboardAdmin.class);
                                startActivity(intent);
                                finish();

                            } else {
                                Snackbar.make(view, "Wrong Password", Snackbar.LENGTH_LONG).show();
                            }
                        } else {
                            Snackbar.make(view, "Login with Staff Account",
                                    Snackbar.LENGTH_LONG).show();
                        }
                    } else {
                        dialog.dismiss();
                        Snackbar.make(view, "User Doesn't Exists in Database", Snackbar.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }else {

            Snackbar.make(view, "Please Enter Valid Data", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(LoginAdmin.this,Login.class));
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

    //Password Validation
    boolean validatePhone() {
        String phone = sPhone.getEditText().getText().toString();
        if (phone.isEmpty()) {
            sPhone.setError("Field Can't be Empty");
            sPhone.requestFocus();
            return false;
        }else if (phone.length() > 10 | phone.length() < 10){
            sPhone.setError("Invalid Phone Number !");
            sPhone.requestFocus();
            return false;
        } else {
            sPhone.setError(null);
            sPhone.clearFocus();
            return true;
        }
    }

}
