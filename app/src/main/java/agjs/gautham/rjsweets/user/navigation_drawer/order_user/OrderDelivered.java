package agjs.gautham.rjsweets.user.navigation_drawer.order_user;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import agjs.gautham.rjsweets.Common;
import agjs.gautham.rjsweets.Model.Request;
import agjs.gautham.rjsweets.R;
import dmax.dialog.SpotsDialog;

public class OrderDelivered extends AppCompatActivity {

    android.app.AlertDialog dialog;
    private DatabaseReference requests;
    private RecyclerView recyclerView;

    TextView textView;

    RecyclerView.LayoutManager layoutManager;

    private FirebaseRecyclerAdapter<Request, OrderViewHolder> adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.orders_delivered);

        Toolbar toolbar = findViewById(R.id.toolbar_delivered);
        toolbar.setTitle("Delivered Order");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setCancelable(false)
                .setMessage("Loading ...")
                .setTheme(R.style.DialogCustom)
                .build();
        dialog.show();

        //Init Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
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
            if (mUser != null){
                textView.setVisibility(View.GONE);
                loadOrders(Common.USER_Phone);
            }else {
                textView.setVisibility(View.VISIBLE);
                textView.setText("You Need To Login");
            }
        }else {
            textView.setVisibility(View.VISIBLE);
            textView.setText(getResources().getString(R.string.no_internet));
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    }

    private void loadOrders(final String phone) {

        adapter = new FirebaseRecyclerAdapter<Request, OrderViewHolder>(
                Request.class,
                R.layout.order_layout,
                OrderViewHolder.class,
                requests.orderByChild("phone").equalTo(phone)
        ) {
            @Override
            protected void populateViewHolder(OrderViewHolder orderViewHolder, final Request request, final int i) {

                final String OrderId, OrderStatus, OrderTotal, OrderAddress, OrderComment, OrderUserName, OrderTime, OrderDate;

                OrderId = adapter.getRef(i).getKey();
                OrderStatus = Common.convertCodeToStatus(request.getStatus());
                OrderTotal = request.getTotal();
                OrderAddress = request.getAddress();
                OrderUserName = request.getName();
                OrderTime = request.getTime();
                OrderDate = request.getDate();

                orderViewHolder.root_order.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_scale_transmission));

                if (request.getStatus().equals("2")){

                    textView.setVisibility(View.GONE);

                    orderViewHolder.itemView.setVisibility(View.VISIBLE);
                    orderViewHolder.itemView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                    orderViewHolder.orderStatusImage.setImageResource(R.drawable.cupcake);
                    orderViewHolder.orderStatusImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    orderViewHolder.bt_orderDetails.setTextColor(ContextCompat.getColor(OrderDelivered.this, R.color.overlayBackground));
                    orderViewHolder.txtOrderId.setTextColor(ContextCompat.getColor(OrderDelivered.this, R.color.overlayBackground));

                    orderViewHolder.txtOrderStatus.setVisibility(View.GONE);
                    orderViewHolder.txtOrderDate.setVisibility(View.GONE);
                    orderViewHolder.txtOrderTime.setVisibility(View.GONE);

                    orderViewHolder.deliveredStatus.setVisibility(View.VISIBLE);

                    orderViewHolder.txtOrderId.setText(String.format("Order Id :  %s",OrderId));
                    orderViewHolder.txtOrderStatus.setText(String.format("Order Status :  %s",OrderStatus));
                    orderViewHolder.txtOrderTime.setText(String.format("Order Time : %s",OrderTime));
                    orderViewHolder.txtOrderDate.setText(String.format("Order Date : %s",OrderDate));

                    if (dialog.isShowing()){
                        dialog.dismiss();
                    }

                    orderViewHolder.bt_orderDetailsReason.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            AlertDialog.Builder builder = new AlertDialog.Builder(OrderDelivered.this)
                                    .setMessage(request.getReason())
                                    .setCancelable(false)
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                        }
                                    });
                            AlertDialog alert = builder.create();
                            alert.show();
                        }
                    });

                    orderViewHolder.bt_orderDetails.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent orderDetail = new Intent(OrderDelivered.this, OrderDetail.class);
                            orderDetail.putExtra("OrderId",OrderId)
                                    .putExtra("OrderPhone",phone)
                                    .putExtra("OrderTotal",OrderTotal)
                                    .putExtra("OrderAddress",OrderAddress)
                                    .putExtra("OrderUserName",OrderUserName)
                                    .putExtra("OrderTime",OrderTime)
                                    .putExtra("OrderDate",OrderDate)
                                    .putExtra("OrderStatus",request.getStatus())
                                    .putExtra("OrderPaymentMethod",request.getPaymentMethod());

                            startActivity(orderDetail);
                        }
                    });

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
            textView.setText("Don't Panic! You Don't Have Any Order Delivered");
        }

        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
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
