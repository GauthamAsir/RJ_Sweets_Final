package agjs.gautham.rjsweets.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import agjs.gautham.rjsweets.R;
import agjs.gautham.rjsweets.admin.Dashboard;

public class LoginAdmin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_admin);
    }

    public void bt_signIn_phone(View view){
        startActivity(new Intent(LoginAdmin.this, Dashboard.class));
    }
}
