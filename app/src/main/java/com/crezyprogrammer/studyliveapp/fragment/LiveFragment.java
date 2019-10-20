package com.crezyprogrammer.studyliveapp.fragment;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.asp.fliptimerviewlibrary.CountDownClock;
import com.crezyprogrammer.studyliveapp.MainActivity;
import com.crezyprogrammer.studyliveapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class LiveFragment extends Fragment {


    @BindView(R.id.timerProgramCountdown)
    CountDownClock timerProgramCountdown;
    @BindView(R.id.textView2)
    android.widget.TextView textView2;
    @BindView(R.id.grammer)
    android.widget.Button grammer;
    @BindView(R.id.spoken)
    android.widget.Button spoken;

    public LiveFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_live, container, false);
        ButterKnife.bind(this, view);

      //  timerProgramCountdown.startCountDown(99999999);
        DatabaseReference  databaseReferenc= FirebaseDatabase.getInstance().getReference("admin").child("live");
        databaseReferenc.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               long live=dataSnapshot.child("next_live").getValue(Long.class);
              timerProgramCountdown.startCountDown(live-System.currentTimeMillis());
               // Log.i("123321",dataSnapshot.getValue()+"");
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

    @butterknife.OnClick({R.id.grammer, R.id.spoken})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.grammer:

                GrammerFragment      listenFragment = new GrammerFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .add(R.id.main_container, listenFragment, "findThisFragment").hide(((MainActivity) getActivity()).getActive())
                        .addToBackStack(null)

                        .commit();
                break;
            case R.id.spoken:
                break;
        }
    }
}
