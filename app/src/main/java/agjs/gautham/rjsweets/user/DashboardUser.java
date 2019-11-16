package agjs.gautham.rjsweets.user;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import android.view.MenuItem;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.Window;
import android.widget.Toast;

import agjs.gautham.rjsweets.login.Login;
import agjs.gautham.rjsweets.R;
import agjs.gautham.rjsweets.user.navigation_drawer.cart_user.Cart;
import agjs.gautham.rjsweets.user.navigation_drawer.home_user.Home;
import agjs.gautham.rjsweets.user.navigation_drawer.order_user.Orders;
import agjs.gautham.rjsweets.user.navigation_drawer.settings_user.Settings;

public class DashboardUser extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private long back_pressed;

    NavigationView navigationView;
    Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        Window window = getWindow();
        window.setStatusBarColor(getResources().getColor(R.color.overlayBackground));

        getSupportActionBar().setTitle(R.string.menu_home);

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Home()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }

        menu = navigationView.getMenu();

    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        // Handle navigation view item clicks here.
        switch (item.getItemId()){
            case R.id.nav_home:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Home()).commit();
                getSupportActionBar().setTitle(R.string.menu_home);
                break;

            case R.id.nav_your_cart:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Cart()).commit();
                getSupportActionBar().setTitle(R.string.menu_cart);
                break;

            case R.id.nav_your_orders:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Orders()).commit();
                getSupportActionBar().setTitle(R.string.menu_your_orders);
                break;

            case R.id.nav_settings:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Settings()).commit();
                getSupportActionBar().setTitle(R.string.menu_settings);
                break;

            /*case R.id.nav_credits:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Credits()).commit();
                getSupportActionBar().setTitle(R.string.menu_credits);
                break;*/

            case R.id.nav_logout:

                toast("Log-Out");
                break;

            case R.id.nav_login:
                startActivity(new Intent(DashboardUser.this, Login.class));
                finish();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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

    private void toast(String msg){
        Toast.makeText(DashboardUser.this,msg,Toast.LENGTH_LONG).show();
    }
}
