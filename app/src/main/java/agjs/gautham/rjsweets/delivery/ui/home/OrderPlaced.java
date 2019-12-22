package agjs.gautham.rjsweets.delivery.ui.Home;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
import agjs.gautham.rjsweets.delivery.OrderDetailDelivery;
import agjs.gautham.rjsweets.user.navigation_drawer.order_user.OrderDetail;
import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderPlaced extends AppCompatActivity {

    AlertDialog dialog;

    private FirebaseDatabase database;
    private DatabaseReference requests;
    private RecyclerView recyclerView;

    APIService mService;

    private EditText editText;
    private Spinner spinner;

    TextView textView;

    RecyclerView.LayoutManager layoutManager;

    private FirebaseRecyclerAdapter<Request, OrderViewHolderDelivery> adapter;

    private static final String[] status = {"Confirming","Placed","On the way","Shipped","Delivered","Rejected"};
    private static final String[] statusCode = {"0","1","2","3","4","5"};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.orders_placed);

        Toolbar toolbar = findViewById(R.id.toolbar_placed);
        toolbar.setTitle("Placed Order");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //Init Service
        mService = Common.getFCMClient();

        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setCancelable(false)
                .setMessage("Loading ...")
                .setTheme(R.style.DialogCustom)
                .build();
        dialog.show();

        //Init Firebase
        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Requests");

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final FirebaseUser mUser = mAuth.getCurrentUser();

        textView = findViewById(R.id.order_empty);

        recyclerView = findViewById(R.id.listOrders);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        ((LinearLayoutManager) layoutManager).setReverseLayout(true);
        ((LinearLayoutManager) layoutManager).setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        if (Common.isConnectedToInternet(this)){
            loadOrders();
        }else {
            textView.setVisibility(View.VISIBLE);
            textView.setText(getResources().getString(R.string.no_internet));
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    }

    private void loadOrders() {

        adapter = new FirebaseRecyclerAdapter<Request, OrderViewHolderDelivery>(
                Request.class,
                R.layout.order_layout_admin_delivery,
                OrderViewHolderDelivery.class,
                requests
        ) {
            @Override
            protected void populateViewHolder(OrderViewHolderDelivery orderViewHolder, final Request request, final int i) {

                final String OrderId, OrderStatus;

                OrderId = adapter.getRef(i).getKey();
                OrderStatus = Common.convertCodeToStatus(request.getStatus());

                if (request.getStatus().equals("0")){

                    textView.setVisibility(View.GONE);

                    orderViewHolder.btndirection.setVisibility(View.GONE);
                    orderViewHolder.btnedit.setVisibility(View.GONE);

                    orderViewHolder.itemView.setVisibility(View.VISIBLE);
                    orderViewHolder.itemView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                    orderViewHolder.txtOrderId.setText(String.format("Order Id :  %s",OrderId));
                    orderViewHolder.txtOrderStatus.setText(String.format("Order Status :  %s",OrderStatus));
                    orderViewHolder.txtOrderAddress.setText(String.format("Address : %s",request.getAddress()));
                    orderViewHolder.txtOrderPhone.setText(String.format("Phone : %s",request.getPhone()));
                    orderViewHolder.txtComment.setText(String.format("Comment : %s",request.getComment()));
                    orderViewHolder.txtUsername.setText(String.format("Username : %s",request.getName()));

                    if (dialog.isShowing()){
                        dialog.dismiss();
                    }

                    orderViewHolder.btnedit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            showUpdateDialog(OrderId,
                                    adapter.getItem(i));
                        }
                    });

                    orderViewHolder.btndetails.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent orderDetail = new Intent(OrderPlaced.this, OrderDetailDelivery.class);
                            orderDetail.putExtra("OrderId",adapter.getRef(i).getKey());
                            orderDetail.putExtra("OrderStatus",request.getStatus());
                            startActivity(orderDetail);
                        }
                    });

                    /*orderViewHolder.btndirection.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent trackingOrder = new Intent(OrderPlacedAdmin.this, TrackingOrderDelivery.class);
                            trackingOrder.putExtra("Address",request.getAddress());
                            trackingOrder.putExtra("Phone",request.getPhone());
                            startActivity(trackingOrder);
                        }
                    });*/

                }else {
                    orderViewHolder.itemView.setVisibility(View.GONE);
                    orderViewHolder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0,0));
                }
            }
        };

        if (String.valueOf(adapter.getItemCount()).equals("0")){
            if (dialog.isShowing()){
                dialog.dismiss();
            }
            textView.setVisibility(View.VISIBLE);
            textView.setText("Don't Panic! You Don't No Orders Have been  Placed");
        }

        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }

    private void showUpdateDialog(String key, final Request item) {

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Update Status");
        alertDialog.setMessage("Please Choose Status");

        LayoutInflater inflater = getLayoutInflater();
        final View view = inflater.inflate(R.layout.update_order_delivery,null);

        spinner = view.findViewById(R.id.statusSpinner_delivery);

        spinner.setAdapter(new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,status));
        alertDialog.setView(view);

        final String localKey = key;
        alertDialog.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                String status1 = statusCode[spinner.getSelectedItemPosition()];
                item.setStatus(status1);

                /*if (status1.equals("4")){
                    item.setDeliveredBy(Common.USER_Phone);
                }*/

                adapter.notifyDataSetChanged();     //Added to update item size
                requests.child(localKey).setValue(item);

                if (status1.equals("5")){
                    //item.setDeliveredBy(Common.USER_Phone);
                    showDeleteDialog(localKey,item);
                }else {
                    sendOrderStatusToUser(localKey,item,status1);
                }
            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialog.show();
    }

    private void sendOrderStatusToUser(final String localKey, final Request item, final String status1) {
        DatabaseReference tokens = database.getReference("Tokens");
        tokens.orderByKey().equalTo(item.getPhone())
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
                                            if (response.body().success == 1){
                                                Toast.makeText(OrderPlaced.this,"Order was updated",Toast.LENGTH_LONG).show();
                                            }else {
                                                Toast.makeText(OrderPlaced.this,"Failed To send SendNotification but Order Updated",Toast.LENGTH_LONG).show();
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

    private void showDeleteDialog(final String key, final Request item) {

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(OrderPlaced.this);
        alertDialog.setTitle("Delete Order !");
        alertDialog.setMessage("Please Enter Your Reason");
        alertDialog.setCancelable(true);

        LayoutInflater inflater = OrderPlaced.this.getLayoutInflater();
        final View view = inflater.inflate(R.layout.delete_order_delivery,null);

        editText = view.findViewById(R.id.delete_reason_text_delivery);
        alertDialog.setView(view);

        final String localKey = key;
        alertDialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

                String reason = editText.getText().toString();
                adapter.notifyDataSetChanged();     //Added to update item size
                //deleteOrder(key);
                item.setStatus("5");
                item.setReason(reason);
                requests.child(localKey).setValue(item);

                sendOrderRemovedStatus(localKey,item);
            }
        });
        alertDialog.show();
    }

    private void sendOrderRemovedStatus(final String localKey, final Request item) {
        DatabaseReference tokens = database.getReference("Tokens");
        tokens.orderByKey().equalTo(item.getPhone())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnapshot:dataSnapshot.getChildren()){
                            Token token = postSnapshot.getValue(Token.class);

                            //Make New Payload
                            Notification notification = new Notification("Your Order "+localKey+" was Rejected","RJ Sweets");
                            Sender content = new Sender(token.getToken(),notification);
                            mService.sendNotification(content)
                                    .enqueue(new Callback<MyResponse>() {
                                        @Override
                                        public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                            if (response.body().success == 1){
                                                Toast.makeText(OrderPlaced.this,"Order was updated",Toast.LENGTH_LONG).show();
                                            }else {
                                                Toast.makeText(OrderPlaced.this,"Failed To send SendNotification but Order Deleted",Toast.LENGTH_LONG).show();
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
