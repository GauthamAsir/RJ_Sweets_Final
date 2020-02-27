package agjs.gautham.rjsweets.admin.navigation_drawer.settings;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import agjs.gautham.rjsweets.R;
import agjs.gautham.rjsweets.common.Credits;
import agjs.gautham.rjsweets.common.UpdateActivity;
import agjs.gautham.rjsweets.login.Login;
import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;

public class Settings extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.nav_settings_admin, container, false);

        ImageView update = root.findViewById(R.id.update);
        ImageView logOut = root.findViewById(R.id.logout_admin);
        ImageView contributor = root.findViewById(R.id.contributor);

        contributor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), Credits.class));
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), UpdateActivity.class));
            }
        });

        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog dlg = new SpotsDialog.Builder()
                        .setContext(getActivity())
                        .setCancelable(false)
                        .setMessage("Logging You Out !...")
                        .setTheme(R.style.DialogCustom)
                        .build();

                final androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getActivity());
                builder.setTitle("Log Out !");
                builder.setMessage("Do you really want to Log Out ?");

                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dlg.show();

                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                dlg.dismiss();

                                Paper.book().destroy();
                                Intent logout = new Intent(getActivity(), Login.class);
                                logout.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(logout);
                                getActivity().finish();
                            }
                        }, 1500);

                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Return To Resumed Fragments
                    }
                });

                androidx.appcompat.app.AlertDialog alertDialog = builder.create();
                alertDialog.show();

            }
        });

        return root;
    }
}