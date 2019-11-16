package agjs.gautham.rjsweets.delivery;

import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Window;
import android.widget.Toast;

import agjs.gautham.rjsweets.R;
import agjs.gautham.rjsweets.admin.navigation_drawer.home.Home;

public class DashboardDelivery extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    NavigationView navigationView;
    private long back_pressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_delivery);

        Toolbar toolbar = findViewById(R.id.toolbar_delivery);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout_delivery);

        Window window = getWindow();
        window.setStatusBarColor(getResources().getColor(R.color.overlayBackground));

        getSupportActionBar().setTitle(R.string.menu_home);

        navigationView = findViewById(R.id.nav_view_delivery);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_delivery, new Home()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }
    }

    @Override
    public void onBackPressed() {

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (!navigationView.getMenu().findItem(R.id.nav_home).isChecked()){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Home()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
            getSupportActionBar().setTitle(R.string.menu_home);
        }
        else {
            if (back_pressed + 2000 > System.currentTimeMillis()){
                finish();
                moveTaskToBack(true);
            }else {
                Snackbar.make(drawer, "Press Again to Exit", Snackbar.LENGTH_LONG).show();
                back_pressed = System.currentTimeMillis();
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        if (menuItem.getItemId() == R.id.nav_logout){

            Toast.makeText(DashboardDelivery.this,"W.I.P LogOut",Toast.LENGTH_LONG).show();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_delivery, new Home()).commit();
            getSupportActionBar().setTitle(R.string.menu_home);

        }else {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_delivery, new Home()).commit();
            getSupportActionBar().setTitle(R.string.menu_home);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout_delivery);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
