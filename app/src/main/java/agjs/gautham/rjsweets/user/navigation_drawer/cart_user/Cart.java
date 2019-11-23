package agjs.gautham.rjsweets.user.navigation_drawer.cart_user;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import agjs.gautham.rjsweets.Common;
import agjs.gautham.rjsweets.Database.Database;
import agjs.gautham.rjsweets.Helper.RecyclerItemTouchHelper;
import agjs.gautham.rjsweets.Interface.RecyclerItemTouchHelperListener;
import agjs.gautham.rjsweets.Model.SweetOrder;
import agjs.gautham.rjsweets.R;
import agjs.gautham.rjsweets.user.Address;
import agjs.gautham.rjsweets.user.NewAddress;
import io.paperdb.Paper;

public class Cart extends Fragment implements RecyclerItemTouchHelperListener {

    private RecyclerView recyclerView;

    public static TextView txtTotalPrice;
    private List<SweetOrder> cart = new ArrayList<>();

    private TextView cartStatus;
    private Button btnPlace;

    private RelativeLayout rootLayout;
    private CartAdapter cartAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.nav_cart_user, container, false);

        rootLayout = root.findViewById(R.id.cart_container);

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

        if (Common.isConnectedToInternet(getActivity())) {

            cartStatus.setVisibility(View.GONE);
            cartStatus.setText(null);

            btnPlace.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mUser != null) {

                        if (cartAdapter.getItemCount() <= 0) {
                            Snackbar.make(getView(), "No Items in Cart", Snackbar.LENGTH_LONG).show();
                        } else {

                            String savedAddressLine1 = Paper.book().read(Common.USER_ADDRESS_LINE1);
                            String savedAddressLine2 = Paper.book().read(Common.USER_ADDRESS_LINE2);
                            String savedAddressLandmark = Paper.book().read(Common.USER_ADDRESS_LANDMARK);
                            String savedAddressPincode = Paper.book().read(Common.USER_ADDRESS_Pincode);

                            if (savedAddressLine1 != null && savedAddressLine2 != null && savedAddressLandmark != null
                                    && savedAddressPincode != null) {
                                Intent intent = new Intent(getActivity(), Address.class);
                                intent.putExtra("Price",txtTotalPrice.getText().toString());
                                startActivity(intent);
                            }else {
                                Intent intent = new Intent(getActivity(), NewAddress.class);
                                intent.putExtra("Price",txtTotalPrice.getText().toString());
                                startActivity(intent);
                            }
                        }
                    } else {
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