package agjs.gautham.rjsweets.delivery.ui.Home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import agjs.gautham.rjsweets.R;

public class Home extends Fragment {


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home_delivery, container, false);

        Button view_placed_order = root.findViewById(R.id.view_placed_order_delivery);
        Button view_delivered_order = root.findViewById(R.id.view_delivered_order_delivery);
        Button view_rejected_order = root.findViewById(R.id.view_rejected_order_delivery);
        Button view_picked_order = root.findViewById(R.id.view_picked_order_delivery);

        view_picked_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),OrderPicked.class));
            }
        });

        view_placed_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(),OrderPlaced.class));
            }
        });

        view_delivered_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(),OrderDelivered.class));
            }
        });

        view_rejected_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(),OrderRejected.class));
            }
        });

        return root;
    }
}