package agjs.gautham.rjsweets.user.navigation_drawer.order_user;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import agjs.gautham.rjsweets.R;
import agjs.gautham.rjsweets.common.Common;

public class Orders extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.nav_orders_user, container, false);

        Button view_placed_order = root.findViewById(R.id.view_placed_order_user);
        Button view_delivered_order = root.findViewById(R.id.view_delivered_order_user);
        Button view_rejected_order = root.findViewById(R.id.view_rejected_order_user);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();

        if (mUser != null){

            view_placed_order.setClickable(true);
            view_delivered_order.setClickable(true);
            view_rejected_order.setClickable(true);

            view_placed_order.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(getActivity(),OrderPlaced.class));
                    Common.intentOpenAnimation(getActivity());
                }
            });

            view_delivered_order.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(getActivity(),OrderDelivered.class));
                    Common.intentOpenAnimation(getActivity());
                }
            });

            view_rejected_order.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(getActivity(),OrderRejected.class));
                    Common.intentOpenAnimation(getActivity());
                }
            });

        } else {

            Toast.makeText(getActivity(),"Please Sign-In To View Orders",Toast.LENGTH_SHORT).show();

            view_placed_order.setClickable(false);
            view_delivered_order.setClickable(false);
            view_rejected_order.setClickable(false);

        }

        return root;
    }
}