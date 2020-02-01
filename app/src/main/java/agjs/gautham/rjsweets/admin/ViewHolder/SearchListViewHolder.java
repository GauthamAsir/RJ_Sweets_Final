package agjs.gautham.rjsweets.admin.ViewHolder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import agjs.gautham.rjsweets.Interface.ItemClickListener;
import agjs.gautham.rjsweets.R;

public class SearchListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView oid, uname, ostatus;
    public CardView root_search_list;
    private ItemClickListener itemClickListener;

    public SearchListViewHolder(@NonNull View itemView) {
        super(itemView);

        oid = itemView.findViewById(R.id.order_id_search);
        uname = itemView.findViewById(R.id.user_name_search);
        ostatus = itemView.findViewById(R.id.order_status_search);

        root_search_list = itemView.findViewById(R.id.root_search_item);

        itemView.setOnClickListener(this);

    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v,getAdapterPosition(),false);
    }
}
