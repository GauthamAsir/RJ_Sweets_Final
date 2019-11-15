package agjs.gautham.rjsweets.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import agjs.gautham.rjsweets.R;
import agjs.gautham.rjsweets.delivery.Dashboard;

public class DeliveryLogin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_login);
    }

    public void bt_signIn_phone(View view){
        startActivity(new Intent(DeliveryLogin.this, Dashboard.class));
    }
}
