package agjs.gautham.rjsweets.admin.navigation_drawer.shippers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import agjs.gautham.rjsweets.R;

public class Shippers extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.nav_shippers_admin, container, false);
        return root;
    }
}