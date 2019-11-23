package agjs.gautham.rjsweets.user.navigation_drawer.order_user;

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

    public OrderDetailAdapter(List<SweetOrder> myOrders) {
        this.myOrders = myOrders;
    }

    @NonNull
    @Override
    public OrderDetailHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_detail_layout,parent,false);
        return new OrderDetailHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderDetailHolder holder, int position) {
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
