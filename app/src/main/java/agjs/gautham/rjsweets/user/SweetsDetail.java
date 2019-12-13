package agjs.gautham.rjsweets.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import agjs.gautham.rjsweets.Common;
import agjs.gautham.rjsweets.Database.Database;
import agjs.gautham.rjsweets.Model.Sweet;
import agjs.gautham.rjsweets.Model.SweetOrder;
import agjs.gautham.rjsweets.R;
import io.paperdb.Paper;

public class SweetsDetail extends AppCompatActivity {

    TextView sweet_name, sweet_price, sweet_description, outOfStock;
    ImageView sweet_image;
    CollapsingToolbarLayout collapsingToolbarLayout;
    FloatingActionButton btnCart;
    ElegantNumberButton numberButton;

    Button buyNow;

    String sweetId="";
    String phone = Common.USER_Phone;
    String avaQuantity="";
    String appType="";

    FirebaseDatabase database;
    DatabaseReference sweet;

    Sweet currentSweet;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sweets_detail);

        //Firebase
        database = FirebaseDatabase.getInstance();
        sweet = database.getReference("Sweets");
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final FirebaseUser mUser = mAuth.getCurrentUser();

        //Init View
        numberButton = findViewById(R.id.number_button_user);

        toolbar = findViewById(R.id.toolbar_sweets_user);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //Get Category Id & Available Quantity Info from Intent Extra
        if (getIntent() != null){
            sweetId = getIntent().getStringExtra("SweetId");
            avaQuantity = getIntent().getStringExtra("AvailableQuantity");
            appType = getIntent().getStringExtra("AppType");

        }



        sweet_description = findViewById(R.id.sweets_description_user);
        sweet_name = findViewById(R.id.sweets_name_user);
        sweet_price = findViewById(R.id.sweets_price_user);
        sweet_image = findViewById(R.id.img_sweets_user);

        collapsingToolbarLayout = findViewById(R.id.collapsing_user);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.CollapsedAppBar);

        outOfStock = findViewById(R.id.txt_out_of_stock_user);

        buyNow = findViewById(R.id.bt_buyNow_user);
        btnCart = findViewById(R.id.btn_cart_user);

        if(appType != null){

            if (appType.equals("admin")){
                numberButton.setVisibility(View.GONE);
                btnCart.hide();
                buyNow.setVisibility(View.GONE);
            } else {
                numberButton.setVisibility(View.VISIBLE);
                buyNow.setVisibility(View.VISIBLE);
            }

        }

        numberButton.setRange(1,Integer.parseInt(avaQuantity));
        numberButton.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
            @Override
            public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {
                if (String.valueOf(newValue).equals(avaQuantity)){
                    Toast.makeText(SweetsDetail.this,"You have Reached Maximum Available Quantity",Toast.LENGTH_LONG).show();
                }
            }
        });

        if (avaQuantity.equals("0")){
            buyNow.setEnabled(false);
            btnCart.hide();
            outOfStock.setVisibility(View.VISIBLE);
        }

        if (avaQuantity.equals("1")){
            outOfStock.setVisibility(View.VISIBLE);
            outOfStock.setText("Last Item Left Hurry Up !");
        }

        if (Common.isConnectedToInternet(this)){

                buyNow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mUser != null) {

                            Toast.makeText(SweetsDetail.this, "Please Select an Address", Toast.LENGTH_SHORT).show();

                            String savedAddressLine1 = Paper.book().read(Common.USER_ADDRESS_LINE1);
                            String savedAddressLine2 = Paper.book().read(Common.USER_ADDRESS_LINE2);
                            String savedAddressLandmark = Paper.book().read(Common.USER_ADDRESS_LANDMARK);
                            String savedAddressPincode = Paper.book().read(Common.USER_ADDRESS_Pincode);

                            SweetOrder sweetOrder1 = new SweetOrder(
                                    phone,
                                    sweetId,
                                    currentSweet.getName(),
                                    numberButton.getNumber(),
                                    currentSweet.getPrice(),
                                    currentSweet.getDiscount(),
                                    currentSweet.getImage());

                            Common.list.add(sweetOrder1);

                            if (savedAddressLine1 != null && savedAddressLine2 != null && savedAddressLandmark != null
                                    && savedAddressPincode != null) {
                                Intent intent = new Intent(SweetsDetail.this, Address.class);
                                intent.putExtra("Price",currentSweet.getPrice());
                                startActivity(intent);
                                finish();
                            }else {
                                Intent intent = new Intent(SweetsDetail.this, NewAddress.class);
                                intent.putExtra("Price",currentSweet.getPrice());
                                startActivity(intent);
                                finish();
                            }

                        } else {
                            Toast.makeText(SweetsDetail.this, "You Need to be Logged In !", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            } else {
            Toast.makeText(SweetsDetail.this, "No Internet", Toast.LENGTH_SHORT).show();
        }

        if (Common.isConnectedToInternet(this)){
            btnCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addToCart();
                }
            });
        }else {
            Toast.makeText(SweetsDetail.this, "No Internet", Toast.LENGTH_SHORT).show();
        }

        if (!sweetId.isEmpty()){
            if (Common.isConnectedToInternet(getBaseContext())){
                getDetailSweet(sweetId);
            }else {
                Toast.makeText(SweetsDetail.this,"Please Check Your Internet Connection !", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void addToCart(){

        boolean isExists = new Database(getBaseContext()).checkSweetExists(sweetId, Common.USER_Phone);

        if (isExists){
            new Database(getApplicationContext()).increaseCart(phone, sweetId, numberButton.getNumber());
            Toast.makeText(SweetsDetail.this, "Added To Cart", Toast.LENGTH_SHORT).show();

        } else {
            new Database(getApplicationContext()).addToCart(new SweetOrder(
                    phone,
                    sweetId,
                    currentSweet.getName(),
                    numberButton.getNumber(),
                    currentSweet.getPrice(),
                    currentSweet.getDiscount(),
                    currentSweet.getImage()
            ));
            Toast.makeText(SweetsDetail.this, "Added To Cart", Toast.LENGTH_SHORT).show();
        }

    }

    private void getDetailSweet(final String sweetId) {

        sweet.child(sweetId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                currentSweet = dataSnapshot.getValue(Sweet.class);

                Picasso.get().load(currentSweet.getImage())
                        .into(sweet_image);

                sweet_price.setText(currentSweet.getPrice());
                sweet_name.setText(currentSweet.getName());
                sweet_description.setText(currentSweet.getDescription());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
