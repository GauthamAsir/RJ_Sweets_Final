package agjs.gautham.rjsweets.admin.ViewHolder;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import agjs.gautham.rjsweets.R;

public class ViewSoldItemsHolderRequest extends RecyclerView.ViewHolder {

    public TextView orderId_itemSold;
    public Button details_itemSold;

    public ViewSoldItemsHolderRequest(@NonNull View itemView) {
        super(itemView);

        orderId_itemSold = itemView.findViewById(R.id.orderId_itemSold);
        details_itemSold = itemView.findViewById(R.id.details_itemSold);
    }
}
