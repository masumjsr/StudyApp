package com.crezyprogrammer.studyliveapp;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessagingService;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HowActivity extends AppCompatActivity {

    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.dis)
    TextView dis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.howto);
        ButterKnife.bind(this);

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("admin/howto");
    databaseReference.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            title.setText(dataSnapshot.child("title").getValue().toString());
            dis.setText(dataSnapshot.child("des").getValue().toString());
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    });
}
   }