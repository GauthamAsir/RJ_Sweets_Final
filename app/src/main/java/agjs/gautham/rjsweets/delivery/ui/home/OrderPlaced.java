package agjs.gautham.rjsweets.delivery.ui.Home;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import agjs.gautham.rjsweets.Model.Request;
import agjs.gautham.rjsweets.R;
import agjs.gautham.rjsweets.Remote.APIService;
import agjs.gautham.rjsweets.common.Common;
import agjs.gautham.rjsweets.delivery.OrderDetailDelivery;
import dmax.dialog.SpotsDialog;

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
                R.layout.order_layout_delivery,
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

                    orderViewHolder.itemView.setVisibility(View.VISIBLE);
                    orderViewHolder.itemView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                    orderViewHolder.txtOrderId.setText(String.format("Order Id :  %s",OrderId));
                    orderViewHolder.txtOrderStatus.setText(String.format("Order Status :  %s",OrderStatus));
                    orderViewHolder.txtOrderAddress.setText(String.format("Address : %s",request.getAddress()));
                    orderViewHolder.txtOrderPhone.setText(String.format("Phone : %s",request.getPhone()));
                    orderViewHolder.txtUsername.setText(String.format("Username : %s",request.getName()));

                    if (dialog.isShowing()){
                        dialog.dismiss();
                    }

                    orderViewHolder.btndetails.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent orderDetail = new Intent(OrderPlaced.this, OrderDetailDelivery.class);
                            orderDetail.putExtra("OrderId",adapter.getRef(i).getKey());
                            orderDetail.putExtra("OrderStatus",request.getStatus());
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
            textView.setText("Don't Panic! You Don't No Orders Have been  Placed");
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
