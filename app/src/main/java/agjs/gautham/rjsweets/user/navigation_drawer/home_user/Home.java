package agjs.gautham.rjsweets.user.navigation_drawer.home_user;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import agjs.gautham.rjsweets.R;

public class Home extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.nav_home_user, container, false);
        return root;
    }
}