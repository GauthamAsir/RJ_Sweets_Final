package agjs.gautham.rjsweets.admin.navigation_drawer.orders;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import agjs.gautham.rjsweets.R;
import agjs.gautham.rjsweets.admin.OrderDeliveredAdmin;
import agjs.gautham.rjsweets.admin.OrderPlacedAdmin;
import agjs.gautham.rjsweets.admin.OrderRejectedAdmin;

public class Orders extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.nav_orders_admin, container, false);

        Button view_placed_order = root.findViewById(R.id.view_placed_order);
        Button view_delivered_order = root.findViewById(R.id.view_delivered_order);
        Button view_rejected_order = root.findViewById(R.id.view_rejected_order);

        view_placed_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), OrderPlacedAdmin.class));
            }
        });

        view_delivered_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), OrderDeliveredAdmin.class));
            }
        });

        view_rejected_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), OrderRejectedAdmin.class));
            }
        });

        return root;
    }
}