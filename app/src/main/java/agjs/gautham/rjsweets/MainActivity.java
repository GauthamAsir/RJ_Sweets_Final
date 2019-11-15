package agjs.gautham.rjsweets;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import agjs.gautham.rjsweets.login.Login;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //getSupportActionBar().hide();
    }

    public void bt_next(View view){
        startActivity(new Intent(MainActivity.this, Login.class));
    }
}
