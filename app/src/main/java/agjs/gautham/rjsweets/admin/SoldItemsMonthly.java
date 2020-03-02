package agjs.gautham.rjsweets.admin;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import agjs.gautham.rjsweets.R;
import agjs.gautham.rjsweets.admin.ViewHolder.SoldItemsMonthlyAdapter;
import dmax.dialog.SpotsDialog;

public class SoldItemsMonthly extends AppCompatActivity {

    private RecyclerView items_monthly;
    private FirebaseDatabase database;
    private DatabaseReference itemsSold;

    private TextView month_tv;
    private String month;

    Map<String, String> map;
    Map<String, String> ff = new HashMap<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sold_items_monthly);

        items_monthly = findViewById(R.id.items_monthly);
        month_tv = findViewById(R.id.month_tv);

        if (getIntent()!=null){

            month = getIntent().getStringExtra("Month");
            database = FirebaseDatabase.getInstance();
            itemsSold = database.getReference("SoldItems").child(month);
        }

        RecyclerView.LayoutManager layoutManagerItems = new LinearLayoutManager(SoldItemsMonthly.this);

        items_monthly.setLayoutManager(layoutManagerItems);

        loadItems();
    }

    private void loadItems() {

        final AlertDialog dlgitems = new SpotsDialog.Builder()
                .setContext(SoldItemsMonthly.this)
                .setCancelable(false)
                .setMessage("Fetching Details...")
                .setTheme(R.style.DialogCustom)
                .build();

        dlgitems.show();

        itemsSold.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                    map = (Map<String, String>) snapshot.child("Sweets").getValue();

                    List<String> listk = new ArrayList<>(map.keySet());

                    for (String s : listk){

                        if (ff.containsKey(s)){

                            int a = Integer.parseInt(ff.get(s)) + Integer.parseInt(map.get(s));
                            ff.put(s,String.valueOf(a));

                        }else {
                            ff.put(s,map.get(s));
                        }

                    }
                }

                month_tv.setText(month);
                SoldItemsMonthlyAdapter adapter1 = new SoldItemsMonthlyAdapter(ff);
                adapter1.notifyDataSetChanged();
                items_monthly.setAdapter(adapter1);
                if (dlgitems.isShowing())
                    dlgitems.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
