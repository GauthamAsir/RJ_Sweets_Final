package agjs.gautham.rjsweets.admin.navigation_drawer.shippers;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import agjs.gautham.rjsweets.Common;
import agjs.gautham.rjsweets.Interface.ItemClickListener;
import agjs.gautham.rjsweets.R;
import agjs.gautham.rjsweets.admin.ViewHolder.ShipperViewHolder;
import dmax.dialog.SpotsDialog;

public class Shippers extends Fragment {

    private RecyclerView recycler_menu;
    private DatabaseReference shippers;

    AlertDialog.Builder alertDialog;

    private android.app.AlertDialog dialog;

    agjs.gautham.rjsweets.Model.Shippers newshippers;

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

                shipperViewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                        String shipperId = adapter.getRef(position).getKey();

                        Toast.makeText(getActivity(),"CLick", Toast.LENGTH_SHORT).show();

                    }
                });

            }
        };
        recycler_menu.setAdapter(adapter);

        return root;
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {

        if (item.getTitle().equals(Common.UPDATE)){
            showUpdateDialog(adapter.getRef(item.getOrder()).getKey(), adapter.getItem(item.getOrder()));
        } else {

            agjs.gautham.rjsweets.Model.Shippers s = adapter.getItem(item.getOrder());

            String shipperName = s.getName();
            showDeleteDialog(adapter.getRef(item.getOrder()).getKey(), shipperName);
        }

        return super.onContextItemSelected(item);
    }

    private void showDeleteDialog(final String key, final String name){

        alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle("Delete Shipper");
        alertDialog.setMessage("Do You Really want to Delete ?");
        alertDialog.setIcon(R.drawable.ic_delete);

        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                shippers.child(key).removeValue();
                Toast.makeText(getActivity(),"Shipper "+name +" Deleted",Toast.LENGTH_SHORT).show();

            }
        });

        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialog.show();

    }

    private void showUpdateDialog(final String key, agjs.gautham.rjsweets.Model.Shippers item){

        final AlertDialog dialogf;

        alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle("Update Shipper Info");
        alertDialog.setMessage("Please Fill The Information's");

        final LayoutInflater inflater = this.getLayoutInflater();
        View add_new_menu_item = inflater.inflate(R.layout.nav_add_shippers_admin, null);

        final TextInputLayout shipperUser, shipperPhone, shipperEmail, shipperPass;
        Button add;

        add = add_new_menu_item.findViewById(R.id.bt_addShipper);
        add.setVisibility(View.GONE);

        shipperUser = add_new_menu_item.findViewById(R.id.addShipperUser);
        shipperPhone = add_new_menu_item.findViewById(R.id.addShipperPhone);
        shipperEmail = add_new_menu_item.findViewById(R.id.addShipperEmail);
        shipperPass = add_new_menu_item.findViewById(R.id.addShipperPass);

        shipperUser.getEditText().setText(item.getName());
        shipperEmail.getEditText().setText(item.getEmail());
        shipperPass.getEditText().setText(item.getPassword());
        shipperPhone.getEditText().setText(item.getPhone());

        alertDialog.setIcon(R.drawable.ic_sweet_add);
        alertDialog.setView(add_new_menu_item);

        alertDialog.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });

        dialogf = alertDialog.create();
        dialogf.show();

        dialogf.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog = new SpotsDialog.Builder()
                        .setContext(getActivity())
                        .setCancelable(false)
                        .setMessage("Updating Shipper Info...")
                        .setTheme(R.style.DialogCustom)
                        .build();

                if (validateName(shipperUser) && validatePass(shipperPass)
                        && validateEmail(shipperEmail) && validatePhone(shipperPhone)){

                    dialog.show();

                    String name, pass, phone, email;

                    name = shipperUser.getEditText().getText().toString();
                    pass = shipperPass.getEditText().getText().toString();
                    phone = shipperPhone.getEditText().getText().toString();
                    email = shipperEmail.getEditText().getText().toString();

                    newshippers = new agjs.gautham.rjsweets.Model.Shippers(name,
                            pass,
                            phone,
                            email);

                    shippers.child(key).setValue(newshippers);

                    Toast.makeText(getActivity(),"Updated Shipper "+ name,Toast.LENGTH_LONG).show();

                    dialog.dismiss();
                    dialogf.dismiss();

                } else {
                    Toast.makeText(getActivity(),"Please Enter Valid Details",Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    //Validate Name
    boolean validateName(TextInputLayout addShipperName){

        String name = addShipperName.getEditText().getText().toString();

        if (name.isEmpty()){
            addShipperName.setError("Field Can't be Empty");
            addShipperName.requestFocus();
            return false;
        }else if (name.length() > 20) {
            addShipperName.setError("Name too long");
            addShipperName.requestFocus();
            return false;
        }else {
            addShipperName.setError(null);
            addShipperName.clearFocus();
            hideKeyboard(addShipperName);
            return true;
        }
    }

    // Password Validation
    boolean validatePass(TextInputLayout addShipperPass){

        String pass = addShipperPass.getEditText().getText().toString();

        if (pass.isEmpty()){
            addShipperPass.setError("Field Can't be Empty");
            addShipperPass.requestFocus();
            return false;
        }else {
            addShipperPass.setError(null);
            hideKeyboard(addShipperPass);
            addShipperPass.clearFocus();
            return true;
        }
    }

    //Email Validation
    boolean validateEmail(TextInputLayout addShipperEmail){

        String email = addShipperEmail.getEditText().getText().toString();

        if (email.isEmpty()){
            addShipperEmail.setError("Field Can't be Empty");
            addShipperEmail.requestFocus();
            return false;
        }else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            addShipperEmail.setError("Please Enter a Valid E-Mail");
            addShipperEmail.requestFocus();
            return false;
        }else {
            addShipperEmail.setError(null);
            hideKeyboard(addShipperEmail);
            addShipperEmail.clearFocus();
            return true;
        }
    }

    //Phone Validation
    boolean validatePhone(TextInputLayout addShipperPhoneNumber){

        String pno = addShipperPhoneNumber.getEditText().getText().toString();

        if (pno.length() != 10){
            addShipperPhoneNumber.setError("Enter Valid Number");
            addShipperPhoneNumber.requestFocus();
            return false;
        }else {
            addShipperPhoneNumber.setError(null);
            hideKeyboard(addShipperPhoneNumber);
            addShipperPhoneNumber.clearFocus();
            return true;
        }
    }

    void hideKeyboard(TextInputLayout t1){
        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(t1.getWindowToken(), 0);
    }

}