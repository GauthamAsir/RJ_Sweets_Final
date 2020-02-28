package agjs.gautham.rjsweets.admin.navigation_drawer.sold_items;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import agjs.gautham.rjsweets.R;
import agjs.gautham.rjsweets.admin.ViewSoldItems;
import agjs.gautham.rjsweets.common.Common;
import dmax.dialog.SpotsDialog;

public class SoldItems extends Fragment {

    private CalendarView calendarView;
    private TextView date, not_available;
    private Button bt_date;
    private FirebaseDatabase database;
    private DatabaseReference soldItems;
    private AlertDialog dialog;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.nav_solditems, container, false);

        dialog = new SpotsDialog.Builder()
                .setContext(getActivity())
                .setCancelable(false)
                .setMessage("Fetching Details...")
                .setTheme(R.style.DialogCustom)
                .build();

        calendarView = root.findViewById(R.id.calendarView);
        date = root.findViewById(R.id.date);
        not_available = root.findViewById(R.id.not_available);
        bt_date = root.findViewById(R.id.bt_date);

        database = FirebaseDatabase.getInstance();
        soldItems = database.getReference("SoldItems");

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {

                dialog.show();

                String monthLen = String.valueOf(String.valueOf(month).length());
                final String Date;
                if (monthLen.equals("1")){
                    int fd = month + 1;
                    String fm = String.format("0%s",String.valueOf(fd));
                    Date = dayOfMonth + "-" + fm + "-" + year;
                }else {
                    Date = dayOfMonth + "-" + (month + 1) + "-" + year;
                }

                date.setText(Date);

                soldItems.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child(Date).exists()){

                            dialog.dismiss();
                            not_available.setVisibility(View.GONE);
                            bt_date.setVisibility(View.VISIBLE);

                            bt_date.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(getActivity(), ViewSoldItems.class);
                                    intent.putExtra("Date",Date);
                                    startActivity(intent);
                                    Common.intentOpenAnimation(getActivity());
                                }
                            });

                        }else {

                            dialog.dismiss();
                            not_available.setVisibility(View.VISIBLE);
                            bt_date.setVisibility(View.GONE);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        return root;
    }
}
