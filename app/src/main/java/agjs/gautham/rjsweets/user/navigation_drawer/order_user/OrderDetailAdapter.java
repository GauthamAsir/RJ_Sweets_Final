package agjs.gautham.rjsweets.user.navigation_drawer.order_user;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import agjs.gautham.rjsweets.Model.SweetOrder;
import agjs.gautham.rjsweets.R;

public class OrderDetailAdapter extends RecyclerView.Adapter<OrderDetailHolder>{

    public List<SweetOrder> myOrders;
    Context context;

    public OrderDetailAdapter(List<SweetOrder> myOrders) {
        this.myOrders = myOrders;
    }

    @NonNull
    @Override
    public OrderDetailHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_detail_layout,parent,false);
        context = parent.getContext();
        return new OrderDetailHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderDetailHolder holder, int position) {
        SweetOrder sweetOrder = myOrders.get(position);
        holder.name.setText(String.format("Name : %s",sweetOrder.getProductName()));
        holder.quantity.setText(String.format("Quantity : %s",sweetOrder.getQuantity()));
        holder.price.setText(String.format("Price : %s ₹",sweetOrder.getPrice()));
        holder.discount.setText(String.format("Discount : %s%%",sweetOrder.getDiscount()));

        if (!sweetOrder.getDiscount().equals("0")){

            holder.discount_container.setVisibility(View.VISIBLE);

            String product_price = sweetOrder.getPrice();
            String order_quan = sweetOrder.getQuantity();
            String product_dis = sweetOrder.getDiscount();

            int final_price = Integer.parseInt(product_price) * Integer.parseInt(order_quan);

            double dis = final_price * (Double.parseDouble(product_dis) / 100);
            double dis_pirce = final_price - dis;
            holder.product_dis_value.setText(String.format("%s%% Discount Applied",sweetOrder.getDiscount()));
            holder.product_dis_price.setText(String.format("%s ₹",String.valueOf(dis_pirce)));

        }else {

            holder.discount_container.setVisibility(View.VISIBLE);
            holder.discount_container.setBackground(context.getResources().getDrawable(R.drawable.button_background_dark_green));
            holder.product_dis_value.setVisibility(View.GONE);
            holder.product_dis_price.setTextSize(18);
            holder.product_dis_price.setText("No Discounts Applied");

        }
    }

    @Override
    public int getItemCount() {
        return myOrders.size();
    }
}
