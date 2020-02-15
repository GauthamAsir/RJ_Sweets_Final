package agjs.gautham.rjsweets.user;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import agjs.gautham.rjsweets.AES;
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
import dmax.dialog.SpotsDialog;
import io.netopen.hotbitmapgg.library.view.RingProgressBar;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderPlaceStatus extends AppCompatActivity {

    private RelativeLayout parentLayout;

    private TextView orderId, username, orderTotal, pay_with, order_placed;
    private String order_number, name, order_total, pay_method, address, comment;

    private Button home;

    private List<SweetOrder> cart = new ArrayList<>();

    private FirebaseDatabase database;
    private DatabaseReference requests, sweets;

    private ProgressBar pg;

    private APIService mService;
    private FirebaseUser mUser;

    private String message = "";
    private boolean checkCart;

    private CardView progress_order;

    private String avaQuantity;
    private int finalQantity;

    private static String secret_key = "agjs04";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_order_status);

        //Firebase
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        database = FirebaseDatabase.getInstance();
        mService = Common.getFCMService();

        sweets = database.getReference("Sweets");
        requests = database.getReference("Requests");

        pg = findViewById(R.id.pg);

        progress_order = findViewById(R.id.progress_order);

        parentLayout = findViewById(R.id.container_parent);

        home = findViewById(R.id.home);

        orderId = findViewById(R.id.orderId);
        username = findViewById(R.id.username);
        orderTotal = findViewById(R.id.orderTotal);
        pay_with = findViewById(R.id.pay_with);
        order_placed = findViewById(R.id.order_placed);

        name = Common.Name;

        Log.d("lmao",String.valueOf(cart));

        if (getIntent() != null){

            order_number = getIntent().getStringExtra("OrderId");
            order_total = getIntent().getStringExtra("Price");
            pay_method = getIntent().getStringExtra("PaymentMode");
            address = getIntent().getStringExtra("Address");
            comment = getIntent().getStringExtra("Comment");

        }

        if (Common.list.size() != 0){
            cart = Common.list;
            checkCart = false;
        }else {
            cart = new Database(getApplicationContext()).getCarts(Common.USER_Phone);
            checkCart = true;
        }

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(OrderPlaceStatus.this,DashboardUser.class));
            }
        });

        init();
        makeOrder();

    }

    private void init(){

        orderId.setText(order_number);
        username.setText(name);
        orderTotal.setText(order_total);
        pay_with.setText(pay_method);

    }

    private void makeOrder() {

        Date dt = Calendar.getInstance().getTime();
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
        String orderTime = timeFormat.format(dt);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String orderDate = dateFormat.format(dt);

        Request request_1 = new Request(
                order_number,
                Common.Name,
                Common.USER_Phone,
                address,
                order_total,
                "0", //init status
                comment,
                orderTime,
                orderDate,
                "empty", //Reject Reason Initially empty
                pay_method,
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

                    avaQuantity = dataSnapshot.child("avaQuantity").getValue(String.class);


                    if (Integer.parseInt(avaQuantity) >= Integer.parseInt(orderQuantity)){

                        finalQantity = Integer.parseInt(avaQuantity) - Integer.parseInt(orderQuantity);
                        Log.d("Final Quantity", String.valueOf(finalQantity));
                        sweets.child(id).child("avaQuantity").setValue(String.valueOf(finalQantity));

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
        requests.child(order_number).setValue(request_1);

        //Delete Cart After Updating
        if (checkCart){
            new Database(getBaseContext()).cleanCart(Common.USER_Phone);
            new Database(this).close();
        }else {
            Common.list.clear();
        }

        sendNotificationorder(order_number);
        sendMailToUser();

        message = mUser.getEmail() + " ," + order_number+ " ," + Common.Name + " ," + order_total + " ,"
                + Common.convertCodeToStatus("0") ;

        progress_order.setCardBackgroundColor(getResources().getColor(R.color.progress_done));
        pg.setVisibility(View.GONE);
        order_placed.setVisibility(View.VISIBLE);
        home.setVisibility(View.VISIBLE);

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
                    Notification notification = new Notification("You Have New Order" + order_number, "RJ Sweets Final");
                    Sender content = new Sender(serverToken.getToken(), notification);
                    mService.sendNotification(content)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if (response.code() == 200 ) {

                                        if (response.isSuccessful()) {
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

    private void sendMailToUser(){

        myTask mt = new myTask();
        mt.execute();
        Log.d("Server msg", "Sent Successfully");

    }

    class myTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            try {

                Socket socket = new Socket(Common.IP, 5000);

                PrintWriter printWriter = new PrintWriter(socket.getOutputStream());

                String encrypted_message = AES.encrypt(message,secret_key);     // Using AES To Encrypt Message

                printWriter.write(encrypted_message);

                printWriter.flush();
                printWriter.close();
                socket.close();

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    @Override
    public void onBackPressed() {
        Snackbar.make(parentLayout,"Back is not Allowed",Snackbar.LENGTH_LONG).show();
    }

    private void toast(String msg){
        Toast.makeText(OrderPlaceStatus.this,msg,Toast.LENGTH_SHORT).show();
    }
}
