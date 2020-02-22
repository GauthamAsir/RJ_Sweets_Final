package agjs.gautham.rjsweets.user.navigation_drawer.order_user;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Objects;

import agjs.gautham.rjsweets.Common;
import agjs.gautham.rjsweets.Model.Request;
import agjs.gautham.rjsweets.Model.SweetOrder;
import agjs.gautham.rjsweets.R;
import agjs.gautham.rjsweets.user.DashboardUser;
import agjs.gautham.rjsweets.user.navigation_drawer.settings_user.SettingsActivity;
import dmax.dialog.SpotsDialog;

public class OrderDetail extends AppCompatActivity {

    TextView order_id, order_status, order_total,user_name, order_time, order_date
            ,orderaddress_line1, orderPaymentMethod;

    NestedScrollView scrollView;

    String order_id_value="";
    RecyclerView lstSweets;
    RecyclerView.LayoutManager layoutManager;
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    Button cancelOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        scrollView = findViewById(R.id.scrollView);
        scrollView.setSmoothScrollingEnabled(true);

        order_id = findViewById(R.id.order_id);

        order_status = findViewById(R.id.order_status);
        order_total = findViewById(R.id.order_total);

        user_name = findViewById(R.id.user_name);
        order_date = findViewById(R.id.order_date);
        order_time = findViewById(R.id.order_time);

        orderPaymentMethod = findViewById(R.id.payment_method);

        orderaddress_line1 = findViewById(R.id.address_line);

        lstSweets = findViewById(R.id.listsweets);
        lstSweets.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        lstSweets.setLayoutManager(layoutManager);

        if (getIntent() != null){

            order_id_value = getIntent().getStringExtra("OrderId");
            String orderTotal = getIntent().getStringExtra("OrderTotal");
            String orderAddress = getIntent().getStringExtra("OrderAddress");
            String orderName = getIntent().getStringExtra("OrderUserName");
            String orderTime = getIntent().getStringExtra("OrderTime");
            String orderDate = getIntent().getStringExtra("OrderDate");
            final String orderStatus = getIntent().getStringExtra("OrderStatus");
            String paymentMethod = getIntent().getStringExtra("OrderPaymentMethod");

            //Set Value to recycler view
            order_id.setText(String.format("Order Id : %s",order_id_value));

            order_total.setText(String.format("%s Rs",orderTotal));
            order_status.setText(Common.convertCodeToStatus(orderStatus));

            user_name.setText(String.format(" %s,",orderName));
            order_time.setText(orderTime);
            order_date.setText(orderDate);

            String addrs = orderAddress.replace(",",",\n");

            orderaddress_line1.setText(String.format(" %s",addrs));
            orderPaymentMethod.setText(paymentMethod);

            database = FirebaseDatabase.getInstance();
            databaseReference = database.getReference("Requests");

            cancelOrder = findViewById(R.id.btnCancelOrder);

            if (orderStatus.equals("2") | orderStatus.equals("3")){
                cancelOrder.setVisibility(View.GONE);
            }else {
                cancelOrder.setVisibility(View.VISIBLE);
            }

            cancelOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (orderStatus.equals("1")){

                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(OrderDetail.this);
                        alertDialog.setTitle("Error");
                        alertDialog.setMessage("This Order can't be cancelled, as your order is out for delivery. Please contact support for more details");
                        alertDialog.setIcon(R.drawable.ic_error_outline);
                        alertDialog.setCancelable(false);

                        alertDialog.setPositiveButton("Support", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SettingsActivity.SettingsFragment()).commit();
                                getSupportActionBar().setTitle(R.string.menu_settings);

                            }
                        }).setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                        final AlertDialog dialog = alertDialog.create();
                        dialog.show();

                    } else {

                         final AlertDialog pdialog = new SpotsDialog.Builder()
                                .setContext(OrderDetail.this)
                                .setCancelable(false)
                                .setMessage("Cancelling Your Order ...")
                                .setTheme(R.style.DialogCustom)
                                .build();

                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(OrderDetail.this);
                        alertDialog.setTitle("Confirmation");
                        alertDialog.setMessage("Do You Really Want To Cancel This Order. This can't be undone");
                        alertDialog.setIcon(R.drawable.ic_error_outline);
                        alertDialog.setCancelable(false);

                        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                pdialog.show();

                                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        Request request = dataSnapshot.child(order_id_value).getValue(Request.class);

                                        List list = request.getSweetOrders();

                                        List<SweetOrder> myOrders = list;

                                        for (int i = 0; i < myOrders.size(); i++){

                                            SweetOrder sweetOrder = myOrders.get(i);

                                            final String pid = sweetOrder.getProductId();

                                            final String Orderquantity = sweetOrder.getQuantity();

                                            final DatabaseReference databaseReference1 = database.getReference("Sweets");

                                            databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                    String avaQuantity = dataSnapshot.child(pid).child("avaQuantity").getValue(String.class);

                                                    int finalQuantity = Integer.parseInt(Orderquantity) + Integer.parseInt(Objects.requireNonNull(avaQuantity));

                                                    databaseReference1.child(pid).child("avaQuantity").setValue(String.valueOf(finalQuantity));

                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });
                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                                if (pdialog.isShowing()){
                                    pdialog.dismiss();
                                }

                                databaseReference.child(order_id_value).child("status").setValue("3");

                                databaseReference.child(order_id_value).child("reason").setValue("Cancelled by user " + Common.Name + "(" +Common.USER_Phone+")");

                                Toast.makeText(OrderDetail.this,"Order Cancelled Successfully",Toast.LENGTH_LONG).show();

                                startActivity(new Intent(OrderDetail.this, DashboardUser.class));

                            }
                        });

                        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                dialog.dismiss();

                            }
                        });

                        final AlertDialog dialog = alertDialog.create();
                        dialog.show();

                    }
                }
            });

            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Request request = dataSnapshot.child(order_id_value).getValue(Request.class);
                    OrderDetailAdapter adapter = new OrderDetailAdapter(request.getSweetOrders());
                    adapter.notifyDataSetChanged();
                    lstSweets.setAdapter(adapter);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.activity_close_enter, R.anim.activity_close_exit );
    }
}
