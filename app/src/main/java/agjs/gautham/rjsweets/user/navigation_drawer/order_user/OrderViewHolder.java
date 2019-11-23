package agjs.gautham.rjsweets.user.navigation_drawer.order_user;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import agjs.gautham.rjsweets.R;

public class OrderViewHolder extends RecyclerView.ViewHolder {

    public TextView txtOrderId, txtOrderStatus, txtOrderPhone, txtOrderAddress, txtOrderTime, txtOrderDate, deliveredStatus;
    public ImageView orderStatusImage;
    public Button bt_orderDetails, bt_orderDetailsReason;
    public CardView orderDetailsReasonContainer;

    public OrderViewHolder(@NonNull View itemView) {
        super(itemView);

        //txtOrderAddress = itemView.findViewById(R.id.order_address);
        txtOrderId = itemView.findViewById(R.id.order_id);
        //txtOrderPhone = itemView.findViewById(R.id.order_phone);
        txtOrderStatus = itemView.findViewById(R.id.order_status);
        txtOrderTime = itemView.findViewById(R.id.order_time);
        txtOrderDate = itemView.findViewById(R.id.order_date);

        orderStatusImage = itemView.findViewById(R.id.order_status_image);
        deliveredStatus = itemView.findViewById(R.id.delivered_status);

        bt_orderDetails = itemView.findViewById(R.id.order_details);
        bt_orderDetailsReason = itemView.findViewById(R.id.order_details_reason);

        orderDetailsReasonContainer = itemView.findViewById(R.id.order_details_reason_container);
    }
}
