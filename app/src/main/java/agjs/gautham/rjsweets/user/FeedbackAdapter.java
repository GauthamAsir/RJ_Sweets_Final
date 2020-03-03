package agjs.gautham.rjsweets.user;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import agjs.gautham.rjsweets.R;

public class FeedbackAdapter extends RecyclerView.Adapter<FeedbackViewHolder> {

    Context context;
    List<String> list;

    public FeedbackAdapter(List<String> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public FeedbackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.feed_questions,parent,false);
        context = parent.getContext();
        return new FeedbackViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedbackViewHolder holder, int position) {

        holder.question.setText(list.get(position));

    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
