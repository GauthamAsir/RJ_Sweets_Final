package agjs.gautham.rjsweets.common;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
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

import java.util.Arrays;
import java.util.List;

import agjs.gautham.rjsweets.Database.Database;
import agjs.gautham.rjsweets.Model.Sweet;
import agjs.gautham.rjsweets.Model.SweetOrder;
import agjs.gautham.rjsweets.R;
import agjs.gautham.rjsweets.admin.navigation_drawer.home.UpdateSweets;
import agjs.gautham.rjsweets.user.PlaceOrder;
import io.paperdb.Paper;

public class SweetsDetail extends AppCompatActivity {

    TextView sweet_name, sweet_price, sweet_description, outOfStock;
    ImageView sweet_image;
    CollapsingToolbarLayout collapsingToolbarLayout;
    FloatingActionButton btnCart;
    ElegantNumberButton numberButton;

    Button buyNow, edit_admin;

    AlertDialog.Builder builder;
    View view1;

    String latlng;

    String sweetId="";
    String phone = Common.USER_Phone;
    String avaQuantity="";
    String appType="";
    String img_url="";

    FirebaseDatabase database;
    DatabaseReference sweet;

    Sweet currentSweet;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sweets_detail);

        Paper.init(this);

        view1 = LayoutInflater.from(SweetsDetail.this).inflate(R.layout.address_places,null);

        //Firebase
        database = FirebaseDatabase.getInstance();
        sweet = database.getReference("Sweets");
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final FirebaseUser mUser = mAuth.getCurrentUser();

        Places.initialize(getApplicationContext(), "AIzaSyAfpdkEt00wmFp8DZVhqqnqG3JpQB880mM");
        final PlacesClient placesClient = Places.createClient(this);

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
        edit_admin = findViewById(R.id.edit_admin);

        if(appType != null){

            if (appType.equals("admin")){
                numberButton.setVisibility(View.GONE);
                btnCart.hide();
                buyNow.setVisibility(View.GONE);
                edit_admin.setVisibility(View.VISIBLE);

            } else {
                numberButton.setVisibility(View.VISIBLE);
                buyNow.setVisibility(View.VISIBLE);
                edit_admin.setVisibility(View.GONE);
            }

        }

        edit_admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SweetsDetail.this, UpdateSweets.class);
                intent.putExtra("Key",sweetId);
                intent.putExtra("Url",img_url);
                startActivity(intent);
                Common.intentOpenAnimation(SweetsDetail.this);
            }
        });

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

        builder = new AlertDialog.Builder(SweetsDetail.this);

        if (Common.isConnectedToInternet(this)){

                buyNow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mUser != null) {

                            builder.setTitle("Find Places");
                            builder.setCancelable(false);
                            builder.setView(view1);
                            builder.setIcon(R.drawable.ic_place_black_24dp);

                            final String savedAddress = Paper.book().read(Common.USER_ADDRESS_SAVED);
                            final String savedLatlng = Paper.book().read(Common.USER_SAVED_LATLNG);

                            SweetOrder sweetOrder1 = new SweetOrder(
                                    phone,
                                    sweetId,
                                    currentSweet.getName(),
                                    numberButton.getNumber(),
                                    currentSweet.getPrice(),
                                    currentSweet.getDiscount(),
                                    currentSweet.getImage());

                            Common.list.add(sweetOrder1);

                            if (savedAddress != null){

                                final AlertDialog.Builder savedAddresBuilder = new AlertDialog.Builder(SweetsDetail.this);
                                savedAddresBuilder.setTitle("Saved Address");

                                final View view2 = LayoutInflater.from(SweetsDetail.this)
                                        .inflate(R.layout.saved_address, null);

                                savedAddresBuilder.setView(view2);

                                TextView addrs = view2.findViewById(R.id.addrs);
                                ImageView delete_addrs = view2.findViewById(R.id.delete_adrs);
                                ImageView new_adrs = view2.findViewById(R.id.new_adrs);
                                Button select_adrs = view2.findViewById(R.id.select_adrs);

                                String a = savedAddress.replaceAll("\\s+","");
                                String address = a.replace(",",",\n");
                                addrs.setText(address);

                                final AlertDialog alertDialog = savedAddresBuilder.create();

                                alertDialog.show();

                                delete_addrs.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        Paper.book().delete(Common.USER_ADDRESS_SAVED);
                                        Paper.book().delete(Common.USER_SAVED_LATLNG);
                                        alertDialog.dismiss();

                                    }
                                });

                                new_adrs.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        if (view1 != null){

                                            ViewGroup parent = (ViewGroup) view1.getParent();
                                            if (parent!= null){
                                                parent.removeView(view1);
                                            }

                                        }

                                        builder.setView(view1);
                                        buy_now(currentSweet.getPrice());

                                    }
                                });

                                select_adrs.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        Intent placeOrder = new Intent(SweetsDetail.this, PlaceOrder.class);
                                        placeOrder.putExtra("Price",currentSweet.getPrice());
                                        placeOrder.putExtra("Address",savedAddress);
                                        placeOrder.putExtra("LatLng",savedLatlng);

                                        alertDialog.dismiss();
                                        startActivity(placeOrder);
                                        Common.intentOpenAnimation(SweetsDetail.this);

                                    }
                                });

                            }
                            else{

                                if (view1 != null){

                                    ViewGroup parent = (ViewGroup) view1.getParent();
                                    if (parent!= null){
                                        parent.removeView(view1);
                                    }

                                }

                                buy_now(currentSweet.getPrice());

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

    private void buy_now(final String price) {

        final TextView enter_address = view1.findViewById(R.id.enter_address);
        final CheckBox checkBox = view1.findViewById(R.id.saveAddress);

        List<Place.Field> fields = Arrays.asList(Place.Field.ADDRESS, Place.Field.NAME,
                Place.Field.ID, Place.Field.LAT_LNG);

        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                (getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment));

        autocompleteFragment.setPlaceFields(fields);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {

                enter_address.setText(place.getAddress());
                latlng = place.getLatLng().toString();

            }

            @Override
            public void onError(@NonNull Status status) {

                Log.d("Error", status.toString());

            }
        });

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        final AlertDialog alertDialog = builder.create();

        alertDialog.show();

        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                builder.setView(null);

            }
        });

        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (enter_address!=null){

                    Intent placeOrder = new Intent(SweetsDetail.this, PlaceOrder.class);
                    placeOrder.putExtra("Price",price);
                    placeOrder.putExtra("Address",enter_address.getText().toString());
                    placeOrder.putExtra("LatLng",latlng);

                    if (checkBox.isChecked()){

                        Paper.book().write(Common.USER_ADDRESS_SAVED,enter_address.getText().toString());
                        Paper.book().write(Common.USER_SAVED_LATLNG,latlng);
                    }

                    alertDialog.dismiss();
                    startActivity(placeOrder);
                    Common.intentOpenAnimation(SweetsDetail.this);

                }

            }
        });

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

                img_url = currentSweet.getImage();

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
