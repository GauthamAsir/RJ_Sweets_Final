package agjs.gautham.rjsweets.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import agjs.gautham.rjsweets.Model.Request;
import agjs.gautham.rjsweets.R;
import agjs.gautham.rjsweets.admin.ViewHolder.OrderViewHolderAdmin;
import agjs.gautham.rjsweets.common.Common;
import dmax.dialog.SpotsDialog;

public class OrderRejectedAdmin extends AppCompatActivity {

    android.app.AlertDialog dialog;
    private DatabaseReference requests;
    private RecyclerView recyclerView;

    TextView textView;

    RecyclerView.LayoutManager layoutManager;

    private FirebaseRecyclerAdapter<Request, OrderViewHolderAdmin> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_rejected_admin);

        Toolbar toolbar = findViewById(R.id.toolbar_rejected);
        toolbar.setTitle("Rejected Order");
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

        adapter = new FirebaseRecyclerAdapter<Request, OrderViewHolderAdmin>(
                Request.class,
                R.layout.order_layout_admin,
                OrderViewHolderAdmin.class,
                requests
        ) {
            @Override
            protected void populateViewHolder(OrderViewHolderAdmin orderViewHolder, final Request request, final int i) {

                final String OrderId;

                OrderId = adapter.getRef(i).getKey();

                if (request.getStatus().equals("3")){

                    textView.setVisibility(View.GONE);

                    orderViewHolder.btndirection.setVisibility(View.GONE);

                    orderViewHolder.itemView.setVisibility(View.VISIBLE);
                    orderViewHolder.itemView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                    orderViewHolder.order_detail_container.setVisibility(View.GONE);
                    orderViewHolder.order_id_info.setVisibility(View.VISIBLE);
                    orderViewHolder.order_id_info.setText(OrderId);
                    orderViewHolder.order_id_info.setTextColor(ContextCompat.getColor(OrderRejectedAdmin.this,R.color.overlayBackground));

                    orderViewHolder.txtOrderRejectedInfo.setVisibility(View.VISIBLE);
                    orderViewHolder.txtOrderRejectedInfo.setText(String.format("Rejected By :  %s",request.getPickedBy()));

                    orderViewHolder.btndetails.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent orderDetail = new Intent(OrderRejectedAdmin.this, OrderDetailAdmin.class);
                            orderDetail.putExtra("OrderId",OrderId);
                            orderDetail.putExtra("OrderStatus",request.getStatus());
                            startActivity(orderDetail);

                        }
                    });

                    if (dialog.isShowing()){
                        dialog.dismiss();
                    }

                    //orderViewHolder.btnedit.setText("Reason");

                    /*orderViewHolder.btnedit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(OrderRejectedAdmin.this)
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
                    orderViewHolder.btndirection.setVisibility(View.GONE);*/

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
            textView.setText("Don't Panic! No Orders Have been Rejected");
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
