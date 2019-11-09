package com.crezyprogrammer.studyliveapp.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.asp.fliptimerviewlibrary.CountDownClock;
import com.crezyprogrammer.studyliveapp.Model;
import com.crezyprogrammer.studyliveapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class LiveFragment extends Fragment {


    @BindView(R.id.timerProgramCountdown)
    CountDownClock timerProgramCountdown;
    @BindView(R.id.grammer)
    RelativeLayout grammer;
    @BindView(R.id.timerProgramCountdown2)
    CountDownClock timerProgramCountdown2;
    @BindView(R.id.spoken)
    RelativeLayout spoken;

    public LiveFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_live, container, false);
        ButterKnife.bind(this, view);
        Model.setI(0);

        //  timerProgramCountdown.startCountDown(99999999);
        DatabaseReference databaseReferenc = FirebaseDatabase.getInstance().getReference("admin/live/time/grammer");
        databaseReferenc.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long live = dataSnapshot.child("next_live").getValue(Long.class) - System.currentTimeMillis();
                if (live > 0) {
                    timerProgramCountdown.startCountDown(live);
                } else {

                    timerProgramCountdown2.setVisibility(View.INVISIBLE);

                }

                Log.i("123321", dataSnapshot.getValue() + "");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        DatabaseReference databaseReferenc1 = FirebaseDatabase.getInstance().getReference("admin/live/time/speaking");
        databaseReferenc1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long live = dataSnapshot.child("next_live").getValue(Long.class) - System.currentTimeMillis();
                if (live > 0) {
                    timerProgramCountdown2.startCountDown(live);
                } else {

                    timerProgramCountdown2.setVisibility(View.INVISIBLE);
                }

                Log.i("123321", dataSnapshot.getValue() + "");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        timerProgramCountdown.setCountdownListener(new CountDownClock.CountdownCallBack() {
            @Override
            public void countdownAboutToFinish() {

            }

            @Override
            public void countdownFinished() {

            }
        });

        return view;
    }

    @OnClick({R.id.grammer, R.id.spoken})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.grammer:

                getActivity().startActivity(new Intent(getActivity(), GrammerFragment.class));
                break;
            case R.id.spoken:
                getActivity().startActivity(new Intent(getActivity(), SpeakingFragment.class));
                break;
        }
    }
}
