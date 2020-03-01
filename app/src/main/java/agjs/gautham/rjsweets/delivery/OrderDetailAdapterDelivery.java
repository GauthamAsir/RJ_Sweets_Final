package agjs.gautham.rjsweets.delivery;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import agjs.gautham.rjsweets.Model.SweetOrder;
import agjs.gautham.rjsweets.R;

class MyViewHolder extends RecyclerView.ViewHolder{

    public TextView name,quantity,price,discount, product_dis_value, product_dis_price;
    public LinearLayout discount_container;

    public MyViewHolder(@NonNull View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.product_name);
        quantity = itemView.findViewById(R.id.product_quantity);
        price = itemView.findViewById(R.id.product_price);
        discount = itemView.findViewById(R.id.product_discount);

        product_dis_value = itemView.findViewById(R.id.product_dis_value);
        product_dis_price = itemView.findViewById(R.id.product_dis_price);

        discount_container = itemView.findViewById(R.id.discount_container);
    }
}

public class OrderDetailAdapterDelivery extends RecyclerView.Adapter<MyViewHolder> {

    List<SweetOrder> myOrders;
    Context context;

    public OrderDetailAdapterDelivery(List<SweetOrder> myOrders) {
        this.myOrders = myOrders;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_detail_layout,parent,false);
        context = parent.getContext();
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        SweetOrder sweetOrder = myOrders.get(position);
        holder.name.setText(String.format("Name : %s",sweetOrder.getProductName()));
        holder.quantity.setText(String.format("Quantity : %s",sweetOrder.getQuantity()));
        holder.price.setText(String.format("Price : %s",sweetOrder.getPrice()));
        holder.discount.setText(String.format("Discount : %s",sweetOrder.getDiscount()));

        if (!sweetOrder.getDiscount().equals("0")){

            holder.discount_container.setVisibility(View.VISIBLE);

            String product_price = sweetOrder.getPrice();
            String order_quan = sweetOrder.getQuantity();
            String product_dis = sweetOrder.getDiscount();

            int final_price = Integer.parseInt(product_price) * Integer.parseInt(order_quan);

            double dis = final_price * (Double.parseDouble(product_dis) / 100);
            double dis_pirce = final_price - dis;

            //holder.discount_container.setBackground(context.getResources().getDrawable(R.drawable.button_background_dark_green));
            holder.product_dis_value.setText(String.format("%s%% Discount Applied",sweetOrder.getDiscount()));
            holder.product_dis_price.setText(String.format("%s ₹",String.valueOf(dis_pirce)));

        }else {

            holder.discount_container.setVisibility(View.VISIBLE);
            holder.discount_container.setBackground(context.getResources().getDrawable(R.drawable.button_background_dark_green));
            holder.product_dis_value.setText("No Discounts Applied");
            holder.product_dis_price.setText(String.format("%s.0 ₹",sweetOrder.getPrice()));

        }
    }

    @Override
    public int getItemCount() {
        return myOrders.size();
    }
}
