package agjs.gautham.rjsweets.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

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
import agjs.gautham.rjsweets.Model.Request;
import agjs.gautham.rjsweets.R;
import agjs.gautham.rjsweets.Remote.APIService;
import agjs.gautham.rjsweets.admin.ViewHolder.OrderDetailAdapterAdmin;

public class OrderDetailAdmin extends AppCompatActivity {

    TextView order_id, order_phone, order_address, order_total, user_name;
    String order_id_value="";
    String order_status="";
    RecyclerView lstSweets;
    RecyclerView.LayoutManager layoutManager;
    FirebaseDatabase database;
    DatabaseReference databaseReference;

    Request request;

    Swipe_Button_View reject;

    EditText editText;

    APIService mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail_admin);

        order_id = findViewById(R.id.order_id);
        order_phone= findViewById(R.id.order_phone);
        order_address = findViewById(R.id.order_address);
        order_total = findViewById(R.id.order_total);
        user_name = findViewById(R.id.user_name);

        reject = findViewById(R.id.reject_order);

        //Init Service
        mService = Common.getFCMClient();

        lstSweets = findViewById(R.id.listsweets);
        lstSweets.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        lstSweets.setLayoutManager(layoutManager);

        if (getIntent() != null){
            order_id_value = getIntent().getStringExtra("OrderId");
            order_status = getIntent().getStringExtra("OrderStatus");
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
                user_name.setText(String.format("Username : %s",request.getName()));

                OrderDetailAdapterAdmin adapter = new OrderDetailAdapterAdmin(request.getSweetOrders());
                adapter.notifyDataSetChanged();
                lstSweets.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if (order_status.equals("0") || order_status.equals("1")){

            reject.setVisibility(View.VISIBLE);
            reject.setOnSwipeCompleteListener_forward_reverse(new OnSwipeCompleteListener() {
                @Override
                public void onSwipe_Forward(Swipe_Button_View swipe_button_view) {
                    reject.setEnabled(false);
                    reject.setThumbImage(null);
                    reject.setText("Order Rejected");
                    Intent intent = new Intent(OrderDetailAdmin.this,RejectOrderReason.class);
                    intent.putExtra("OrderId",order_id_value);
                    intent.putExtra("OrderStatus", order_status);
                    intent.putExtra("OrderPhNumber",request.getPhone());
                    startActivity(intent);
                }

                @Override
                public void onSwipe_Reverse(Swipe_Button_View swipe_button_view) {
                    //InActive
                }
            });
        }else {
            reject.setVisibility(View.GONE);
        }
    }
}
