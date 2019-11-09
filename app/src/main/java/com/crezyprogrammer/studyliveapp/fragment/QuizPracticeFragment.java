package com.crezyprogrammer.studyliveapp.fragment;


import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.crezyprogrammer.studyliveapp.MainActivity;
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
public class QuizPracticeFragment extends AppCompatActivity {



    @BindView(R.id.question)
    TextView question;
    int i = 1;
    int w = 0, r = 0;
    int postion;
    @BindView(R.id.passage)
    TextView passage;
    long total_question = 0;
    @BindView(R.id.position)
    TextView position_question;
    int current = 0;
    ProgressDialog progressDialog;
    @BindView(R.id.oa)
    Button oa;
    @BindView(R.id.oc)
    Button oc;
    @BindView(R.id.ob)
    Button ob;
    @BindView(R.id.od)
    Button od;


    public QuizPracticeFragment() {
        // Required empty public constructor
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quiz_layout);
        ButterKnife.bind(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("loading");
        progressDialog.show();
        get_total();
        buttonEnable(false);
        //getQuestion(i);


    }

    private void get_total() {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("admin").child("practice");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                total_question = dataSnapshot.getChildrenCount();
                progressDialog.dismiss();
                getQuestion(i);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void buttonEnable(boolean b) {
        oa.setClickable(b);
        ob.setClickable(b);
        oc.setClickable(b);
        od.setClickable(b);
    }

    private void getQuestion(int i) {

        if (i <= total_question) {
            position_question.setText(i + "/" + total_question);
        }
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("loading");
        progressDialog.show();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("admin").child("practice").child(i + "");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    ResetColor();
                    question.setText(Html.fromHtml(dataSnapshot.child("title").getValue().toString()));
                    postion = dataSnapshot.child("answer").getValue(Integer.class);
                    oa.setText(dataSnapshot.child("optionA").getValue().toString());
                    ob.setText(dataSnapshot.child("optionB").getValue().toString());
                    oc.setText(dataSnapshot.child("optionC").getValue().toString());
                    od.setText(dataSnapshot.child("optionD").getValue().toString());
                    passage.setText(dataSnapshot.child("passage").getValue().toString());

                    buttonEnable(true);
                    progressDialog.dismiss();

                } else {

                    Builder dialogBuilder = new Builder(QuizPracticeFragment.this);
                    // ...Irrelevant code for customizing the buttons and title
                    LayoutInflater inflater = getLayoutInflater();
                    View dialogView = inflater.inflate(R.layout.complete, null);
                    dialogBuilder.setView(dialogView);
                    TextView totalL = dialogView.findViewById(R.id.totalL);
                    TextView rightL=dialogView.findViewById(R.id.rightL);
                    TextView wrongL=dialogView.findViewById(R.id.wrongL);
                    Button home = dialogView.findViewById(R.id.home);
                    TextView txt = dialogView.findViewById(R.id.textView11);
                    txt.setText("You Have Successfully Completed Reading");
                    totalL.setText("Total Quiz:" + total_question);
                    rightL.setText("Right:" + r);
                    wrongL.setText("Wrong:" + w);
                    home.setOnClickListener(v -> {
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    });

                    AlertDialog alertDialog = dialogBuilder.create();
                    try {
                        alertDialog.show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });
    }

    @OnClick({R.id.oa, R.id.ob, R.id.oc, R.id.od})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.oa:
                ResetColor();
                changeColor(1, 1);
                break;
            case R.id.ob:
                ResetColor();
                changeColor(2, 2);
                break;
            case R.id.oc:
                ResetColor();
                changeColor(3, 3);
                break;
            case R.id.od:
                ResetColor();
                changeColor(4, 4);
                break;
        }
    }

    private void changeColor(int option, int ans) {
        oa.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        ob.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        oc.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        od.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        // option.setBackgroundColor(Color.YELLOW);

        //Do something after 100ms
        switch (option) {
            case 1:
                oa.setBackgroundColor(Color.RED);
                break;
            case 2:
                ob.setBackgroundColor(Color.RED);
                break;
            case 3:
                oc.setBackgroundColor(Color.RED);
                break;
            case 4:
                od.setBackgroundColor(Color.RED);
                break;
        }
        if (postion == ans) {


            switch (postion) {
                case 1:
                    oa.setBackgroundColor(Color.GREEN);
                    break;
                case 2:
                    ob.setBackgroundColor(Color.GREEN);
                    break;
                case 3:
                    oc.setBackgroundColor(Color.GREEN);
                    break;
                case 4:
                    od.setBackgroundColor(Color.GREEN);
                    break;
            }
        }
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 100ms
                if (ans == postion) {
                    i++;
                    getQuestion(i);
                    r += 1;
                } else {
                    i++;
                    getQuestion(i);
                    w += 1;
                }
            }
        }, 3000);


    }

    private void ResetColor() {
        buttonEnable(false);
        oa.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        ob.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        oc.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        od.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
    }

}
