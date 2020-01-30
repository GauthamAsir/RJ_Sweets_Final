package agjs.gautham.rjsweets.user.navigation_drawer.home_user;

import android.content.Context;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import agjs.gautham.rjsweets.Interface.ItemClickListener;
import agjs.gautham.rjsweets.R;

public class MenuViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtMenuName, txtAvailableQuantity;
    public ImageView imageView;
    public Context context;
    public CardView root;

    private ItemClickListener itemClickListener;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public MenuViewHolder(@NonNull View itemView) {
        super(itemView);
        txtMenuName = itemView.findViewById(R.id.menu_name);
        imageView = itemView.findViewById(R.id.menu_image);

        root = itemView.findViewById(R.id.root);
        txtAvailableQuantity = itemView.findViewById(R.id.menu_available_quantity);

        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view,getAdapterPosition(),false);
    }
}
