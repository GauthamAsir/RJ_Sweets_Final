package agjs.gautham.rjsweets.user.navigation_drawer.home_user;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.andremion.counterfab.CounterFab;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.squareup.picasso.Picasso;

import java.util.Map;

import agjs.gautham.rjsweets.Database.Database;
import agjs.gautham.rjsweets.Interface.ItemClickListener;
import agjs.gautham.rjsweets.Model.Flags;
import agjs.gautham.rjsweets.Model.Sweet;
import agjs.gautham.rjsweets.Model.Token;
import agjs.gautham.rjsweets.R;
import agjs.gautham.rjsweets.common.Common;
import agjs.gautham.rjsweets.common.SweetsDetail;
import agjs.gautham.rjsweets.user.navigation_drawer.cart_user.Cart;
import dmax.dialog.SpotsDialog;

public class Home extends Fragment {

    private DatabaseReference sweets;

    private RecyclerView recycler_menu;

    private FirebaseRecyclerAdapter<Sweet, MenuViewHolder> adapter;

    private TextView internetStatus;

    private SwipeRefreshLayout swipeRefreshLayout;

    private CounterFab fab;

    private FirebaseUser mUser;

    private Handler handler = new Handler();
    private View root;

    private android.app.AlertDialog dialog;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.nav_home_user, container, false);

        //Init Firebase
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        sweets = database.getReference("Sweets");
        internetStatus = root.findViewById(R.id.no_internet_user);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        //Init Progress Dialog
        dialog = new SpotsDialog.Builder()
                .setContext(getActivity())
                .setCancelable(false)
                .setMessage("Loading Sweets for You...")
                .setTheme(R.style.DialogCustom)
                .build();

        //View
        swipeRefreshLayout = root.findViewById(R.id.swipe_layout);

        swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(getActivity(),R.color.colorPrimary)
                ,ContextCompat.getColor(getActivity(),android.R.color.holo_green_dark),
                ContextCompat.getColor(getActivity(),android.R.color.holo_orange_dark),
                ContextCompat.getColor(getActivity(),android.R.color.holo_blue_dark));

        fab = root.findViewById(R.id.btn_cart_user);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabOnClick();
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if (Common.isConnectedToInternet(getActivity())) {
                    internetStatus.setVisibility(View.GONE);
                }else {
                    internetStatus.setVisibility(View.VISIBLE);
                    toast("No Internet Connection",Toast.LENGTH_SHORT);
                }

                recycler_menu = root.findViewById(R.id.recycle_menu_user);
                recycler_menu.setHasFixedSize(true);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
                recycler_menu.setLayoutManager(layoutManager);

                loadMenu();
            }
        });

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                if (Common.isConnectedToInternet(getActivity())){

                    recycler_menu = root.findViewById(R.id.recycle_menu_user);
                    recycler_menu.setHasFixedSize(true);
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
                    recycler_menu.setLayoutManager(layoutManager);

                    //Load menu
                    loadMenu();
                }
            }
        });

        //Update Token
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(getActivity(), new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String tokenRefreshed = instanceIdResult.getToken();
                updateToken(tokenRefreshed);
            }
        });

        return root;

    }

    @Override
    public void onResume() {
        super.onResume();
        fab.setCount(new Database(getActivity()).getCountCart());
    }

    private void fabOnClick() {

        NavigationView navigationView;
        navigationView = getActivity().findViewById(R.id.nav_view);
        FragmentManager manager = getActivity().getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.fragment_container, new Cart()).commit();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.menu_cart);
        navigationView.setCheckedItem(R.id.nav_your_cart);
    }

    private void updateToken(final String token) {

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

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
                            FirebaseDatabase db = FirebaseDatabase.getInstance();
                            DatabaseReference tokens = db.getReference("Tokens");

                            String uPhone = (String) userInfo.get("Number");
                            Common.USER_Phone = uPhone;

                            Token data = new Token(token, false); //Sending From Client App So False
                            tokens.child(uPhone).setValue(data);
                        }
                    }
                }
            });
        }
    }

    private void loadMenu() {

        internetStatus = root.findViewById(R.id.no_internet_user);

        if (Common.isConnectedToInternet(getActivity())) {

            recycler_menu.setVisibility(View.VISIBLE);
            internetStatus.setVisibility(View.GONE);
            loadFlags loadFlags = new loadFlags();
            loadFlags.execute();
            dialog.show();

            new Thread(new Runnable() {
                @Override
                public void run() {

                    adapter = new FirebaseRecyclerAdapter<Sweet, MenuViewHolder>
                            (Sweet.class, R.layout.menu_item, MenuViewHolder.class, sweets) {
                        @Override
                        protected void populateViewHolder(final MenuViewHolder menuViewHolder, final Sweet model, final int i) {

                            menuViewHolder.txtMenuName.setText(model.getName());
                            Picasso.get().load(model.getImage())
                                    .into(menuViewHolder.imageView);

                            if (model.getAvaQuantity().equals("0")){
                                menuViewHolder.txtAvailableQuantity.setVisibility(View.VISIBLE);
                                menuViewHolder.discount_badge.setVisibility(View.GONE);
                            }else {
                                menuViewHolder.txtAvailableQuantity.setVisibility(View.GONE);
                                menuViewHolder.discount_badge.setVisibility(View.VISIBLE);
                            }

                            if (!model.getDiscount().equals("0")){

                                menuViewHolder.discount_badge.setVisibility(View.VISIBLE);
                                switch (model.getDiscount()){

                                    case "10":
                                        Picasso.get().load(R.drawable.badge_10).into(menuViewHolder.discount_badge);
                                        break;

                                    case "20":
                                        Picasso.get().load(R.drawable.badge_20).into(menuViewHolder.discount_badge);
                                        break;

                                    case "30":
                                        Picasso.get().load(R.drawable.badge_30).into(menuViewHolder.discount_badge);
                                        break;

                                    case "40":
                                        Picasso.get().load(R.drawable.badge_40).into(menuViewHolder.discount_badge);
                                        break;

                                    case "50":
                                        Picasso.get().load(R.drawable.badge_50).into(menuViewHolder.discount_badge);
                                        break;

                                    case "60":
                                        Picasso.get().load(R.drawable.badge_60).into(menuViewHolder.discount_badge);
                                        break;

                                    case "70":
                                        Picasso.get().load(R.drawable.badge_70).into(menuViewHolder.discount_badge);
                                        break;

                                    case "80":
                                        Picasso.get().load(R.drawable.badge_80).into(menuViewHolder.discount_badge);
                                        break;

                                    case "90":
                                        Picasso.get().load(R.drawable.badge_90).into(menuViewHolder.discount_badge);
                                        break;

                                    default:
                                        menuViewHolder.discount_badge.setVisibility(View.GONE);
                                        break;

                                }

                                if (model.getAvaQuantity().equals("0")){
                                    menuViewHolder.txtAvailableQuantity.setVisibility(View.VISIBLE);
                                    menuViewHolder.discount_badge.setVisibility(View.GONE);
                                }else {
                                    menuViewHolder.txtAvailableQuantity.setVisibility(View.GONE);
                                    menuViewHolder.discount_badge.setVisibility(View.VISIBLE);
                                }

                            }else {
                                menuViewHolder.discount_badge.setVisibility(View.GONE);
                            }

                            if (dialog.isShowing()){
                                dialog.dismiss();
                            }

                            menuViewHolder.root.setAnimation(AnimationUtils.loadAnimation(getActivity(),R.anim.fade_scale_transmission));

                            menuViewHolder.setItemClickListener(new ItemClickListener() {
                                @Override
                                public void onClick(View view, final int position, boolean isLongClick) {
                                    Intent sweetsDetail = new Intent(getActivity(), SweetsDetail.class);
                                    sweetsDetail.putExtra("SweetId", adapter.getRef(position).getKey());
                                    sweetsDetail.putExtra("AvailableQuantity",model.getAvaQuantity());

                                    Common.intentOpenAnimation(getActivity());
                                    startActivity(sweetsDetail);
                                    getActivity().overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
                                }
                            });

                        }
                    };

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            fab.setCount(new Database(getActivity()).getCountCart());
                            recycler_menu.setHasFixedSize(true);
                            recycler_menu.setAdapter(adapter);
                        }
                    });

                }
            }).start();
        }else {
            recycler_menu.setVisibility(View.GONE);
            internetStatus.setVisibility(View.VISIBLE);
            if (dialog.isShowing()){
                dialog.dismiss();
            }
            toast("No Internet Connection",Toast.LENGTH_SHORT);
        }
        swipeRefreshLayout.setRefreshing(false);
    }

    private class loadFlags extends AsyncTask{

        @Override
        protected Object doInBackground(Object[] objects) {

            // Show A Reason For Not Accepting Orders like Server Under Maintainence or due to COVID-19
            DatabaseReference flags = FirebaseDatabase.getInstance().getReference("flags");
            flags.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Common.flags = dataSnapshot.getValue(Flags.class);

                    TextView make_no_orders = root.findViewById(R.id.make_no_order_reason);
                    if (Common.flags.isMakeOrders()){
                        make_no_orders.setVisibility(View.GONE);
                    }else {
                        make_no_orders.setText(Common.flags.getMakeOrdersReason());
                        make_no_orders.setVisibility(View.VISIBLE);
                        make_no_orders.setSelected(true);
                    }

                    if (Common.flags.getMakeOrdersReason().equals("default")){
                        make_no_orders.setVisibility(View.GONE);
                    }else {
                        make_no_orders.setText(Common.flags.getMakeOrdersReason());
                        make_no_orders.setVisibility(View.VISIBLE);
                        make_no_orders.setSelected(true);
                    }

                    make_no_orders.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            AlertDialog.Builder builder_no_order = new  AlertDialog.Builder(getActivity());
                            builder_no_order.setTitle("Message from server");
                            builder_no_order.setIcon(R.drawable.ic_error_outline);
                            builder_no_order.setMessage(Common.flags.getMakeOrdersReason());

                            builder_no_order.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            builder_no_order.show();
                        }
                    });

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            return null;
        }
    }

    private void toast(String msg, int length){
        Toast.makeText(getActivity(),msg,length).show();
    }
}