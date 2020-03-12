package agjs.gautham.rjsweets.admin.navigation_drawer.feedback;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import agjs.gautham.rjsweets.Model.FeedModel;
import agjs.gautham.rjsweets.R;
import agjs.gautham.rjsweets.admin.ViewHolder.FeedViewHolderAdmin;

public class FeedbackAdmin extends Fragment {

    private FirebaseRecyclerAdapter<FeedModel, FeedViewHolderAdmin> adapter;
    private int totalUsers;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.nav_feedback_admin, container, false);

        RecyclerView feedList = root.findViewById(R.id.feedList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        feedList.setLayoutManager(layoutManager);

        DatabaseReference feed_v = FirebaseDatabase.getInstance().getReference("Feedback").child("feedValues");
        final DatabaseReference feed_q = FirebaseDatabase.getInstance().getReference("Feedback").child("feedQuestions");

        DatabaseReference feed_a = FirebaseDatabase.getInstance().getReference("Feedback").child("feedAnswers");

        feed_a.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                totalUsers = (int) dataSnapshot.getChildrenCount();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        adapter = new FirebaseRecyclerAdapter<FeedModel, FeedViewHolderAdmin>(
                FeedModel.class, R.layout.feedback_admin_layout, FeedViewHolderAdmin.class, feed_v
        ) {
            @Override
            protected void populateViewHolder(final FeedViewHolderAdmin feedViewHolderAdmin, FeedModel feedModel, int i) {

                final String key = String.valueOf(adapter.getRef(i).getKey());

                feed_q.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        String question = dataSnapshot.child(key).child("fquestion").getValue(String.class);
                        feedViewHolderAdmin.question.setText(question);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                String great = feedModel.getGreat();
                String good = feedModel.getGood();
                String neutral = feedModel.getNeutral();
                String bad = feedModel.getBad();
                String veryBad = feedModel.getVeryBad();

                Double a = (Double.parseDouble(great) / totalUsers) * 100;
                Double b = (Double.parseDouble(good) / totalUsers) * 100;
                Double c = (Double.parseDouble(neutral) / totalUsers) * 100;
                Double d = (Double.parseDouble(bad) / totalUsers) * 100;
                Double e = (Double.parseDouble(veryBad) / totalUsers) * 100;

                feedViewHolderAdmin.pgbar_great.setMax(100);
                feedViewHolderAdmin.pgbar_good.setMax(100);
                feedViewHolderAdmin.pgbar_neutral.setMax(100);
                feedViewHolderAdmin.pgbar_bad.setMax(100);
                feedViewHolderAdmin.pgbar_very_bad.setMax(100);

                feedViewHolderAdmin.pgbar_great.setProgress(a.intValue());
                feedViewHolderAdmin.pgbar_good.setProgress(b.intValue());
                feedViewHolderAdmin.pgbar_neutral.setProgress(c.intValue());
                feedViewHolderAdmin.pgbar_bad.setProgress(d.intValue());
                feedViewHolderAdmin.pgbar_very_bad.setProgress(e.intValue());

                feedViewHolderAdmin.pgbar_great.getProgressDrawable().setColorFilter(
                        Color.GREEN, android.graphics.PorterDuff.Mode.SRC_IN);
                feedViewHolderAdmin.pgbar_good.getProgressDrawable().setColorFilter(
                        Color.CYAN, android.graphics.PorterDuff.Mode.SRC_IN);
                feedViewHolderAdmin.pgbar_neutral.getProgressDrawable().setColorFilter(
                        Color.DKGRAY, android.graphics.PorterDuff.Mode.SRC_IN);
                feedViewHolderAdmin.pgbar_bad.getProgressDrawable().setColorFilter(
                        Color.RED, android.graphics.PorterDuff.Mode.SRC_IN);
                feedViewHolderAdmin.pgbar_very_bad.getProgressDrawable().setColorFilter(
                        Color.RED, android.graphics.PorterDuff.Mode.SRC_IN);

                feedViewHolderAdmin.pg_percent_very_bad.setText(String.format("%s votes",veryBad));
                feedViewHolderAdmin.pg_percent_bad.setText(String.format("%s votes",bad));
                feedViewHolderAdmin.pg_percent_good.setText(String.format("%s votes",good));
                feedViewHolderAdmin.pg_percent_great.setText(String.format("%s votes",great));
                feedViewHolderAdmin.pg_percent_neutral.setText(String.format("%s votes",neutral));

            }
        };

        adapter.notifyDataSetChanged();
        feedList.setAdapter(adapter);

        return root;
    }
}
