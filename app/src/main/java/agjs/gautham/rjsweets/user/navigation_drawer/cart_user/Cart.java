package agjs.gautham.rjsweets.user.navigation_drawer.cart_user;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import agjs.gautham.rjsweets.Database.Database;
import agjs.gautham.rjsweets.Helper.RecyclerItemTouchHelper;
import agjs.gautham.rjsweets.Interface.RecyclerItemTouchHelperListener;
import agjs.gautham.rjsweets.Model.SweetOrder;
import agjs.gautham.rjsweets.R;
import agjs.gautham.rjsweets.common.Common;
import agjs.gautham.rjsweets.user.PlaceOrder;
import io.paperdb.Paper;

public class Cart extends Fragment implements RecyclerItemTouchHelperListener {

    private RecyclerView recyclerView;

    public static TextView txtTotalPrice;
    private List<SweetOrder> cart = new ArrayList<>();

    AlertDialog.Builder builder;
    private View view1;

    private TextView cartStatus;
    private Button btnPlace;

    private RelativeLayout rootLayout;
    private CartAdapter cartAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.nav_cart_user, container, false);

        rootLayout = root.findViewById(R.id.cart_container);

        Places.initialize(getActivity().getApplicationContext(), "AIzaSyAfpdkEt00wmFp8DZVhqqnqG3JpQB880mM");
        final PlacesClient placesClient = Places.createClient(getActivity());

        view1 = inflater.inflate(R.layout.address_places,null);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final FirebaseUser mUser = mAuth.getCurrentUser();

        //Init
        recyclerView = root.findViewById(R.id.listCart);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

        cartStatus = root.findViewById(R.id.cart_status);

        //Swipe Left To Delete
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);

        txtTotalPrice = root.findViewById(R.id.total);
        btnPlace = root.findViewById(R.id.btnPlaceOrder);

        builder = new AlertDialog.Builder(getActivity());

        if (Common.isConnectedToInternet(getActivity())) {

            cartStatus.setVisibility(View.GONE);
            cartStatus.setText(null);

            btnPlace.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    if (mUser != null) {

                        if (cartAdapter.getItemCount() <= 0) {
                            Snackbar snackbar1 = Snackbar.make(getView(), "Press Again to Exit", Snackbar.LENGTH_SHORT);
                            View snackView = snackbar1.getView();
                            TextView tv = snackView.findViewById(com.google.android.material.R.id.snackbar_text);
                            tv.setTextColor(Color.WHITE);
                            snackbar1.setText("No Items in Cart");
                            snackbar1.show();
                        } else {

                            builder.setTitle("Find Places");
                            builder.setCancelable(false);
                            builder.setView(view1);
                            builder.setIcon(R.drawable.ic_place_black_24dp);

                            final String savedAddress = Paper.book().read(Common.USER_ADDRESS_SAVED);

                            String price = txtTotalPrice.getText().toString();
                            String price2 = price.replace("₹","");

                            final String final_price = price2.replaceAll("\\s+","");

                            if (savedAddress != null){

                                final AlertDialog.Builder savedAddresBuilder = new AlertDialog.Builder(getActivity());
                                savedAddresBuilder.setTitle("Saved Address");

                                final View view2 = LayoutInflater.from(getActivity())
                                        .inflate(R.layout.saved_address, null);

                                savedAddresBuilder.setView(view2);

                                TextView addrs = view2.findViewById(R.id.addrs);
                                ImageView delete_addrs = view2.findViewById(R.id.delete_adrs);
                                ImageView new_adrs = view2.findViewById(R.id.new_adrs);
                                Button select_adrs = view2.findViewById(R.id.select_adrs);

                                String a = savedAddress.replaceAll("\\s+","");
                                String address = a.replace(",",",\n");
                                addrs.setText(address);

                                final AlertDialog alertDialog = savedAddresBuilder.create();

                                alertDialog.show();

                                delete_addrs.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        Paper.book().delete(Common.USER_ADDRESS_SAVED);
                                        alertDialog.dismiss();

                                    }
                                });

                                new_adrs.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        if (view1 != null){

                                            ViewGroup parent = (ViewGroup) view1.getParent();
                                            if (parent!= null){
                                                parent.removeView(view1);
                                            }

                                        }

                                        builder.setView(view1);
                                        buy_now(final_price);

                                    }
                                });

                                select_adrs.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        Intent placeOrder = new Intent(getActivity(), PlaceOrder.class);
                                        placeOrder.putExtra("Price",final_price);
                                        placeOrder.putExtra("Address",savedAddress);
                                        Common.intentOpenAnimation(getActivity());
                                        alertDialog.dismiss();
                                        startActivity(placeOrder);

                                    }
                                });

                            }
                            else{

                                if (view1 != null){

                                    ViewGroup parent = (ViewGroup) view1.getParent();
                                    if (parent!= null){
                                        parent.removeView(view1);
                                    }

                                }
                                buy_now(final_price);

                            }
                        }
                    }
                    else {
                        Snackbar.make(getView(), "You need to Login To Place Order, Please Login", Snackbar.LENGTH_LONG).show();
                    }
                }
            });

            loadSweetList();

        } else {
            cartStatus.setVisibility(View.VISIBLE);
            cartStatus.setText(R.string.no_internet);
            btnPlace.setClickable(false);
            toast("No Internet Connection");
        }

        return root;
    }

    private void buy_now(final String price) {

        final TextView enter_address = view1.findViewById(R.id.enter_address);
        final CheckBox checkBox = view1.findViewById(R.id.saveAddress);

        List<Place.Field> fields1 = Arrays.asList(Place.Field.ADDRESS, Place.Field.NAME,
                Place.Field.ID, Place.Field.LAT_LNG);

        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                (getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment));

        autocompleteFragment.setPlaceFields(fields1);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {

                enter_address.setText(place.getAddress());

            }

            @Override
            public void onError(@NonNull Status status) {

                Log.d("Error", status.toString());

            }
        });

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        final AlertDialog alertDialog = builder.create();

        alertDialog.show();

        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                builder.setView(null);

            }
        });

        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (enter_address!=null){

                    Intent placeOrder = new Intent(getActivity(), PlaceOrder.class);
                    placeOrder.putExtra("Price",price);
                    placeOrder.putExtra("Address",enter_address.getText().toString());

                    if (checkBox.isChecked())
                        Paper.book().write(Common.USER_ADDRESS_SAVED,enter_address.getText().toString());

                    alertDialog.dismiss();
                    Common.intentOpenAnimation(getActivity());
                    startActivity(placeOrder);

                }

            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void loadSweetList() {

        cart = new Database(getActivity().getApplicationContext()).getCarts(Common.USER_Phone);

        cartAdapter = new CartAdapter(cart, getActivity());
        cartAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(cartAdapter);

        btnPlace = getActivity().findViewById(R.id.btnPlaceOrder);
        cartStatus = getActivity().findViewById(R.id.cart_status);

        //Calculate Total Price
        int total = 0;
        for (SweetOrder sweetOrder : cart) {
            total += (Integer.parseInt(sweetOrder.getPrice())) * (Integer.parseInt(sweetOrder.getQuantity()));
        }
        Locale locale = new Locale("en", "IN");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
        txtTotalPrice.setText(fmt.format(total));

    }

    private void toast(String msg){
        Toast.makeText(getActivity(),msg,Toast.LENGTH_LONG).show();
    }

    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof CartViewHolder){
            String name = ((CartAdapter)recyclerView.getAdapter()).getItem(viewHolder.getAdapterPosition()).getProductName();

            final SweetOrder deleteItem = ((CartAdapter)recyclerView.getAdapter()).getItem(viewHolder.getAdapterPosition());
            final int deleteIndex = viewHolder.getAdapterPosition();

            cartAdapter.removeItem(deleteIndex);
            new Database(getActivity().getBaseContext()).removeFromCart(deleteItem.getProductId());

            //Update Text Total
            //Calculate Total Price
            int total = 0;
            for (SweetOrder sweetOrder : cart) {
                total += (Integer.parseInt(sweetOrder.getPrice())) * (Integer.parseInt(sweetOrder.getQuantity()));
            }
            Locale locale = new Locale("en", "IN");
            NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
            txtTotalPrice.setText(fmt.format(total));

            Snackbar snackbar = Snackbar.make(rootLayout,name+" removed from cart! ", Snackbar.LENGTH_LONG);
            View snackView = snackbar.getView();
            TextView tv = snackView.findViewById(com.google.android.material.R.id.snackbar_text);
            tv.setTextColor(Color.WHITE);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cartAdapter.restoreItem(deleteItem, deleteIndex);
                    new Database(getActivity().getBaseContext()).addToCart(deleteItem);

                    //Update Text Total
                    //Calculate Total Price
                    int total2 = 0;
                    for (SweetOrder sweetOrder : cart) {
                        total2 += (Integer.parseInt(sweetOrder.getPrice())) * (Integer.parseInt(sweetOrder.getQuantity()));
                    }
                    Locale locale = new Locale("en", "IN");
                    NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
                    txtTotalPrice.setText(fmt.format(total2));
                    loadSweetList();
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }
    }

}