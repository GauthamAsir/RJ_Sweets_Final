package agjs.gautham.rjsweets.admin.navigation_drawer.shippers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import agjs.gautham.rjsweets.R;
import agjs.gautham.rjsweets.admin.ViewHolder.ShipperViewHolder;
import dmax.dialog.SpotsDialog;

public class Shippers extends Fragment {

    private RecyclerView recycler_menu;
    private DatabaseReference shippers;

    private android.app.AlertDialog dialog;

    private FirebaseRecyclerAdapter<agjs.gautham.rjsweets.Model.Shippers, ShipperViewHolder> adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.nav_shippers_admin, container, false);

        //Init Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        shippers = database.getReference("Shippers");

        dialog = new SpotsDialog.Builder()
                .setContext(getActivity())
                .setCancelable(false)
                .setMessage("Loading Shippers...")
                .setTheme(R.style.DialogCustom)
                .build();

        recycler_menu = root.findViewById(R.id.shippers_list);

        recycler_menu.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recycler_menu.setLayoutManager(layoutManager);

        adapter = new FirebaseRecyclerAdapter<agjs.gautham.rjsweets.Model.Shippers, ShipperViewHolder>(
                agjs.gautham.rjsweets.Model.Shippers.class,
                R.layout.shipper_profiles,
                ShipperViewHolder.class, shippers
        ) {
            @Override
            protected void populateViewHolder(ShipperViewHolder shipperViewHolder, agjs.gautham.rjsweets.Model.Shippers shippers, int i) {

                shipperViewHolder.shipperName.setText(shippers.getName());
                shipperViewHolder.shipperPhone.setText(shippers.getPhone());
                shipperViewHolder.shipperEmail.setText(shippers.getEmail());

            }
        };
        recycler_menu.setAdapter(adapter);

        return root;
    }
}