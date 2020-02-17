package agjs.gautham.rjsweets.user;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.agik.AGIKSwipeButton.Controller.OnSwipeCompleteListener;
import com.agik.AGIKSwipeButton.View.Swipe_Button_View;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import agjs.gautham.rjsweets.Common;
import agjs.gautham.rjsweets.Database.Database;
import agjs.gautham.rjsweets.Model.MyResponse;
import agjs.gautham.rjsweets.Model.Notification;
import agjs.gautham.rjsweets.Model.Request;
import agjs.gautham.rjsweets.Model.RequestTemp;
import agjs.gautham.rjsweets.Model.Sender;
import agjs.gautham.rjsweets.Model.SweetOrder;
import agjs.gautham.rjsweets.Model.Token;
import agjs.gautham.rjsweets.R;
import agjs.gautham.rjsweets.Remote.APIService;
import agjs.gautham.rjsweets.user.navigation_drawer.order_user.OrderDetailAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlaceOrder extends AppCompatActivity {

    RadioGroup paymentGrp;
    RadioButton paymentMethod;

    String address, comment, price;

    List<SweetOrder> cart = new ArrayList<>();

    FirebaseDatabase database;
    DatabaseReference requests, sweets, temp_req;

    String avaQuantity, orderTime, orderDate, paymentMode;
    int finalQantity;

    boolean checkCart;

    private APIService mService;

    private FirebaseUser mUser;

    Button place;

    private static Socket socket;
    private static ServerSocket serverSocket;

    private static String ip = "192.168.0.103";
    private static PrintWriter printWriter;

    String message = "";

    private TextView orderId, username, orderTotal, pay_with;
    private Button changeAddress, back;
    private RadioGroup paymentGroup;
    private RecyclerView listSweets;
    private Swipe_Button_View swipeConfirm;

    private RelativeLayout parent_layout;

    private String order_number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_order);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        database = FirebaseDatabase.getInstance();

        mService = Common.getFCMService();

        orderId = findViewById(R.id.orderId);
        username = findViewById(R.id.username);
        orderTotal = findViewById(R.id.orderTotal);
        pay_with = findViewById(R.id.pay_with);

        changeAddress = findViewById(R.id.change_address);
        paymentGroup = findViewById(R.id.radio_group);
        back = findViewById(R.id.back);

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
        temp_req = database.getReference("Requests_Temp");

        if (getIntent() != null){

            address = getIntent().getStringExtra("Address");
            comment = getIntent().getStringExtra("Comment");
            price = getIntent().getStringExtra("Price");

        }

        generateOrder();

        makeOrder2();

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
                intent.putExtra("Price",price);
                intent.putExtra("Comment",comment);
                intent.putExtra("PaymentMode",paymentMode);
                startActivity(intent);
            }

            @Override
            public void onSwipe_Reverse(Swipe_Button_View swipe_button_view) {
                //InActive
            }
        });
        //paymentGrp = findViewById(R.id.payment_group_user);

        //place = findViewById(R.id.btnplace_user);

        /*place.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int id = paymentGrp.getCheckedRadioButtonId();

                paymentMethod = findViewById(id);

                if (id == -1){
                    toast("No Method Selected");
                } else {
                    switch (id){
                        case R.id.paypal_user:
                            toast("Paypal Service is Unavailable");
                            break;

                        case R.id.paytm_user:
                            toast("Paytm Service is Unavailable");
                            break;

                        case R.id.cod_user:
                            Date dt = Calendar.getInstance().getTime();
                            SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
                            orderTime = timeFormat.format(dt);
                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                            orderDate = dateFormat.format(dt);
                            paymentMode = "Cash On Delivery";
                            makeOrder();
                            break;
                    }
                }

            }
        });*/
    }

    private void generateOrder(){

        order_number = String.valueOf(System.currentTimeMillis());

        orderId.setText(order_number);
        username.setText(Common.Name);
        orderTotal.setText(price);
        pay_with.setText(getResources().getString(R.string.pay_on_delivery));

        OrderDetailAdapter orderDetailAdapter = new OrderDetailAdapter(cart);
        orderDetailAdapter.notifyDataSetChanged();
        listSweets.setAdapter(orderDetailAdapter);

    }

    private void makeOrder2(){

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

    /*private void makeOrder(){

        String order_number = String.valueOf(System.currentTimeMillis());

        Request request = new Request(
                order_number,
                Common.Name,
                Common.USER_Phone,
                address,
                price,
                "0", //init status
                comment,
                orderTime,
                orderDate,
                "empty", //Reject Reason Initially empty
                paymentMode,
                "0", //init isPicked
                "0", //init Picked By
                cart
        );


        for (int i=0; i<cart.size(); i++){

            final String id = cart.get(i).getProductId();
            final String orderQuantity = cart.get(i).getQuantity();

            sweets.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    //sweet = dataSnapshot.getValue(Sweet.class);

                    avaQuantity = dataSnapshot.child("AvaQuantity").getValue(String.class);

                    if (Integer.parseInt(avaQuantity) >= Integer.parseInt(orderQuantity)){

                        finalQantity = Integer.parseInt(avaQuantity) - Integer.parseInt(orderQuantity);
                        Log.d("Final Quantity", String.valueOf(finalQantity));
                        sweets.child(id).child("AvaQuantity").setValue(String.valueOf(finalQantity));

                    }else {
                        toast("Some Sweet Quantity is more than the Available Quantity, Your Order May get Rejected, Sorry For Inconvenience ");
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        //Update to Firebase using milisec to key
        requests.child(order_number).setValue(request);

        //Delete Cart After Updating

        if (checkCart){
            new Database(getBaseContext()).cleanCart(Common.USER_Phone);
            new Database(this).close();
        }else {
            Common.list.clear();
        }

        message = mUser.getEmail() + " ," + order_number+ " ," + Common.Name + " ," + Common.USER_Phone + " ," + price
                + " ,"  + "0" + " ," + " ,"  + orderTime + " ,"  + orderDate + " ," + cart ;

        sendMailToUser();

        sendNotificationorder(order_number);
        startActivity(new Intent(this,DashboardUser.class));
        finish();
        toast("Thank You Order Placed !");

    }*/

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Common.list.clear(); //To Avoid Conflicts between Cart and Buy Option
    }

    private void sendNotificationorder(final String order_number) {

        final DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query data = tokens.orderByChild("serverToken").equalTo(true);

        data.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot:dataSnapshot.getChildren()) {

                    Token serverToken = postSnapshot.getValue(Token.class);

                    //Create Raw PayLoad To Send
                    Notification notification = new Notification("You Have New Order" + order_number, "RJ Sweets Final");
                    Sender content = new Sender(serverToken.getToken(), notification);
                    mService.sendNotification(content)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if (response.code() == 200 ) {

                                        if (response.isSuccessful()) {
                                            Log.d("SendNotification Status","Success");
                                        } else {
                                            Log.d("SendNotification Status","Failed");
                                        }
                                    }
                                }
                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {
                                    Log.d("ERROR", t.getMessage());
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("CANCELED",databaseError.getMessage());
            }
        });
    }

    private void sendMailToUser(){

        myTask mt = new myTask();
        mt.execute();
        Log.d("Server msg", "Sent Successfully");

    }

    class myTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            try {

                socket = new Socket(ip, 5000);
                printWriter = new PrintWriter(socket.getOutputStream());
                printWriter.write(message);
                printWriter.flush();
                printWriter.close();
                socket.close();


            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    private void toast(String msg){
        Toast.makeText(PlaceOrder.this,msg,Toast.LENGTH_LONG).show();
    }

}
