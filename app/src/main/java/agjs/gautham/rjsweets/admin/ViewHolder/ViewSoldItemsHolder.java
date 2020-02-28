package agjs.gautham.rjsweets.admin.ViewHolder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import agjs.gautham.rjsweets.R;

public class ViewSoldItemsHolder extends RecyclerView.ViewHolder {

    public TextView product_name, product_value;

    public ViewSoldItemsHolder(@NonNull View itemView) {
        super(itemView);

        product_name = itemView.findViewById(R.id.product_name);
        product_value = itemView.findViewById(R.id.product_value);
    }
}
