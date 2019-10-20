package com.crezyprogrammer.studyliveapp.fragment;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.crezyprogrammer.studyliveapp.Main2Activity;
import com.crezyprogrammer.studyliveapp.R;
import com.gauravk.audiovisualizer.visualizer.BarVisualizer;
import com.gauravk.audiovisualizer.visualizer.WaveVisualizer;
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
public class ListenFragment extends Fragment {


    @BindView(R.id.wave2)
    WaveVisualizer wave2;
    @BindView(R.id.play)
    ImageView play;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.time)
    ImageView time;
    @BindView(R.id.option1)
    TextView option1;
    @BindView(R.id.option3)
    TextView option3;
    @BindView(R.id.option2)
    TextView option2;
    @BindView(R.id.option4)
    TextView option4;
    @BindView(R.id.option)
    LinearLayout option;
    @BindView(R.id.question2)
    TextView question2;
    @BindView(R.id.options)
    RelativeLayout options;
    @BindView(R.id.level)
    TextView level;
    @BindView(R.id.prize)
    TextView prize;
    @BindView(R.id.quiz_content)
    RelativeLayout quizContent;
    @BindView(R.id.wait)
    TextView wait;
    private Button btn;
    private boolean playPause;
    private MediaPlayer mediaPlayer;
    private boolean initialStage = true;
    int i=1;
    int postion;
    String link;
    public ListenFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        BottomNavigationView navBar = getActivity().findViewById(R.id.navigation);
        navBar.setVisibility(View.GONE);

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_listen, container, false);
        ButterKnife.bind(this, view);
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        progressBar.setVisibility(View.INVISIBLE);

        buttonEnable(false);
        getQuestion(i);
        return view;
    }

    @OnClick({R.id.play, R.id.option1, R.id.option3, R.id.option2, R.id.option4})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.play:
                playAudio(link);

            break;
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

    private void playAudio(String link) {
        {
            if (!playPause) {
                // btn.setText("Pause Streaming");


                if (initialStage) {
                    new Player().execute(link);

                    int audioSessionId = mediaPlayer.getAudioSessionId();
                    if (audioSessionId != -1)
                        if (audioSessionId != AudioManager.ERROR) {
                            wave2.setAudioSessionId(audioSessionId);

                        }


                } else {
                    if (!mediaPlayer.isPlaying())
                        mediaPlayer.start();

                }

                playPause = true;

            } else {
                // btn.setText("Launch Streaming");
                //  Toast.makeText(getActivity(), "launch", Toast.LENGTH_SHORT).show();
                play.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_circle_filled_black_24dp));

                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                }

                playPause = false;
            }
        }
    }

    class Player extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... strings) {
            Boolean prepared = false;

            try {
                mediaPlayer.setDataSource(strings[0]);
                mediaPlayer.setOnCompletionListener(mediaPlayer -> {
                    initialStage = true;
                    playPause = false;
                    //   btn.setText("Launch Streaming");
                   // Toast.makeText(getActivity(), "launch 2", Toast.LENGTH_SHORT).show();
                    play.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_circle_filled_black_24dp));


                    mediaPlayer.stop();
                    mediaPlayer.reset();
                });

                mediaPlayer.prepare();
                prepared = true;

            } catch (Exception e) {
                Log.e("MyAudioStreamingApp", e.getMessage());
                prepared = false;
            }

            return prepared;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            progressBar.setVisibility(View.INVISIBLE);
            play.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_circle_filled_black_24dp));


            mediaPlayer.start();
            initialStage = false;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            play.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_circle_filled_black_24dp));

            progressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mediaPlayer != null) {
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }



    private void buttonEnable(boolean b) {
        option1.setClickable(b);
        option2.setClickable(b);
        option3.setClickable(b);
        option4.setClickable(b);
    }

    private void getQuestion(int i) {
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("admin").child("listen").child(i+"");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    ResetColor();
                   link=dataSnapshot.child("link").getValue().toString();
                    postion = dataSnapshot.child("answer").getValue(Integer.class);
                    option1.setText(dataSnapshot.child("optionA").getValue().toString());
                    option2.setText(dataSnapshot.child("optionB").getValue().toString());
                    option3.setText(dataSnapshot.child("optionC").getValue().toString());
                    option4.setText(dataSnapshot.child("optionD").getValue().toString());
                    buttonEnable(true);
                    playAudio(link);

                }
                else Toast.makeText(getActivity(), "No more Question", Toast.LENGTH_SHORT).show();

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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




