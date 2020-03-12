package agjs.gautham.rjsweets.admin.ViewHolder;

import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import agjs.gautham.rjsweets.R;

public class FeedViewHolderAdmin extends RecyclerView.ViewHolder{

    public TextView question;
    public ProgressBar pgbar_great, pgbar_good, pgbar_neutral, pgbar_bad,  pgbar_very_bad;
    public TextView pg_percent_great, pg_percent_good, pg_percent_neutral, pg_percent_bad, pg_percent_very_bad;

    public FeedViewHolderAdmin(@NonNull View itemView) {
        super(itemView);

        question = itemView.findViewById(R.id.question);
        pgbar_great = itemView.findViewById(R.id.pgbar_great);
        pg_percent_great = itemView.findViewById(R.id.pg_percent_great);
        pgbar_good = itemView.findViewById(R.id.pgbar_good);
        pg_percent_good = itemView.findViewById(R.id.pg_percent_good);
        pgbar_neutral = itemView.findViewById(R.id.pgbar_neutral);
        pg_percent_neutral = itemView.findViewById(R.id.pg_percent_neutral);
        pgbar_bad = itemView.findViewById(R.id.pgbar_bad);
        pg_percent_bad = itemView.findViewById(R.id.pg_percent_bad);
        pgbar_very_bad = itemView.findViewById(R.id.pgbar_very_bad);
        pg_percent_very_bad = itemView.findViewById(R.id.pg_percent_very_bad);


    }
}
