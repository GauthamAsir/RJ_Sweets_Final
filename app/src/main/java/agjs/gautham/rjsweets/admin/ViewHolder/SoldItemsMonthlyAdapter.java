package agjs.gautham.rjsweets.admin.ViewHolder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Map;

import agjs.gautham.rjsweets.R;

public class SoldItemsMonthlyAdapter extends RecyclerView.Adapter<ViewSoldItemsHolder> {

    Map<String, String> map;
    Context context;

    public SoldItemsMonthlyAdapter(Map<String, String> map) {
        this.map = map;
    }

    @NonNull
    @Override
    public ViewSoldItemsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.items_sold,parent,false);
        context = parent.getContext();
        return new ViewSoldItemsHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewSoldItemsHolder holder, int position) {

        int i = 0;
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if(position == i){
                String key = entry.getKey();
                String value = entry.getValue();

                holder.product_name.setText(key);
                holder.product_value.setText(value);

                // print your hello word here
                break;
            }
            i++;
        }

    }

    @Override
    public int getItemCount() {
        return map.size();
    }
}
