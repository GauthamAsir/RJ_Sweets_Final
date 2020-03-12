package agjs.gautham.rjsweets.user;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import agjs.gautham.rjsweets.Model.FeedModel;
import agjs.gautham.rjsweets.Model.Feedback;
import agjs.gautham.rjsweets.Model.FeedbackList;
import agjs.gautham.rjsweets.R;
import agjs.gautham.rjsweets.common.Common;
import dmax.dialog.SpotsDialog;

public class FeedbackActivity extends AppCompatActivity {

    private RecyclerView feed_questions;
    private FirebaseRecyclerAdapter<Feedback, FeedbackViewHolder> adapter;
    RadioButton bt;
    Map<String, Integer> list = new HashMap<>();
    Map<String,FeedbackList> feedbackLists = new HashMap<>();

    private android.app.AlertDialog dlg;
    private String totalQ;
    private DatabaseReference feed_a, feed_v;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        dlg = new SpotsDialog.Builder()
                .setContext(FeedbackActivity.this)
                .setCancelable(false)
                .setMessage("Please wait...")
                .setTheme(R.style.DialogCustom)
                .build();

        feed_a = FirebaseDatabase.getInstance().getReference("Feedback").child("feedAnswers");
        feed_v = FirebaseDatabase.getInstance().getReference("Feedback").child("feedValues");

        loadQuestions();

        AlertDialog.Builder builder = new AlertDialog.Builder(FeedbackActivity.this);
        builder.setTitle("Feedback");
        builder.setCancelable(false);
        builder.setMessage("It just takes a few seconds, this will help us to improve our Application & services");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                check();
            }
        });

        builder.show();

    }

    private void check(){

        dlg.show();

        feed_a.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {

                if (dataSnapshot.child(Common.USER_Phone).exists()){

                    dlg.dismiss();

                    AlertDialog.Builder builder = new AlertDialog.Builder(FeedbackActivity.this);
                    builder.setTitle("Feedback Exists");
                    builder.setMessage("Your Feedback Already Exists. Do you want to submit another?");
                    builder.setCancelable(false);

                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            for (DataSnapshot snapshot : dataSnapshot.child(Common.USER_Phone).getChildren()) {

                                final String key = snapshot.getKey();
                                final String value = snapshot.getValue(String.class);

                                feed_v.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {

                                        FeedModel feedModel = dataSnapshot1.child(key).getValue(FeedModel.class);
                                        String b = feedModel.getVeryBad();

                                        if (!b.equals("0")){

                                            int c = Integer.parseInt(b) - 1;
                                            feed_v.child(key).child(value).setValue(String.valueOf(c));

                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                            dialog.dismiss();
                        }
                    });

                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            startActivity(new Intent(FeedbackActivity.this,DashboardUser.class));
                        }
                    });

                    builder.show();

                }else {
                    dlg.dismiss();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void loadQuestions(){

        dlg.show();

        feed_questions = findViewById(R.id.feed_questions);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(FeedbackActivity.this);
        feed_questions.setLayoutManager(layoutManager);

        DatabaseReference feed_q = FirebaseDatabase.getInstance().getReference("Feedback").child("feedQuestions");

        adapter = new FirebaseRecyclerAdapter<Feedback, FeedbackViewHolder>(
                Feedback.class, R.layout.feed_questions, FeedbackViewHolder.class, feed_q)
        {
            @Override
            protected void populateViewHolder(FeedbackViewHolder feedbackViewHolder, final Feedback feedback, final int i) {

                feedbackViewHolder.question.setText(feedback.getFquestion());

                feedbackViewHolder.radio_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {

                        bt = group.findViewById(checkedId);
                        list.put(String.valueOf(adapter.getItemId(i)), checkedId);

                        if (bt != null){

                            FeedbackList feedbackList = new FeedbackList(
                                    feedback.getFno(),
                                    feedback.getFquestion(),
                                    bt.getText().toString());

                            feedbackLists.put(String.valueOf(adapter.getItemId(i)),feedbackList);

                        }

                        dlg.dismiss();

                    }
                });

                String id = String.valueOf(adapter.getItemId(i));

                if (list.containsKey(id)){

                    feedbackViewHolder.radio_group.check(list.get(id));

                }else {
                    feedbackViewHolder.radio_group.clearCheck();
                }

            }
        };

        adapter.notifyDataSetChanged();
        feed_questions.setAdapter(adapter);

        feed_q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                totalQ = String.valueOf(dataSnapshot.getChildrenCount());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void bt_submit(View view) {

        if (String.valueOf(feedbackLists.size()).equals(totalQ)){

            feed_a.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    //check();
                    update();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }else {

            AlertDialog.Builder builder = new AlertDialog.Builder(FeedbackActivity.this);
            builder.setTitle("Feedback");
            builder.setMessage("Please Answer all the feedback questions");
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            builder.show();

        }

    }

    private void update(){

        dlg.show();

        List<String> l = new ArrayList<>(feedbackLists.keySet());

        for (int i = 0; i<l.size(); i++){

            final FeedbackList fl = feedbackLists.get(l.get(i));

            feed_a.child(Common.USER_Phone).child(fl.getId_f()).setValue(fl.getF_ans());

            feed_v.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    String a = dataSnapshot.child(fl.getId_f()).child(fl.getF_ans()).getValue(String.class);
                    int u = Integer.parseInt(a) + 1;
                    feed_v.child(fl.getId_f()).child(fl.getF_ans()).setValue(String.valueOf(u));

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

        if (dlg.isShowing())
            dlg.dismiss();
        startActivity(new Intent(FeedbackActivity.this, DashboardUser.class));

    }
}
