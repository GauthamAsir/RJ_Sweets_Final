package agjs.gautham.rjsweets.admin.navigation_drawer.flags;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import agjs.gautham.rjsweets.Model.Flags;
import agjs.gautham.rjsweets.R;
import agjs.gautham.rjsweets.common.Common;

public class FlagsAdmin extends Fragment {

    private View root;
    private TextView latest_version, mandatory_update, make_orders, make_orders_reason, splash_update_check;
    private TextInputLayout latest_version_edit, make_no_order_reason_edit;
    private Button back, update, edit;
    private RadioGroup make_no_order_radio_grp, mandatory_update_grp, splash_check_grp;
    private CardView display_container_flags, display_edit_flags;
    private Animation fadeout, fadein;
    private DatabaseReference flags;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.nav_flags_admin, container, false);

        init();

        fadein = AnimationUtils.loadAnimation(getActivity(), R.anim.fadein);
        fadeout = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_out);
        flags = FirebaseDatabase.getInstance().getReference("flags");

        load();


        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                display_container_flags.startAnimation(fadeout);
                display_container_flags.setVisibility(View.GONE);
                display_edit_flags.startAnimation(fadein);
                display_edit_flags.setVisibility(View.VISIBLE);

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back_();
            }
        });

        latest_version_edit.getEditText().setText(String.valueOf(Common.flags.getLatestVersion()));
        make_no_order_reason_edit.getEditText().setText(Common.flags.getMakeOrdersReason());

        if (Common.flags.isMakeOrders()){
            make_no_order_radio_grp.check(R.id.true_make_no_order);
        }else {
            make_no_order_radio_grp.check(R.id.false_make_no_order);
        }

        if (Common.flags.isMandatoryLatestUpdate()){
            mandatory_update_grp.check(R.id.true_mandatory_update);
        }else {
            mandatory_update_grp.check(R.id.false_mandatory_update);
        }

        if (Common.flags.isSplashUpdateCheck()){
            splash_check_grp.check(R.id.true_splash_check);
        }else {
            splash_check_grp.check(R.id.false_splash_check);
        }

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (latestVersionCheck() && makeNoOrderReasonCheck()){

                    String lv = latest_version_edit.getEditText().getText().toString();
                    String reason = make_no_order_reason_edit.getEditText().getText().toString();

                    boolean make_no_order_value = make_no_order_radio_grp.getCheckedRadioButtonId() == R.id.true_make_no_order;
                    boolean mandatory_value = mandatory_update_grp.getCheckedRadioButtonId() == R.id.true_mandatory_update;
                    boolean splash_check_value = splash_check_grp.getCheckedRadioButtonId() == R.id.true_splash_check;

                    Flags flags1 = new Flags(splash_check_value,
                            make_no_order_value,
                            Double.parseDouble(lv),
                            mandatory_value,
                            reason);

                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Updates")
                            .child("version");
                    reference.setValue(Double.parseDouble(lv));

                    flags.setValue(flags1);
                    back_();
                    load();
                    Toast.makeText(getActivity(),"Flags Update",Toast.LENGTH_SHORT).show();


                }

            }
        });

        return root;
    }

    private void load() {

        flags.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Common.flags = dataSnapshot.getValue(Flags.class);
                latest_version.setText(Common.flags.getLatestVersion().toString());
                mandatory_update.setText(String.valueOf(Common.flags.isMandatoryLatestUpdate()));
                make_orders.setText(String.valueOf(Common.flags.isMakeOrders()));
                make_orders_reason.setText(Common.flags.getMakeOrdersReason());
                splash_update_check.setText(String.valueOf(Common.flags.isSplashUpdateCheck()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void back_() {

        display_edit_flags.startAnimation(fadeout);
        display_edit_flags.setVisibility(View.GONE);
        display_container_flags.startAnimation(fadein);
        display_container_flags.setVisibility(View.VISIBLE);

    }

    private void init(){

        latest_version = root.findViewById(R.id.latest_version_tv);
        mandatory_update = root.findViewById(R.id.mandatory_update_tv);
        make_orders = root.findViewById(R.id.make_no_order_tv);
        make_orders_reason = root.findViewById(R.id.make_no_order_reason_tv);
        splash_update_check = root.findViewById(R.id.splash_update_check_tv);

        latest_version_edit = root.findViewById(R.id.latest_version_edit);
        make_no_order_reason_edit = root.findViewById(R.id.make_no_order_reason_edit);

        edit = root.findViewById(R.id.edit_flags);
        back = root.findViewById(R.id.back_flags);
        update = root.findViewById(R.id.update_flags);

        display_container_flags = root.findViewById(R.id.display_container_flags);
        display_edit_flags = root.findViewById(R.id.display_edit_flags);

        make_no_order_radio_grp = root.findViewById(R.id.make_no_order_radio_grp);
        mandatory_update_grp = root.findViewById(R.id.mandatory_update_grp);
        splash_check_grp = root.findViewById(R.id.splash_check_grp);

    }

    private boolean latestVersionCheck(){

        String lv = latest_version_edit.getEditText().getText().toString();

        if (lv.isEmpty()){
            latest_version_edit.setError("Field Can't be Empty");
            latest_version_edit.requestFocus();
            return false;
        }else {
            latest_version_edit.setError(null);
            latest_version_edit.clearFocus();
            hideKeyboard(latest_version_edit);
            return true;
        }

    }

    private boolean makeNoOrderReasonCheck(){

        String reasom = make_no_order_reason_edit.getEditText().getText().toString();

        if (reasom.isEmpty()){
            make_no_order_reason_edit.setError("Field Can't be Empty");
            make_no_order_reason_edit.requestFocus();
            return false;
        }else {
            make_no_order_reason_edit.setError(null);
            make_no_order_reason_edit.clearFocus();
            hideKeyboard(make_no_order_reason_edit);
            return true;
        }

    }

    public void hideKeyboard(TextInputLayout t1){
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(t1.getWindowToken(), 0);
    }

}
