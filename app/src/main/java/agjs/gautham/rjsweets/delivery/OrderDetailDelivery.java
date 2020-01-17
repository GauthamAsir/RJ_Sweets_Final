package agjs.gautham.rjsweets.delivery;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.agik.AGIKSwipeButton.Controller.OnSwipeCompleteListener;
import com.agik.AGIKSwipeButton.View.Swipe_Button_View;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import agjs.gautham.rjsweets.Common;
import agjs.gautham.rjsweets.Model.MyResponse;
import agjs.gautham.rjsweets.Model.Notification;
import agjs.gautham.rjsweets.Model.Request;
import agjs.gautham.rjsweets.Model.Sender;
import agjs.gautham.rjsweets.Model.Token;
import agjs.gautham.rjsweets.R;
import agjs.gautham.rjsweets.Remote.APIService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderDetailDelivery extends AppCompatActivity {

    TextView order_id, order_phone, order_address, order_total, order_comment,user_name;
    String order_id_value="";
    String order_status="";
    RecyclerView lstSweets;
    RecyclerView.LayoutManager layoutManager;
    FirebaseDatabase database;
    DatabaseReference databaseReference;

    Swipe_Button_View pickup;
    Request request;

    APIService mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail_delivery);

        order_id = findViewById(R.id.order_id_delivery);
        order_phone= findViewById(R.id.order_phone_delivery);
        order_address = findViewById(R.id.order_address_delivery);
        order_total = findViewById(R.id.order_total_delivery);
        order_comment = findViewById(R.id.order_comment_delivery);
        user_name = findViewById(R.id.user_name_delivery);

        //Init Service
        mService = Common.getFCMClient();

        pickup = findViewById(R.id.pickup_delivery);

        lstSweets = findViewById(R.id.listsweets_details_delivery);
        lstSweets.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        lstSweets.setLayoutManager(layoutManager);

        if (getIntent() != null){
            order_id_value = getIntent().getStringExtra("OrderId");
            order_status = getIntent().getStringExtra("OrderStatus");
        }

        if (order_status.equals("0") || order_status.equals("1")){

            pickup.setVisibility(View.VISIBLE);
            if (order_status.equals("0")){

                pickup.setOnSwipeCompleteListener_forward_reverse(new OnSwipeCompleteListener() {
                    @Override
                    public void onSwipe_Forward(Swipe_Button_View swipe_button_view) {
                        pickup.setEnabled(false);
                        pickup.setThumbImage(null);
                        pickup.setText("Order Picked");

                        pickUp();
                    }

                    @Override
                    public void onSwipe_Reverse(Swipe_Button_View swipe_button_view) {
                        //InActive
                    }
                });
            }else {

                pickup.setText("Delivered");

                pickup.setOnSwipeCompleteListener_forward_reverse(new OnSwipeCompleteListener() {
                    @Override
                    public void onSwipe_Forward(Swipe_Button_View swipe_button_view) {
                        pickup.setEnabled(false);
                        pickup.setThumbImage(null);
                        pickup.setText("Delivery Completed");

                        delivered();
                    }

                    @Override
                    public void onSwipe_Reverse(Swipe_Button_View swipe_button_view) {

                    }
                });
            }

        }else {
            pickup.setVisibility(View.GONE);
        }

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("Requests");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                request = dataSnapshot.child(order_id_value).getValue(Request.class);

                //Set Value to recycler view
                order_id.setText(String.format("Order Id : %s",order_id_value));
                order_phone.setText(String.format("Phone : %s",request.getPhone()));
                order_total.setText(String.format("Total : %s",request.getTotal()));
                order_address.setText(String.format("Address : %s",request.getAddress()));
                order_comment.setText(String.format("Comment : %s",request.getComment()));
                user_name.setText(String.format("Username : %s",request.getName()));

                OrderDetailAdapterDelivery adapter = new OrderDetailAdapterDelivery(request.getSweetOrders());
                adapter.notifyDataSetChanged();
                lstSweets.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void delivered() {

        databaseReference.child(order_id_value).child("deliveredBy").setValue(Common.USER_Phone);
        databaseReference.child(order_id_value).child("status").setValue("2");

        sendOrderStatusToUser(order_id_value, "2");
        startActivity(new Intent(OrderDetailDelivery.this,DashboardDelivery.class));

    }

    private void pickUp() {

        sendOrderStatusToUser(order_id_value,"1");
        databaseReference.child(order_id_value).child("picked").setValue("1");
        databaseReference.child(order_id_value).child("pickedBy").setValue(Common.USER_Phone);
        databaseReference.child(order_id_value).child("status").setValue("1");
        startActivity(new Intent(OrderDetailDelivery.this,DashboardDelivery.class));
    }

    private void sendOrderStatusToUser(final String localKey, final String status1) {
        DatabaseReference tokens = database.getReference("Tokens");
        tokens.orderByKey().equalTo(request.getPhone())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnapshot:dataSnapshot.getChildren()){
                            Token token = postSnapshot.getValue(Token.class);

                            //Make New Payload
                            Notification notification = new Notification
                                    ("Your Order "+localKey+" is "+Common.convertCodeToStatus(status1) ,"RJ Sweets");
                            Sender content = new Sender(token.getToken(),notification);
                            mService.sendNotification(content)
                                    .enqueue(new Callback<MyResponse>() {
                                        @Override
                                        public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {

                                            if (response.code() == 200){

                                                if (response.body().success==1){
                                                    Log.d("test",response.message());
                                                    Toast.makeText(OrderDetailDelivery.this,"Order was updated",Toast.LENGTH_LONG).show();
                                                }else {
                                                    Log.d("test","heryr "+response.body().success);
                                                    Toast.makeText(OrderDetailDelivery.this,"Failed To send SendNotification but Order Updated",Toast.LENGTH_LONG).show();
                                                }

                                            }
                                        }
                                        @Override
                                        public void onFailure(Call<MyResponse> call, Throwable t) {
                                            Log.e("ERROR",t.getMessage());
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

}
