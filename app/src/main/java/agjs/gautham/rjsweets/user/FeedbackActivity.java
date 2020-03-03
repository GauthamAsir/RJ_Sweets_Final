package agjs.gautham.rjsweets.user;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import agjs.gautham.rjsweets.R;

public class FeedbackActivity extends AppCompatActivity {

    RecyclerView feed_questions;
    List<String> list =  new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        feed_questions = findViewById(R.id.feed_questions);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(FeedbackActivity.this);
        feed_questions.setLayoutManager(layoutManager);

        DatabaseReference feed = FirebaseDatabase.getInstance().getReference("Feedback").child("feedQuestions");

        feed.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                    list.add(snapshot.getValue(String.class));

                }

                FeedbackAdapter adapter1 = new FeedbackAdapter(list);
                adapter1.notifyDataSetChanged();
                feed_questions.setAdapter(adapter1);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void bt_submit(View view) {

        Toast toast = Toast.makeText(FeedbackActivity.this,"W.I.P",Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0 , 0);
        toast.show();

    }
}
