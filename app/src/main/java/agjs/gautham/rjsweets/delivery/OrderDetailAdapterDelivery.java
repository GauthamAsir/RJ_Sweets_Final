package agjs.gautham.rjsweets.delivery;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import agjs.gautham.rjsweets.Model.SweetOrder;
import agjs.gautham.rjsweets.R;

class MyViewHolder extends RecyclerView.ViewHolder{

    public TextView name,quantity,price,discount;

    public MyViewHolder(@NonNull View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.product_name);
        quantity = itemView.findViewById(R.id.product_quantity);
        price = itemView.findViewById(R.id.product_price);
        discount = itemView.findViewById(R.id.product_discount);
    }
}

public class OrderDetailAdapterDelivery extends RecyclerView.Adapter<MyViewHolder> {

    List<SweetOrder> myOrders;

    public OrderDetailAdapterDelivery(List<SweetOrder> myOrders) {
        this.myOrders = myOrders;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_detail_layout,parent,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        SweetOrder sweetOrder = myOrders.get(position);
        holder.name.setText(String.format("Name : %s",sweetOrder.getProductName()));
        holder.quantity.setText(String.format("Quantity : %s",sweetOrder.getQuantity()));
        holder.price.setText(String.format("Price : %s",sweetOrder.getPrice()));
        holder.discount.setText(String.format("Discount : %s",sweetOrder.getDiscount()));
    }

    @Override
    public int getItemCount() {
        return myOrders.size();
    }
}
