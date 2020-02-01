package agjs.gautham.rjsweets.admin;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;

import java.util.ArrayList;
import java.util.List;

import agjs.gautham.rjsweets.Common;
import agjs.gautham.rjsweets.Interface.ItemClickListener;
import agjs.gautham.rjsweets.Model.Request;
import agjs.gautham.rjsweets.R;
import agjs.gautham.rjsweets.admin.ViewHolder.SearchListViewHolder;
import dmax.dialog.SpotsDialog;

public class OrderSearchAdmin extends AppCompatActivity {

    MaterialSearchBar searchView;
    RecyclerView recyclerView;
    android.app.AlertDialog dialog;
    FirebaseDatabase database;
    DatabaseReference reference;
    FirebaseRecyclerAdapter<Request, SearchListViewHolder> adapter;

    FirebaseRecyclerAdapter<Request, SearchListViewHolder> searchAdapter;
    List<String> suggestions = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_search_admin);

        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setCancelable(false)
                .setMessage("Loading Orders...")
                .setTheme(R.style.DialogCustom)
                .build();

        dialog.show();

        searchView = findViewById(R.id.search_bar);
        recyclerView = findViewById(R.id.search_list);

        searchView.setHint("Search by Order Id");

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(OrderSearchAdmin.this);
        recyclerView.setLayoutManager(layoutManager);

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Requests");

        loadList();

        searchView.setLastSuggestions(suggestions);
        searchView.setCardViewElevation(4);
        searchView.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                List<String> suggest = new ArrayList<>();
                for (String search: suggestions){

                    if (search.contains(searchView.getText())){
                        suggest.add(search);
                    }

                    searchView.setLastSuggestions(suggest);

                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        searchView.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {

                if (!enabled)
                    recyclerView.setAdapter(adapter);

            }

            @Override
            public void onSearchConfirmed(CharSequence text) {

                startSearch(text);

            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });

        loadSuggestions();

    }

    private void loadList() {

        adapter = new FirebaseRecyclerAdapter<Request, SearchListViewHolder>(
                Request.class,R.layout.search_list, SearchListViewHolder.class, reference) {
            @Override
            protected void populateViewHolder(final SearchListViewHolder searchListViewHolder, final Request request, int i) {

                searchListViewHolder.root_search_list.setAnimation(AnimationUtils
                        .loadAnimation(OrderSearchAdmin.this,R.anim.fade_scale_transmission));

                searchListViewHolder.oid.setText(request.getId());
                searchListViewHolder.uname.setText(request.getName());
                searchListViewHolder.ostatus.setText(Common.convertCodeToStatus(request.getStatus()));
                dialog.dismiss();

                searchListViewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                        Intent intent = new Intent(OrderSearchAdmin.this,OrderDetailAdmin.class);
                        intent.putExtra("OrderId",request.getId());
                        intent.putExtra("OrderStatus",request.getStatus());
                        startActivity(intent);

                    }
                });

            }
        };

        recyclerView.setAdapter(adapter);

    }

    private void startSearch(CharSequence text) {

        searchAdapter = new FirebaseRecyclerAdapter<Request, SearchListViewHolder>(
                Request.class,
                R.layout.search_list,
                SearchListViewHolder.class,
                reference.orderByChild("id").equalTo(String.valueOf(text))
        ) {
            @Override
            protected void populateViewHolder(SearchListViewHolder searchListViewHolder, final Request request, int i) {

                searchListViewHolder.root_search_list.setAnimation(AnimationUtils
                        .loadAnimation(OrderSearchAdmin.this,R.anim.fade_transmission));


                searchListViewHolder.oid.setText(request.getId());
                searchListViewHolder.uname.setText(request.getName());
                searchListViewHolder.ostatus.setText(Common.convertCodeToStatus(request.getStatus()));

                searchListViewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                        Intent intent = new Intent(OrderSearchAdmin.this,OrderDetailAdmin.class);
                        intent.putExtra("OrderId",request.getId());
                        intent.putExtra("OrderStatus",request.getStatus());
                        startActivity(intent);

                    }
                });

            }
        };

        recyclerView.swapAdapter(searchAdapter,true);

    }

    private void loadSuggestions(){

        reference.orderByChild("id").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnap: dataSnapshot.getChildren()){

                    Request request = postSnap.getValue(Request.class);
                    suggestions.add(request.getId());  //Adding All Order Ids to Suggest List

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}
