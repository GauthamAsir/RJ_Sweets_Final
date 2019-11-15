package agjs.gautham.rjsweets.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


import agjs.gautham.rjsweets.R;
import agjs.gautham.rjsweets.user.Dashboard;

public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar toolbar = findViewById(R.id.toolbar_login);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.admin_login:
                startActivity(new Intent(Login.this, LoginAdmin.class));
                return true;

            case R.id.delivery_login:
                startActivity(new Intent(Login.this, DeliveryLogin.class));
                return true;
        }
        return(super.onOptionsItemSelected(item));
    }

    public void bt_skip(View view){
        startActivity(new Intent(Login.this, Dashboard.class));
    }
}
