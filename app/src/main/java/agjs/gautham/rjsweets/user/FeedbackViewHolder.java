package agjs.gautham.rjsweets.user;

import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import agjs.gautham.rjsweets.R;

public class FeedbackViewHolder extends RecyclerView.ViewHolder {

    public TextView question;
    public RadioGroup radio_group;
    public RadioButton great, good, neutral, bad, very_bad;

    public FeedbackViewHolder(@NonNull View itemView) {
        super(itemView);

        question = itemView.findViewById(R.id.question);

        radio_group = itemView.findViewById(R.id.radio_group);

        great = itemView.findViewById(R.id.great);
        good = itemView.findViewById(R.id.good);
        neutral = itemView.findViewById(R.id.neutral);
        bad = itemView.findViewById(R.id.bad);
        very_bad = itemView.findViewById(R.id.very_bad);

    }
}
