package agjs.gautham.rjsweets.admin;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import agjs.gautham.rjsweets.Model.Request;
import agjs.gautham.rjsweets.R;
import agjs.gautham.rjsweets.admin.ViewHolder.ViewSoldItemsHolder;
import agjs.gautham.rjsweets.admin.ViewHolder.ViewSoldItemsHolderRequest;
import dmax.dialog.SpotsDialog;

public class ViewSoldItems extends AppCompatActivity {

    private RecyclerView requests, items;
    private FirebaseRecyclerAdapter<Request, ViewSoldItemsHolderRequest> requestAdapter;
    private FirebaseRecyclerAdapter<String, ViewSoldItemsHolder> itemAdapter;
    private AlertDialog dlg;
    private String date;
    private FirebaseDatabase database;
    private DatabaseReference itemsoldRequest, itemsSold;

    private List<String> list = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_sold_items);

        requests = findViewById(R.id.requests);
        items = findViewById(R.id.items);

        dlg = new SpotsDialog.Builder()
                .setContext(ViewSoldItems.this)
                .setCancelable(false)
                .setMessage("Fetching Details...")
                .setTheme(R.style.DialogCustom)
                .build();

        database = FirebaseDatabase.getInstance();

        if (getIntent()!=null){

            date = getIntent().getStringExtra("Date");
            itemsoldRequest = database.getReference("Requests");
            itemsSold = database.getReference("SoldItems").child(date).child("Sweets");
        }

        requests.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManagerRequest = new LinearLayoutManager(ViewSoldItems.this);
        ((LinearLayoutManager) layoutManagerRequest).setReverseLayout(true);
        ((LinearLayoutManager) layoutManagerRequest).setStackFromEnd(true);
        requests.setLayoutManager(layoutManagerRequest);

        RecyclerView.LayoutManager layoutManagerItems = new LinearLayoutManager(ViewSoldItems.this);
        items.setHasFixedSize(true);
        items.setLayoutManager(layoutManagerItems);

        loadRequests();
        loadItems();

    }

    private void loadItems() {

        final AlertDialog dlgitems = new SpotsDialog.Builder()
                .setContext(ViewSoldItems.this)
                .setCancelable(false)
                .setMessage("Fetching Details...")
                .setTheme(R.style.DialogCustom)
                .build();

        dlgitems.show();

        itemAdapter = new FirebaseRecyclerAdapter<String, ViewSoldItemsHolder>(
                String.class, R.layout.items_sold, ViewSoldItemsHolder.class, itemsSold) {
            @Override
            protected void populateViewHolder(ViewSoldItemsHolder viewSoldItemsHolder, String s, int i) {

                viewSoldItemsHolder.product_name.setText(itemAdapter.getRef(i).getKey());
                viewSoldItemsHolder.product_value.setText(s);

                if (dlgitems.isShowing())
                    dlgitems.dismiss();

            }
        };
        items.setAdapter(itemAdapter);

    }

    private void loadRequests() {

        final AlertDialog dlgreq = new SpotsDialog.Builder()
                .setContext(ViewSoldItems.this)
                .setCancelable(false)
                .setMessage("Fetching Details...")
                .setTheme(R.style.DialogCustom)
                .build();

        dlgreq.show();

        requestAdapter = new FirebaseRecyclerAdapter<Request, ViewSoldItemsHolderRequest>(
                Request.class, R.layout.request_details_item_sold, ViewSoldItemsHolderRequest.class,
                itemsoldRequest.orderByChild("date").equalTo(date)
        ) {
            @Override
            protected void populateViewHolder(ViewSoldItemsHolderRequest viewSoldItemsHolderRequest, final Request request, final int i) {

                viewSoldItemsHolderRequest.orderId_itemSold.setText(request.getId());

                viewSoldItemsHolderRequest.details_itemSold.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent orderDetail = new Intent(ViewSoldItems.this, OrderDetailAdmin.class);
                        orderDetail.putExtra("OrderId",requestAdapter.getRef(i).getKey());
                        orderDetail.putExtra("OrderStatus",request.getStatus());
                        startActivity(orderDetail);

                    }
                });

                if (dlgreq.isShowing())
                    dlgreq.dismiss();

            }
        };
        requests.setAdapter(requestAdapter);

    }
}
