package agjs.gautham.rjsweets.user.navigation_drawer.cart_user;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import agjs.gautham.rjsweets.Database.Database;
import agjs.gautham.rjsweets.Model.SweetOrder;
import agjs.gautham.rjsweets.R;
import agjs.gautham.rjsweets.common.Common;

public class CartAdapter extends RecyclerView.Adapter<CartViewHolder>{

    private List<SweetOrder> listData;
    private Context context;

    public CartAdapter(List<SweetOrder> listData, Context context) {
        this.listData = listData;
        this.context = context;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.cart_layout,parent,false);
        return new CartViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final CartViewHolder holder, final int position) {

        Picasso.get().load(listData.get(position).getImage())
                .resize(70,70)  //70dp
                .centerCrop()
                .into(holder.cart_image);

        holder.bt_quantity.setNumber(listData.get(position).getQuantity());

        if (!listData.get(position).getDiscount().equals("0")){

            holder.discount_tv.setText(String.format("%s%% Discount Applied",listData.get(position).getDiscount()));
            holder.discount_tv.setBackground(context.getResources().getDrawable(R.drawable.button_background_dark_green));

        }else {

            holder.discount_tv.setText("No Discount Applied");
            holder.discount_tv.setBackground(context.getResources().getDrawable(R.drawable.button_background_dark_green));

        }

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference sweets = database.getReference("Sweets");
        final List<String> q = new ArrayList<>();

        holder.root_cart.setAnimation(AnimationUtils.loadAnimation(context,R.anim.fade_transmission));

        final String pos = String.valueOf(listData.get(position).getProductId());

        sweets.child(pos).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                final int maxRange = Integer.parseInt(Objects.requireNonNull(dataSnapshot.child("avaQuantity").getValue(String.class)));
                holder.bt_quantity.setRange(1,maxRange);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        holder.delete_f.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final SweetOrder sweetOrder = listData.get(position);
                listData.remove(position);
                new Database(context).removeFromCart(sweetOrder.getProductId());

                //Calculate Total Price
                double total = 0;
                for (SweetOrder sweetOrder1 : listData) {

                    String product_price = sweetOrder1.getPrice();
                    String order_quan = sweetOrder1.getQuantity();
                    String product_dis = sweetOrder1.getDiscount();

                    int final_price = Integer.parseInt(product_price) * Integer.parseInt(order_quan);

                    final double dis = final_price * (Double.parseDouble(product_dis) / 100);
                    final double dis_pirce = final_price - dis;

                    total += dis_pirce;
                }
                Locale locale = new Locale("en", "IN");
                NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
                Cart.txtTotalPrice.setText(fmt.format(total));

                notifyDataSetChanged();

            }
        });

        holder.bt_quantity.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
            @Override
            public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {
                final SweetOrder sweetOrder = listData.get(position);
                sweetOrder.setQuantity(String.valueOf(newValue));
                new Database(context).updateCart(sweetOrder);

                //Calculate Total Price
                int total = 0;
                List<SweetOrder> orders = new Database(context).getCarts(Common.USER_Phone);
                for (SweetOrder item : orders) {

                    String product_price = item.getPrice();
                    String order_quan = item.getQuantity();
                    String product_dis = item.getDiscount();

                    int final_price = Integer.parseInt(product_price) * Integer.parseInt(order_quan);

                    final double dis = final_price * (Double.parseDouble(product_dis) / 100);
                    final double dis_pirce = final_price - dis;

                    total += dis_pirce;
                }
                Locale locale = new Locale("en", "IN");
                NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
                Cart.txtTotalPrice.setText(fmt.format(total));

            }
        });

        Locale locale = new Locale("en", "IN");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
        int price = (Integer.parseInt(listData.get(position).getPrice()));

        holder.txt_price.setText(fmt.format(price));
        holder.txt_cart_name.setText(listData.get(position).getProductName());

    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public SweetOrder getItem(int position){
        return listData.get(position);
    }

    public void removeItem(int position){
        listData.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(SweetOrder item, int position){
        listData.add(position, item);
        notifyItemInserted(position);
    }

}
