package com.crezyprogrammer.studyliveapp.fragment;


import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.crezyprogrammer.studyliveapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
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
public class QuizPracticeFragment extends Fragment {


    @BindView(R.id.wait)
    TextView wait;
    @BindView(R.id.time)
    ImageView time;
    @BindView(R.id.option1)
    TextView option1;
    @BindView(R.id.option2)
    TextView option2;
    @BindView(R.id.option3)
    TextView option3;
    @BindView(R.id.option4)
    TextView option4;
    @BindView(R.id.question2)
    TextView question2;
    @BindView(R.id.options)
    RelativeLayout options;
    @BindView(R.id.question)
    TextView question;
    @BindView(R.id.level)
    TextView level;
    @BindView(R.id.prize)
    TextView prize;
    @BindView(R.id.quiz_content)
    RelativeLayout quizContent;
    int i=1;
int postion;
    public QuizPracticeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        BottomNavigationView navBar = getActivity().findViewById(R.id.navigation);
        navBar.setVisibility(View.GONE);
        View view = inflater.inflate(R.layout.quiz_layout, container, false);
        ButterKnife.bind(this,view);
        buttonEnable(false);
        getQuestion(i);
        return view;
    }

    private void buttonEnable(boolean b) {
        option1.setClickable(b);
        option2.setClickable(b);
        option3.setClickable(b);
        option4.setClickable(b);
    }

    private void getQuestion(int i) {
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("admin").child("practice").child(i+"");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    ResetColor();
                    question.setText(Html.fromHtml(dataSnapshot.child("title").getValue().toString()));
                    postion = dataSnapshot.child("answer").getValue(Integer.class);
                    option1.setText(dataSnapshot.child("optionA").getValue().toString());
                    option2.setText(dataSnapshot.child("optionB").getValue().toString());
                    option3.setText(dataSnapshot.child("optionC").getValue().toString());
                    option4.setText(dataSnapshot.child("optionD").getValue().toString());
                    buttonEnable(true);

                }
                else Toast.makeText(getActivity(), "No more Question", Toast.LENGTH_SHORT).show();

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @OnClick({R.id.option1, R.id.option2, R.id.option3, R.id.option4})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.option1:
                ResetColor();
                changeColor(option1,1);
                break;
            case R.id.option2:
                ResetColor();
                changeColor(option2,2);
                break;
            case R.id.option3:
                ResetColor();
                changeColor(option3,3);
                break;
            case R.id.option4:
                ResetColor();
                changeColor(option4,4);
                break;
        }
    }
    private void changeColor(TextView option,int ans) {
        option1.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        option2.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        option3.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        option4.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        option.setBackgroundColor(Color.YELLOW);
        final Handler handler1 = new Handler();
        handler1.postDelayed(() -> {
            //Do something after 100ms
            option.setBackgroundColor(Color.RED);


            switch (postion){
                case 1:
                    option1.setBackgroundColor(Color.GREEN);
                    break;
                case 2:
                    option2.setBackgroundColor(Color.GREEN);
                    break;
                case 3:
                    option3.setBackgroundColor(Color.GREEN);
                    break;
                case 4:
                    option4.setBackgroundColor(Color.GREEN);
                    break;
            }

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //Do something after 100ms
                    if(ans==postion){
                        Toast.makeText(getActivity(), "Correct!!", Toast.LENGTH_SHORT).show();
                        getQuestion(i++);
                    }
                    else {
                        Toast.makeText(getActivity(), "Wrong!!!", Toast.LENGTH_SHORT).show();
                        Builder builder = new Builder(getActivity());
                        builder.setTitle("Oops");
                        builder.setCancelable(false);

                        builder.setMessage("Wrong Answer");
                        builder.setPositiveButton("ok", null);
                        builder.show();
                    } }
            }, 1000);

        }, 2000);

    }
    private void ResetColor() {
        buttonEnable(false);
        option1.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        option2.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        option3.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        option4.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
    }
}
