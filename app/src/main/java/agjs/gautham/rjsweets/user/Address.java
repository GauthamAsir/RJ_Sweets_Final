package agjs.gautham.rjsweets.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import agjs.gautham.rjsweets.Common;
import agjs.gautham.rjsweets.Model.SavedAddress;
import agjs.gautham.rjsweets.R;
import io.paperdb.Paper;

public class Address extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        Button addNewAddress = findViewById(R.id.addNewAddress);
        CardView savedAddressContainer = findViewById(R.id.savedAddressContainer_user);

        TextView user_name = findViewById(R.id.user_name_user);

        TextView orderaddress_line1 = findViewById(R.id.address_line1_details_user);
        TextView orderaddress_line2 = findViewById(R.id.address_line2_details_user);
        TextView orderaddress_landark = findViewById(R.id.address_landmark_details_user);
        TextView orderaddress_city = findViewById(R.id.address_city_details_user);
        TextView orderaddress_state = findViewById(R.id.address_state_details_user);

        TextInputEditText edComment = findViewById(R.id.edtComment_user);

        final String savedAddressLine1 = Paper.book().read(Common.USER_ADDRESS_LINE1);
        final String savedAddressLine2 = Paper.book().read(Common.USER_ADDRESS_LINE2);
        final String savedAddressLandmark = Paper.book().read(Common.USER_ADDRESS_LANDMARK);
        final String savedAddressPincode = Paper.book().read(Common.USER_ADDRESS_Pincode);

        if (savedAddressLine1 != null && savedAddressLine2 != null && savedAddressLandmark != null
                && savedAddressPincode != null) {
            if (!savedAddressLine1.isEmpty() && !savedAddressLine2.isEmpty() && !savedAddressLandmark.isEmpty()
                    && !savedAddressPincode.isEmpty()){

                savedAddressContainer.setVisibility(View.VISIBLE);
                addNewAddress.setVisibility(View.VISIBLE);

                user_name.setText(String.format("%s,",Common.Name));
                orderaddress_line1.setText(String.format("%s,",savedAddressLine1));
                orderaddress_line2.setText(String.format("%s,",savedAddressLine2));
                orderaddress_landark.setText(String.format("%s,",savedAddressLandmark));
                orderaddress_city.setText("Mumbai,");
                orderaddress_state.setText(String.format("Maharashtra-%s",savedAddressPincode));

                savedAddressContainer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String edAddress = savedAddressLine1+", "+ savedAddressLine2+", "
                                + savedAddressLandmark+", "+ "Mumbai" +", "+ "Maharashtra" +"-"+ savedAddressPincode;

                        String comment = "No Comments";

                        String price = getIntent().getStringExtra("Price");

                        Intent placeOrder = new Intent(Address.this, PlaceOrder.class);
                        placeOrder.putExtra("Address",edAddress);
                        placeOrder.putExtra("Comments",comment);
                        placeOrder.putExtra("Price",price);
                        startActivity(placeOrder);
                        finish();

                    }
                });

            }
        }

        addNewAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Address.this, NewAddress.class));
            }
        });

    }

    @Override
    public void onBackPressed() {

        Common.list.clear();
        super.onBackPressed();
    }
}
