package agjs.gautham.rjsweets.delivery.ui.Settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import agjs.gautham.rjsweets.R;
import agjs.gautham.rjsweets.login.DeliveryLogin;
import io.paperdb.Paper;

public class Settings extends Fragment {


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_settings_delivery, container, false);

        Button logout = root.findViewById(R.id.logOut_delivery);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Paper.book().destroy();
                Intent logout = new Intent(getActivity(), DeliveryLogin.class);
                logout.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(logout);
                getActivity().finish();

            }
        });

        return root;
    }
}