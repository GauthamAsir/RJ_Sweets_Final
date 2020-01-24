package agjs.gautham.rjsweets.user.navigation_drawer.home_user;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.squareup.picasso.Picasso;

import java.util.Map;

import agjs.gautham.rjsweets.Common;
import agjs.gautham.rjsweets.Database.Database;
import agjs.gautham.rjsweets.Interface.ItemClickListener;
import agjs.gautham.rjsweets.Model.Sweet;
import agjs.gautham.rjsweets.Model.Token;
import agjs.gautham.rjsweets.R;
import agjs.gautham.rjsweets.SweetsDetail;
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

    private android.app.AlertDialog dialog;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        final View root = inflater.inflate(R.layout.nav_home_user, container, false);

        //Init Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
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

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if (Common.isConnectedToInternet(getActivity())) {
                    internetStatus.setVisibility(View.GONE);
                    fab.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            fabOnClick();
                        }
                    });
                }else {
                    internetStatus.setVisibility(View.VISIBLE);
                    toast("No Internet Connection");
                }

                recycler_menu = root.findViewById(R.id.recycle_menu_user);
                recycler_menu.setHasFixedSize(true);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
                recycler_menu.setLayoutManager(layoutManager);

                loadMenu(root);
            }
        });

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                if (Common.isConnectedToInternet(getActivity())){

                    fab.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            fabOnClick();
                        }
                    });

                    recycler_menu = root.findViewById(R.id.recycle_menu_user);
                    recycler_menu.setHasFixedSize(true);
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
                    recycler_menu.setLayoutManager(layoutManager);

                    //Load menu
                    loadMenu(root);
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

    private void loadMenu(View root) {

        internetStatus = root.findViewById(R.id.no_internet_user);

        if (Common.isConnectedToInternet(getActivity())) {

            internetStatus.setVisibility(View.GONE);

            dialog.show();
            adapter = new FirebaseRecyclerAdapter<Sweet, MenuViewHolder>
                    (Sweet.class, R.layout.menu_item, MenuViewHolder.class, sweets) {
                @Override
                protected void populateViewHolder(final MenuViewHolder menuViewHolder, final Sweet model, final int i) {

                    menuViewHolder.txtMenuName.setText(model.getName());
                    Picasso.get().load(model.getImage())
                            .into(menuViewHolder.imageView);

                    if (dialog.isShowing()){
                        dialog.dismiss();
                    }

                    if (model.getAvaQuantity().equals("0")){
                        menuViewHolder.txtAvailableQuantity.setVisibility(View.VISIBLE);
                    }else {
                        menuViewHolder.txtAvailableQuantity.setVisibility(View.GONE);
                    }

                    menuViewHolder.setItemClickListener(new ItemClickListener() {
                        @Override
                        public void onClick(View view, final int position, boolean isLongClick) {
                            Intent sweetsDetail = new Intent(getActivity(), SweetsDetail.class);
                            sweetsDetail.putExtra("SweetId", adapter.getRef(position).getKey());
                            sweetsDetail.putExtra("AvailableQuantity",model.getAvaQuantity());

                            startActivity(sweetsDetail);
                            getActivity().overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
                        }
                    });
                }
            };

            fab.setCount(new Database(getActivity()).getCountCart());

            recycler_menu.setAdapter(adapter);
        }else {
            internetStatus.setVisibility(View.VISIBLE);
            if (dialog.isShowing()){
                dialog.dismiss();
            }
            toast("No Internet Connection");
        }
        swipeRefreshLayout.setRefreshing(false);
    }

    private void toast(String msg){
        Toast.makeText(getActivity(),msg,Toast.LENGTH_LONG).show();
    }
}