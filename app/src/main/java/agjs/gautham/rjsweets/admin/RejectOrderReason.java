package agjs.gautham.rjsweets.admin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
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
import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RejectOrderReason extends AppCompatActivity {

    TextInputLayout r;
    String order_id_value, order_status, order_p_number;

    FirebaseDatabase database;
    DatabaseReference databaseReference;

    APIService mService;

    private android.app.AlertDialog dialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reject_order_reason);

        //Init Service
        mService = Common.getFCMClient();

        dialog = new SpotsDialog.Builder()
                .setContext(RejectOrderReason.this)
                .setCancelable(false)
                .setMessage("Please wait...")
                .setTheme(R.style.DialogCustom)
                .build();


        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("Requests");

        if (getIntent() != null) {
            order_id_value = getIntent().getStringExtra("OrderId");
            order_status = getIntent().getStringExtra("OrderStatus");
            order_p_number = getIntent().getStringExtra("OrderPhNumber");
        }

        r = findViewById(R.id.delete_reason_text);

    }

    public void reject(View view){

        dialog.show();

        String reason = r.getEditText().getText().toString();

        if (!reason.isEmpty()){
            databaseReference.child(order_id_value).child("picked").setValue("0");
            databaseReference.child(order_id_value).child("pickedBy").setValue(Common.USER_Phone);
            databaseReference.child(order_id_value).child("status").setValue("3");
            databaseReference.child(order_id_value).child("reason").setValue(reason);

            sendOrderStatusToUser();

            final DatabaseReference request = FirebaseDatabase.getInstance().getReference("Requests");

            request.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    Request request1 = dataSnapshot.child(order_id_value).getValue(Request.class);

                    Common.sendMail(request1.getMail(), order_id_value, request1.getName(), request1.getDate(),
                            request1.getTotal(), request1.getPhone() , "3",
                            Common.Name + "(" + Common.USER_Phone + ")");

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });



        }else {
            dialog.dismiss();
        }

    }

    private void sendOrderStatusToUser() {

        dialog.show();

        DatabaseReference tokens = database.getReference("Tokens");
        tokens.orderByKey().equalTo(order_p_number)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnapshot:dataSnapshot.getChildren()){
                            Token token = postSnapshot.getValue(Token.class);

                            //Make New Payload
                            Notification notification = new Notification
                                    ("Your Order "+order_id_value+" is "+Common.convertCodeToStatus("3") ,"RJ Sweets Final");
                            Sender content = new Sender(token.getToken(),notification);
                            mService.sendNotification(content)
                                    .enqueue(new Callback<MyResponse>() {
                                        @Override
                                        public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                            if (response.isSuccessful()){

                                                dialog.dismiss();
                                                startActivity(new Intent(RejectOrderReason.this,DashboardAdmin.class));
                                                Log.d("Order Notification Admin","Success  "+order_p_number);
                                                Toast.makeText(RejectOrderReason.this,"Order was updated",Toast.LENGTH_LONG).show();

                                            }else {

                                                dialog.dismiss();
                                                startActivity(new Intent(RejectOrderReason.this,DashboardAdmin.class));
                                                Log.d("Order Notification Admin","Failed");
                                                Toast.makeText(RejectOrderReason.this,"Failed To send SendNotification but Order Updated",Toast.LENGTH_LONG).show();

                                            }
                                        }
                                        @Override
                                        public void onFailure(Call<MyResponse> call, Throwable t) {
                                            Log.e("ERROR",t.getMessage());
                                            dialog.dismiss();
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        dialog.dismiss();
                    }
                });
    }

    public void cancel(View view){

        startActivity(new Intent(RejectOrderReason.this,DashboardAdmin.class));
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(RejectOrderReason.this, "Back is Not Allowed", Toast.LENGTH_LONG).show();
    }
}
