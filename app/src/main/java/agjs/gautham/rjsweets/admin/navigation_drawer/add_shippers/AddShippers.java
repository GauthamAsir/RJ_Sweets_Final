package agjs.gautham.rjsweets.admin.navigation_drawer.add_shippers;

import android.content.Context;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import agjs.gautham.rjsweets.Model.Shippers;
import agjs.gautham.rjsweets.R;
import dmax.dialog.SpotsDialog;

public class AddShippers extends Fragment {

    private TextInputLayout addShipperName, addShipperPhoneNumber, addShipperEmail, addShipperPass;
    private Button addShipper;
    private LinearLayout addShipper_container;

    android.app.AlertDialog dialog;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.nav_add_shippers_admin, container, false);

        dialog = new SpotsDialog.Builder()
                .setContext(getActivity())
                .setCancelable(false)
                .setMessage("Adding Shipper ...")
                .setTheme(R.style.DialogCustom)
                .build();

        addShipper_container = root.findViewById(R.id.addShipper_container);

        addShipperName = root.findViewById(R.id.addShipperUser);
        addShipperPhoneNumber = root.findViewById(R.id.addShipperPhone);
        addShipperEmail = root.findViewById(R.id.addShipperEmail);
        addShipperPass = root.findViewById(R.id.addShipperPass);

        addShipper = root.findViewById(R.id.bt_addShipper);

        addShipper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String shipperName = addShipperName.getEditText().getText().toString();
                final String shipperPhone = addShipperPhoneNumber.getEditText().getText().toString();
                final String shipperEmail = addShipperEmail.getEditText().getText().toString();
                final String shipperPass = addShipperPass.getEditText().getText().toString();

                if (validateName(shipperName) & validatePass(shipperPass) & validateEmail(shipperEmail) & validatePhone(shipperPhone)){

                    dialog.show();

                    //Init Firebase
                    final FirebaseDatabase database = FirebaseDatabase.getInstance();
                    final DatabaseReference databse_user = database.getReference("Shippers");

                    databse_user.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if (dataSnapshot.child(shipperPhone).exists()){
                                if (dialog.isShowing()){
                                    dialog.dismiss();
                                }
                                Snackbar.make(addShipper_container,"User Already Exists",Snackbar.LENGTH_LONG).show();
                            }else {

                                if (dialog.isShowing()){
                                    dialog.dismiss();
                                }

                                Shippers shippers = new Shippers(shipperName, shipperPass, shipperPhone, shipperEmail);
                                databse_user.child(shipperPhone).setValue(shippers);
                                Snackbar.make(addShipper_container,"Successfully Added Shipper "+shipperName,Snackbar.LENGTH_LONG).show();
                                addShipperName.getEditText().setText(null);
                                addShipperEmail.getEditText().setText(null);
                                addShipperPass.getEditText().setText(null);
                                addShipperPhoneNumber.getEditText().setText(null);
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }else {
                    Snackbar.make(addShipper_container,"Enter Valid Details",Snackbar.LENGTH_LONG).show();
                }
            }
        });

        return root;
    }

    //Validate Name
    boolean validateName(String name){

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
    boolean validatePass(String pass){

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
    boolean validateEmail(String email){

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
    boolean validatePhone(String pno){

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