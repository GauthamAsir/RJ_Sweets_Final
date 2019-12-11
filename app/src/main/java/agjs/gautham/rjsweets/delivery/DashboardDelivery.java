package agjs.gautham.rjsweets.delivery;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import agjs.gautham.rjsweets.R;

public class DashboardDelivery extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_delivery);

        BottomNavigationView navView = findViewById(R.id.nav_view_delivery);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_delivery);

        NavigationUI.setupWithNavController(navView, navController);
    }
}
