package agjs.gautham.rjsweets.delivery.ui.Home;

import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import agjs.gautham.rjsweets.R;

public class OrderViewHolderDelivery extends RecyclerView.ViewHolder {

    public TextView txtOrderId, txtOrderStatus, txtOrderPhone, txtOrderAddress, txtUsername, txtOrderRejectedInfo
            , order_id_info;

    public Button btndetails, btndirection;

    public LinearLayout order_detail_container;

    public OrderViewHolderDelivery(@NonNull final View itemView) {
        super(itemView);

        txtOrderAddress = itemView.findViewById(R.id.order_address);
        txtOrderId = itemView.findViewById(R.id.order_id);
        txtOrderPhone = itemView.findViewById(R.id.order_phone);
        txtOrderStatus = itemView.findViewById(R.id.order_status);
        txtUsername = itemView.findViewById(R.id.order_username);

        btndetails = itemView.findViewById(R.id.bt_details);
        btndirection = itemView.findViewById(R.id.bt_direction);

        txtOrderRejectedInfo = itemView.findViewById(R.id.order_rejected_info);

        order_detail_container = itemView.findViewById(R.id.order_detail_container);
        order_id_info = itemView.findViewById(R.id.order_id_info);

    }

}
