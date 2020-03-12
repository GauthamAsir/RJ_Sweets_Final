package agjs.gautham.rjsweets.admin.navigation_drawer.feedback;

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

import java.util.HashMap;
import java.util.Map;

import agjs.gautham.rjsweets.Model.FeedModel;
import agjs.gautham.rjsweets.R;
import agjs.gautham.rjsweets.admin.ViewHolder.FeedViewHolderAdmin;

public class FeedbackAdmin extends Fragment {

    RecyclerView feedList;

    Map<String, String> map;
    Map<String, String> ff = new HashMap<>();

    FirebaseRecyclerAdapter<FeedModel, FeedViewHolderAdmin> adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.nav_feedback_admin, container, false);

        feedList = root.findViewById(R.id.feedList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        feedList.setLayoutManager(layoutManager);

        DatabaseReference feed_v = FirebaseDatabase.getInstance().getReference("Feedback").child("feedValues");
        final DatabaseReference feed_q = FirebaseDatabase.getInstance().getReference("Feedback").child("feedQuestions");

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

                feedViewHolderAdmin.pgbar_great.setProgress(Integer.parseInt(great ) * 100);
                feedViewHolderAdmin.pgbar_good.setProgress(Integer.parseInt(great ) * 100);
                feedViewHolderAdmin.pgbar_neutral.setProgress(Integer.parseInt(great ) * 100);
                feedViewHolderAdmin.pgbar_bad.setProgress(Integer.parseInt(great ) * 100);
                feedViewHolderAdmin.pgbar_very_bad.setProgress(Integer.parseInt(great ) * 100);

                feedViewHolderAdmin.pg_percent_very_bad.setText(veryBad);
                feedViewHolderAdmin.pg_percent_bad.setText(bad);
                feedViewHolderAdmin.pg_percent_good.setText(good);
                feedViewHolderAdmin.pg_percent_great.setText(great);
                feedViewHolderAdmin.pg_percent_neutral.setText(neutral);

            }
        };

        adapter.notifyDataSetChanged();
        feedList.setAdapter(adapter);

        return root;
    }
}
