package agjs.gautham.rjsweets.user.navigation_drawer.order_user;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import agjs.gautham.rjsweets.R;

public class OrderDetailHolder extends RecyclerView.ViewHolder {

    public TextView name,quantity,price,discount;

    public OrderDetailHolder(@NonNull View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.product_name);
        quantity = itemView.findViewById(R.id.product_quantity);
        price = itemView.findViewById(R.id.product_price);
        discount = itemView.findViewById(R.id.product_discount);

    }
}
