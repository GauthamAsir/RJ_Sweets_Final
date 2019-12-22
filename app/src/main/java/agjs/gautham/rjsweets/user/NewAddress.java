package agjs.gautham.rjsweets.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import agjs.gautham.rjsweets.Common;
import agjs.gautham.rjsweets.R;
import io.paperdb.Paper;

public class NewAddress extends AppCompatActivity {

    EditText orderaddress_line1, orderaddress_line2, orderaddress_landark, orderaddress_city, orderaddress_state, orderaddress_pincode, orderComment;

    String addressLine1, addressLine2, addressLandmark, addressCity, addressState, addressPincode, comment;

    CheckBox addressCheckbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_address);

        orderaddress_line1 = findViewById(R.id.address_line1_user);
        orderaddress_line2 = findViewById(R.id.address_line2_user);
        orderaddress_landark = findViewById(R.id.address_landmark_1_user);
        orderaddress_city = findViewById(R.id.address_city1_user);
        orderaddress_state = findViewById(R.id.address_state1_user);
        orderaddress_pincode = findViewById(R.id.address_pincode1_user);

        orderComment = findViewById(R.id.edtComment_user);

        addressCheckbox = findViewById(R.id.address_checkbox_user);

        Button place = findViewById(R.id.btnPlaceOrder_user);
        Button cancel = findViewById(R.id.btnCancelOrder_user);

        place.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bt_place_order();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    public void bt_place_order(){

        addressLine1 = orderaddress_line1.getText().toString();
        addressLine2 = orderaddress_line2.getText().toString();
        addressLandmark = orderaddress_landark.getText().toString();
        addressCity = orderaddress_city.getText().toString();
        addressState = orderaddress_state.getText().toString();
        addressPincode = orderaddress_pincode.getText().toString();

        comment = orderComment.getText().toString();

        if (validateLine1(addressLine1) && validateLine2(addressLine2) && validateLandmark(addressLandmark)
                && validatepincode(addressPincode)){

            String edAddress = addressLine1+", "+ addressLine2+", "
                    + addressLandmark+", "+ addressCity +", "+ addressState +"-"+ addressPincode;

            if (addressCheckbox.isChecked()){

                Paper.book().write(Common.USER_ADDRESS_LINE1,addressLine1);
                Paper.book().write(Common.USER_ADDRESS_LINE2,addressLine2);
                Paper.book().write(Common.USER_ADDRESS_LANDMARK,addressLandmark);
                Paper.book().write(Common.USER_ADDRESS_Pincode,addressPincode);
            }

            String price = getIntent().getStringExtra("Price");

            if (comment.isEmpty()){
                comment = "No Comments";

                Intent placeOrder = new Intent(NewAddress.this, PlaceOrder.class);
                placeOrder.putExtra("Address",edAddress);
                placeOrder.putExtra("Comments",comment);
                placeOrder.putExtra("Price",price);
                startActivity(placeOrder);
                finish();

            }else {

                Intent placeOrder = new Intent(NewAddress.this,PlaceOrder.class);
                placeOrder.putExtra("Address",edAddress);
                placeOrder.putExtra("Comments",comment);
                placeOrder.putExtra("Price",price);
                startActivity(placeOrder);
                finish();
            }
        }

    }

    @Override
    public void onBackPressed() {
        Toast.makeText(NewAddress.this, "Back is Not Allowed", Toast.LENGTH_LONG).show();
    }

    private boolean validatepincode(String addressPincode) {

        if (addressPincode.isEmpty()){
            orderaddress_pincode.setError("Field Can't be Empty");
            orderaddress_pincode.requestFocus();
            return false;
        }else if (addressPincode.length() > 6 || addressPincode.length() < 6 ){
            orderaddress_pincode.setError("Enter a valid Pincode");
            orderaddress_pincode.requestFocus();
            return false;
        }else {
            orderaddress_pincode.clearFocus();
            return true;
        }
    }

    private boolean validateLandmark(String addressLandmark) {

        if (addressLandmark.isEmpty()){
            orderaddress_landark.setError("Field Can't be Empty");
            orderaddress_landark.requestFocus();
            return false;
        }else {
            orderaddress_landark.clearFocus();
            return true;
        }

    }

    private boolean validateLine2(String addressLine2) {

        if (addressLine2.isEmpty()){
            orderaddress_line2.setError("Field Can't be Empty");
            orderaddress_line2.requestFocus();
            return false;
        }else {
            orderaddress_line2.clearFocus();
            return true;
        }

    }

    private boolean validateLine1(String addressLine1) {

        if (addressLine1.isEmpty()){
            orderaddress_line1.setError("Field Can't be Empty");
            orderaddress_line1.requestFocus();
            return false;
        }else {
            orderaddress_line1.clearFocus();
            return true;
        }

    }

}
