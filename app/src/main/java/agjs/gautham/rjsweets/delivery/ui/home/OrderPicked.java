package agjs.gautham.rjsweets.delivery.ui.Home;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import agjs.gautham.rjsweets.Model.Request;
import agjs.gautham.rjsweets.R;
import agjs.gautham.rjsweets.common.Common;
import agjs.gautham.rjsweets.common.TrackingOrder;
import agjs.gautham.rjsweets.delivery.DashboardDelivery;
import agjs.gautham.rjsweets.delivery.OrderDetailDelivery;
import dmax.dialog.SpotsDialog;

public class OrderPicked extends AppCompatActivity {

    android.app.AlertDialog dialog;

    private FirebaseDatabase database;
    private DatabaseReference requests;
    private RecyclerView recyclerView;

    private EditText editText;
    TextView textView;

    RecyclerView.LayoutManager layoutManager;
    private FirebaseRecyclerAdapter<Request, OrderViewHolderDelivery> adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.orders_picked);

        Toolbar toolbar = findViewById(R.id.toolbar_picked);
        toolbar.setTitle("Picked Order");
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
        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Requests");

        recyclerView = findViewById(R.id.listOrders);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        ((LinearLayoutManager) layoutManager).setReverseLayout(true);
        ((LinearLayoutManager) layoutManager).setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        textView = findViewById(R.id.order_empty);

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

                final String OrderId, OrderUserName, latlng;

                OrderId = adapter.getRef(i).getKey();
                OrderUserName = request.getName();

                if (request.getStatus().equals("1") && request.getPickedBy().equals(Common.USER_Phone)){

                    textView.setVisibility(View.GONE);

                    latlng = request.getLatLng();
                    final String lat;
                    final String lng;

                    if (latlng!=null){
                        String[] spliter = latlng.split("\\(");
                        final String ll = spliter[1].replaceAll("\\)","");
                        final String[] l2 = ll.split(",");
                        lat = l2[0];
                        lng = l2[1];

                        orderViewHolder.itemView.setVisibility(View.VISIBLE);
                        orderViewHolder.itemView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                        orderViewHolder.order_detail_container.setVisibility(View.GONE);
                        orderViewHolder.order_id_info.setVisibility(View.VISIBLE);
                        orderViewHolder.order_id_info.setText(OrderId);
                        orderViewHolder.order_id_info.setTextColor(ContextCompat.getColor(OrderPicked.this,R.color.overlayBackground));

                        orderViewHolder.txtOrderRejectedInfo.setVisibility(View.VISIBLE);
                        orderViewHolder.txtOrderRejectedInfo.setText(String.format("Name :  %s",OrderUserName));

                        orderViewHolder.btndetails.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent orderDetail = new Intent(OrderPicked.this, OrderDetailDelivery.class);
                                orderDetail.putExtra("OrderId",OrderId);
                                orderDetail.putExtra("OrderStatus",request.getStatus());
                                startActivity(orderDetail);

                            }
                        });

                        orderViewHolder.btndirection.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                AlertDialog.Builder alertDialog = new AlertDialog.Builder(OrderPicked.this);
                                alertDialog.setTitle("Select an Option");

                                alertDialog.setPositiveButton("Show in Maps", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });

                                alertDialog.setNegativeButton("In App", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        Intent trackingOrder = new Intent(OrderPicked.this, TrackingOrder.class);
                                        trackingOrder.putExtra("Lat",lat);
                                        trackingOrder.putExtra("Lng",lng);
                                        startActivity(trackingOrder);

                                    }
                                });

                                AlertDialog alertDialog1 = alertDialog.create();
                                alertDialog1.show();

                                alertDialog1.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        PackageManager pm = OrderPicked.this.getPackageManager();

                                        if (isPackageInstalled("com.google.android.apps.maps",pm)){

                                            Uri gmmIntentUri = Uri.parse("google.navigation:q="+lat+","+lng+ "&mode=l");
                                            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                            mapIntent.setPackage("com.google.android.apps.maps");
                                            startActivity(mapIntent);

                                        }else {

                                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.google.android.apps.maps"));
                                            startActivity(intent);
                                            Toast.makeText(OrderPicked.this,"Please Install Google Maps",Toast.LENGTH_SHORT).show();

                                        }

                                    }
                                });
                            }
                        });

                        if (dialog.isShowing()){
                            dialog.dismiss();
                        }

                    }

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
            textView.setText("Don't Panic! No Orders Haven't Delivered");
        }

        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }

    private boolean isPackageInstalled(String packageName, PackageManager packageManager) {
        try {
            packageManager.getPackageInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(OrderPicked.this, DashboardDelivery.class));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
