package agjs.gautham.rjsweets.admin;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
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
import agjs.gautham.rjsweets.admin.ViewHolder.OrderDetailAdapterAdmin;
import agjs.gautham.rjsweets.user.navigation_drawer.order_user.OrderDetail;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderDetailAdmin extends AppCompatActivity {

    TextView order_id, order_phone, order_address, order_total, order_comment,user_name;
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
        order_comment = findViewById(R.id.order_comment);
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
                order_comment.setText(String.format("Comment : %s",request.getComment()));
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
                    rejectOrder();
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

    private void rejectOrder() {
        showDeleteDialog();
    }

    private void showDeleteDialog() {

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(OrderDetailAdmin.this);
        alertDialog.setTitle("Delete Order !");
        alertDialog.setMessage("Please Enter Your Reason");
        alertDialog.setCancelable(false);

        LayoutInflater inflater = OrderDetailAdmin.this.getLayoutInflater();
        final View view = inflater.inflate(R.layout.delete_order,null);

        editText = view.findViewById(R.id.delete_reason_text);
        alertDialog.setView(view);

        alertDialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        final AlertDialog dialog =alertDialog.create();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String reason = editText.getText().toString();
                if (!reason.isEmpty()){
                    databaseReference.child(order_id_value).child("picked").setValue("0");
                    databaseReference.child(order_id_value).child("pickedBy").setValue(Common.PHONE_KEY);
                    databaseReference.child(order_id_value).child("status").setValue("3");

                    sendOrderStatusToUser();
                }
            }
        });
    }

    private void sendOrderStatusToUser() {

        DatabaseReference tokens = database.getReference("Tokens");
        tokens.orderByKey().equalTo(Common.PHONE_KEY)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnapshot:dataSnapshot.getChildren()){
                            Token token = postSnapshot.getValue(Token.class);

                            //Make New Payload
                            Notification notification = new Notification
                                    ("Your Order "+order_id_value+" is "+Common.convertCodeToStatus(order_status) ,"RJ Sweets");
                            Sender content = new Sender(token.getToken(),notification);
                            mService.sendNotification(content)
                                    .enqueue(new Callback<MyResponse>() {
                                        @Override
                                        public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                            if (response.body().success == 1){
                                                Toast.makeText(OrderDetailAdmin.this,"Order was updated",Toast.LENGTH_LONG).show();
                                            }else {
                                                Toast.makeText(OrderDetailAdmin.this,"Failed To send SendNotification but Order Updated",Toast.LENGTH_LONG).show();
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
