package agjs.gautham.rjsweets.admin.ViewHolder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import agjs.gautham.rjsweets.R;

public class ShipperViewHolder extends RecyclerView.ViewHolder {

    public TextView shipperName, shipperEmail, shipperPhone;

    public ShipperViewHolder(@NonNull View itemView) {
        super(itemView);

        shipperName = itemView.findViewById(R.id.shipper_name);
        shipperEmail = itemView.findViewById(R.id.shipper_email);
        shipperPhone = itemView.findViewById(R.id.shipper_number);

    }
}
