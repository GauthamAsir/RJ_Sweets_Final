package agjs.gautham.rjsweets.user.navigation_drawer.order_user;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import agjs.gautham.rjsweets.R;

public class OrderDetailHolder extends RecyclerView.ViewHolder {

    public TextView name,quantity,price,discount, product_dis_value, product_dis_price;
    public LinearLayout discount_container;

    public OrderDetailHolder(@NonNull View itemView) {
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
