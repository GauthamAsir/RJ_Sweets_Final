package agjs.gautham.rjsweets.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import agjs.gautham.rjsweets.Common;
import agjs.gautham.rjsweets.Model.UserDelivery;
import agjs.gautham.rjsweets.R;
import agjs.gautham.rjsweets.delivery.DashboardDelivery;
import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;

public class DeliveryLogin extends AppCompatActivity {

    private TextInputLayout sPhone, sPass;
    private CheckBox checkBox;
    android.app.AlertDialog dialog;

    DatabaseReference databaseReference;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_login);

        sPhone = findViewById(R.id.signInPhoneDelivery);
        sPass = findViewById(R.id.signInPassDelivery);
        FloatingActionButton login = findViewById(R.id.login_delivery_bt);
        checkBox = findViewById(R.id.rememberDelivery);

        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setCancelable(false)
                .setMessage("Please wait ...")
                .build();

        TextView headline1 = findViewById(R.id.headline1_delivery);
        TextView headline2 = findViewById(R.id.headline2_delivery);

        Typeface typeface = Typeface.createFromAsset(getAssets(),"fonts/NABILA.TTF");

        headline1.setTypeface(typeface);
        headline2.setTypeface(typeface);

        //Init Paper to Remember
        Paper.init(this);

        //Init Firebase
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("Shippers");

    }

    public void bt_signIn_phone_delivery(final View view){

        final String localPhone = sPhone.getEditText().getText().toString();
        final String localPass = sPass.getEditText().getText().toString();

        //Hide Keyboard Button onClick
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        if (imm != null) {
            imm.hideSoftInputFromWindow(sPass.getWindowToken(), 0);
        }

        if (validatePass() & validatePhone()){

            dialog.show();

            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    //Check user existence
                    if (dataSnapshot.child(localPhone).exists()) {

                        dialog.dismiss();
                        UserDelivery user = dataSnapshot.child(localPhone).getValue(UserDelivery.class);

                        //Check Password
                        if (user.getPassword().equals(localPass)) {

                            if (checkBox.isChecked()){
                                Paper.book().destroy();
                                Paper.book().write(Common.loginType,"2");
                                Paper.book().write(Common.USER_EMAIL,localPhone);
                                Paper.book().write(Common.USER_PASS,localPass);
                            }

                            Intent intent = new Intent(DeliveryLogin.this, DashboardDelivery.class);
                            Common.USER_Phone = localPhone;
                            startActivity(intent);
                            finish();
                        } else {
                            Snackbar.make(view, "Wrong Password", Snackbar.LENGTH_LONG).show();
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
        startActivity(new Intent(DeliveryLogin.this,Login.class));
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
