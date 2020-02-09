package agjs.gautham.rjsweets.user;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;

import android.os.Handler;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Map;

import agjs.gautham.rjsweets.Common;
import agjs.gautham.rjsweets.login.Login;
import agjs.gautham.rjsweets.R;
import agjs.gautham.rjsweets.user.navigation_drawer.settings_user.SettingsActivity;
import agjs.gautham.rjsweets.user.navigation_drawer.cart_user.Cart;
import agjs.gautham.rjsweets.user.navigation_drawer.home_user.Home;
import agjs.gautham.rjsweets.user.navigation_drawer.order_user.Orders;
import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;

public class DashboardUser extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private long back_pressed;
    FirebaseFirestore firestore;
    FirebaseUser mUser;

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

        if (getIntent() != null){

            String e = getIntent().getStringExtra("Execute_Settings");

            if (e != null){

                if (e.equals("1")){
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SettingsActivity.SettingsFragment()).commit();
                    getSupportActionBar().setTitle(R.string.menu_settings);
                }

            }
        }

        menu = navigationView.getMenu();

        if (Common.isConnectedToInternet(getBaseContext())) {
            Init();
        }else {
            menu.findItem(R.id.nav_login).setVisible(false);
            menu.findItem(R.id.nav_logout).setVisible(false);
            Toast.makeText(DashboardUser.this,"No Internet Connection !",Toast.LENGTH_LONG).show();
        }

    }

    public void Init(){

        if (Common.isConnectedToInternet(getBaseContext())) {

            View headerView = navigationView.getHeaderView(0);
            final TextView t1 = headerView.findViewById(R.id.header_txt);

            Typeface typeface = Typeface.createFromAsset(getAssets(),"fonts/NABILA.TTF");
            t1.setTypeface(typeface);

            firestore = FirebaseFirestore.getInstance();

            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            mUser = mAuth.getCurrentUser();

            if (mUser != null) {
                String email = mUser.getEmail();

                assert email != null;
                DocumentReference documentReference = firestore.collection("users").document(email);
                documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            assert documentSnapshot != null;
                            if (documentSnapshot.exists()) {
                                Map<String, Object> userInfo = documentSnapshot.getData();
                                assert userInfo != null;
                                String uName = (String) userInfo.get("Username");
                                String uPhone = (String) userInfo.get("Number");
                                t1.setText("Hello, " + uName);
                                Common.USER_Phone = uPhone;
                                Common.Name = uName;
                            }
                        }
                    }
                });

                menu.findItem(R.id.nav_login).setVisible(false);
            } else {
                menu.findItem(R.id.nav_logout).setVisible(false);
            }

        }else {
            Toast.makeText(DashboardUser.this,"No Internet Connection !",Toast.LENGTH_LONG).show();
        }
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
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SettingsActivity.SettingsFragment()).commit();
                getSupportActionBar().setTitle(R.string.menu_settings);
                break;

            /*case R.id.nav_credits:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Credits()).commit();
                getSupportActionBar().setTitle(R.string.menu_credits);
                break;*/

            case R.id.nav_logout:

                final FirebaseAuth mAuth;
                mAuth = FirebaseAuth.getInstance();

                final android.app.AlertDialog dialog;

                dialog = new SpotsDialog.Builder()
                        .setContext(this)
                        .setCancelable(false)
                        .setMessage("Logging You Out !...")
                        .setTheme(R.style.DialogCustom)
                        .build();

                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Log Out !");
                builder.setMessage("Do you really want to Log Out ?");
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        dialog.show();
                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                if (dialog.isShowing()){
                                    dialog.dismiss();
                                }
                                mAuth.signOut();

                                Paper.book().destroy();
                                Intent logout = new Intent(DashboardUser.this,Login.class);
                                logout.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(logout);
                                finish();
                                overridePendingTransition(R.anim.activity_close_enter, R.anim.activity_close_exit );
                            }
                        }, 3000);
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Return To Resumed Fragments
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getApplicationContext().getColor(R.color.colorText));
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getApplicationContext().getColor(R.color.colorText));
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

        //Snackbar
        Snackbar snackbar = Snackbar.make(drawer, "Press Again to Exit", Snackbar.LENGTH_SHORT);
        View snackView = snackbar.getView();
        TextView tv = snackView.findViewById(com.google.android.material.R.id.snackbar_text);
        tv.setTextColor(Color.WHITE);

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
                System.exit(1);
                android.os.Process.killProcess(android.os.Process.myPid());
            }else {
                snackbar.setText("Press Again to Exit");
                snackbar.show();
                back_pressed = System.currentTimeMillis();
            }
        }
    }
}
