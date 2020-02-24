package agjs.gautham.rjsweets;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import agjs.gautham.rjsweets.login.Login;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView imageView = findViewById(R.id.img);
        TextView textView = findViewById(R.id.welcome);
        TextView textView1 = findViewById(R.id.welcome1);
        Button button = findViewById(R.id.next);
        Animation fadein = AnimationUtils.loadAnimation(MainActivity.this, R.anim.fadein);

        imageView.startAnimation(fadein);
        textView.startAnimation(fadein);
        textView1.startAnimation(fadein);
        button.startAnimation(fadein);

    }

    public void bt_next(View view){
        startActivity(new Intent(MainActivity.this, Login.class));
        Common.intentOpenAnimation(MainActivity.this);
    }
}
