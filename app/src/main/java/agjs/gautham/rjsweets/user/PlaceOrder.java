package agjs.gautham.rjsweets.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.agik.AGIKSwipeButton.Controller.OnSwipeCompleteListener;
import com.agik.AGIKSwipeButton.View.Swipe_Button_View;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import agjs.gautham.rjsweets.Database.Database;
import agjs.gautham.rjsweets.Model.SweetOrder;
import agjs.gautham.rjsweets.R;
import agjs.gautham.rjsweets.common.Common;
import agjs.gautham.rjsweets.user.navigation_drawer.order_user.OrderDetailAdapter;

public class PlaceOrder extends AppCompatActivity {

    String address, price, latlng;

    List<SweetOrder> cart = new ArrayList<>();

    FirebaseDatabase database;
    DatabaseReference requests, sweets;

    String avaQuantity, orderTime, orderDate, paymentMode;

    boolean checkCart;

    private TextView orderId, username, items_total, packaging_charge, delivery_charge, order_total;
    private Button changeAddress, back;
    private RadioGroup paymentGroup;
    private RecyclerView listSweets;
    private Swipe_Button_View swipeConfirm;

    private RelativeLayout parent_layout;

    private String order_number;
    private double orderT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_order);

        database = FirebaseDatabase.getInstance();

        orderId = findViewById(R.id.orderId);
        username = findViewById(R.id.username);

        changeAddress = findViewById(R.id.change_address);
        paymentGroup = findViewById(R.id.radio_group);
        back = findViewById(R.id.back);

        items_total = findViewById(R.id.items_total);
        packaging_charge = findViewById(R.id.packaging_charge);
        delivery_charge = findViewById(R.id.delivery_charge);
        order_total = findViewById(R.id.order_total);

        listSweets = findViewById(R.id.list_sweets);
        listSweets.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        listSweets.setLayoutManager(layoutManager);

        swipeConfirm = findViewById(R.id.swipeConfirm);

        parent_layout = findViewById(R.id.parent_layout);

        if (Common.list.size() != 0){
            cart = Common.list;
            checkCart = false;
        }else {
            cart = new Database(getApplicationContext()).getCarts(Common.USER_Phone);
            checkCart = true;
        }

        //Firebase
        sweets = database.getReference("Sweets");
        requests = database.getReference("Requests");

        if (getIntent() != null){

            address = getIntent().getStringExtra("Address");
            price = getIntent().getStringExtra("Price");
            latlng = getIntent().getStringExtra("LatLng");

        }

        generateOrder();

        makeOrder();

        int id = paymentGroup.getCheckedRadioButtonId();

        if (id == R.id.cod) {
            Date dt = Calendar.getInstance().getTime();
            SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
            orderTime = timeFormat.format(dt);
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            orderDate = dateFormat.format(dt);
            paymentMode = "Pay On Delivery";
        }

        changeAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        swipeConfirm.setOnSwipeCompleteListener_forward_reverse(new OnSwipeCompleteListener() {
            @Override
            public void onSwipe_Forward(Swipe_Button_View swipe_button_view) {

                swipeConfirm.setEnabled(false);
                swipeConfirm.setThumbImage(null);
                swipeConfirm.setText("Order Confirmed");
                Intent intent = new Intent(PlaceOrder.this, OrderPlaceStatus.class);
                intent.putExtra("OrderId",order_number);
                intent.putExtra("Address",address);
                intent.putExtra("Price",String.valueOf(orderT));
                intent.putExtra("PaymentMode",paymentMode);
                intent.putExtra("LatLng",latlng);
                Common.intentOpenAnimation(PlaceOrder.this);
                startActivity(intent);
                finish();
            }

            @Override
            public void onSwipe_Reverse(Swipe_Button_View swipe_button_view) {
                //InActive
            }
        });
    }

    private void generateOrder(){

        order_number = String.valueOf(System.currentTimeMillis());

        orderId.setText(order_number);
        username.setText(Common.Name);

        items_total.setText(String.format("%s ₹",price));
        packaging_charge.setText("10.0 ₹");
        delivery_charge.setText("40.0 ₹");

        orderT = Double.parseDouble(price) + 40.0 + 10.0;
        order_total.setText(String.format("%s ₹",String.valueOf(orderT)));

        OrderDetailAdapter orderDetailAdapter = new OrderDetailAdapter(cart);
        orderDetailAdapter.notifyDataSetChanged();
        listSweets.setAdapter(orderDetailAdapter);

    }

    private void makeOrder(){

        for (int i=0; i<cart.size(); i++){

            final String id = cart.get(i).getProductId();
            final String orderQuantity = cart.get(i).getQuantity();

            sweets.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    avaQuantity = dataSnapshot.child("avaQuantity").getValue(String.class);

                    String sweetName = dataSnapshot.child("name").getValue(String.class);

                    if (Integer.parseInt(avaQuantity) < Integer.parseInt(orderQuantity)){

                        swipeConfirm.setVisibility(View.GONE);
                        back.setVisibility(View.VISIBLE);
                        Snackbar.make(parent_layout,sweetName+" is more than the Available Quantity",Snackbar.LENGTH_LONG).show();

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Common.list.clear(); //To Avoid Conflicts between Cart and Buy Option
        Common.intentCloseAnimation(PlaceOrder.this);
    }

}
