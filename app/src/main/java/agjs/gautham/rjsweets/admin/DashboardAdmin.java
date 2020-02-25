package agjs.gautham.rjsweets.admin;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.Window;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import agjs.gautham.rjsweets.R;
import agjs.gautham.rjsweets.admin.navigation_drawer.add_shippers.AddShippers;
import agjs.gautham.rjsweets.admin.navigation_drawer.home.Home;
import agjs.gautham.rjsweets.admin.navigation_drawer.notification.SendNotification;
import agjs.gautham.rjsweets.admin.navigation_drawer.orders.Orders;
import agjs.gautham.rjsweets.admin.navigation_drawer.settings.Settings;
import agjs.gautham.rjsweets.admin.navigation_drawer.shippers.Shippers;
import agjs.gautham.rjsweets.login.Login;
import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;

public class DashboardAdmin extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    NavigationView navigationView;
    private long back_pressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_admin);

        Toolbar toolbar = findViewById(R.id.toolbar_admin);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout_admin);

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

    }

    public boolean onNavigationItemSelected(MenuItem item) {

        // Handle navigation view item clicks here.
        switch (item.getItemId()) {
            case R.id.nav_home:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Home()).commit();
                getSupportActionBar().setTitle(R.string.menu_home);
                break;

            case R.id.nav_notifications:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SendNotification()).commit();
                getSupportActionBar().setTitle(R.string.send_notification);
                break;

            case R.id.nav_your_orders:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Orders()).commit();
                getSupportActionBar().setTitle(R.string.menu_your_orders);
                break;

            case R.id.nav_shippers:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Shippers()).commit();
                getSupportActionBar().setTitle(R.string.shippers);
                break;

            case R.id.nav_add_shipper:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AddShippers()).commit();
                getSupportActionBar().setTitle(R.string.add_shipper);
                break;

            case R.id.nav_settings:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Settings()).commit();
                getSupportActionBar().setTitle(R.string.settings);
                break;

            case R.id.nav_logout:

                final AlertDialog dlg = new SpotsDialog.Builder()
                        .setContext(DashboardAdmin.this)
                        .setCancelable(false)
                        .setMessage("Logging You Out !...")
                        .setTheme(R.style.DialogCustom)
                        .build();

                final androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
                builder.setTitle("Log Out !");
                builder.setMessage("Do you really want to Log Out ?");

                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dlg.show();

                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                dlg.dismiss();

                                Paper.book().destroy();
                                Intent logout = new Intent(DashboardAdmin.this, Login.class);
                                logout.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(logout);
                                finish();
                            }
                        }, 1500);

                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Return To Resumed Fragments
                    }
                });

                androidx.appcompat.app.AlertDialog alertDialog = builder.create();
                alertDialog.show();

                break;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout_admin);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {

        DrawerLayout drawer = findViewById(R.id.drawer_layout_admin);
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

}
