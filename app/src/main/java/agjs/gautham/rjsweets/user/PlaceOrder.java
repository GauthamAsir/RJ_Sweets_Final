package agjs.gautham.rjsweets.user;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

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
import agjs.gautham.rjsweets.Model.Sender;
import agjs.gautham.rjsweets.Model.SweetOrder;
import agjs.gautham.rjsweets.Model.Token;
import agjs.gautham.rjsweets.R;
import agjs.gautham.rjsweets.Remote.APIService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlaceOrder extends AppCompatActivity {

    RadioGroup paymentGrp;
    RadioButton paymentMethod;

    String address, comment, price;

    List<SweetOrder> cart = new ArrayList<>();

    FirebaseDatabase database;
    DatabaseReference requests, sweets;

    String avaQuantity, orderTime, orderDate, paymentMode;
    int finalQantity;

    boolean checkCart;

    private APIService mService;

    Button place;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_order);

        database = FirebaseDatabase.getInstance();

        mService = Common.getFCMService();

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
            comment = getIntent().getStringExtra("Comment");
            price = getIntent().getStringExtra("Price");

        }

        paymentGrp = findViewById(R.id.payment_group_user);

        place = findViewById(R.id.btnplace_user);

        place.setOnClickListener(new View.OnClickListener() {
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
        });
    }

    private void makeOrder(){

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

        sendNotificationorder(order_number);
        startActivity(new Intent(this,DashboardUser.class));
        finish();
        toast("Thank You Order Placed !");

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
                    Notification notification = new Notification("You Have New Order" + order_number, "RJ Sweets");
                    Sender content = new Sender(serverToken.getToken(), notification);
                    mService.sendNotification(content)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if (response.code() == 200 ) {

                                        if (response.body().success == 1) {
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

    private void toast(String msg){
        Toast.makeText(PlaceOrder.this,msg,Toast.LENGTH_LONG).show();
    }

}
