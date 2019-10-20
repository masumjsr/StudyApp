package com.crezyprogrammer.studyliveapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.media.audiofx.Visualizer;
import android.os.Bundle;
import android.app.ProgressDialog;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.chibde.visualizer.LineBarVisualizer;
import com.gauravk.audiovisualizer.visualizer.BarVisualizer;
import com.gauravk.audiovisualizer.visualizer.WaveVisualizer;

public class Main2Activity extends AppCompatActivity {




        private Button btn;
        private boolean playPause;
        private MediaPlayer mediaPlayer;
        private ProgressDialog progressDialog;
        private boolean initialStage = true;
        BarVisualizer mVisualizer;
        WaveVisualizer mWaveVisualizer;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main2);
            btn = (Button) findViewById(R.id.audioStreamBtn);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            progressDialog = new ProgressDialog(this);
            mVisualizer=findViewById(R.id.bar);
            mWaveVisualizer=findViewById(R.id.wave);
          //  LineBarVisualizer lineBarVisualizer = findViewById(R.id.visualizer);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!playPause) {
                        btn.setText("Pause Streaming");

                        if (initialStage) {
                            new Player().execute("https://firebasestorage.googleapis.com/v0/b/study-live-app.appspot.com/o/Na%20Na%20Re%20Na%20Re%20KGF%20Mother%20Bgm%20Ringtone%202(MP3_160K).mp3?alt=media&token=84033558-782f-4123-aece-94ef2ea58c06");
                        //    lineBarVisualizer.setColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
//

// define custom number of bars you want in the visualizer between (10 - 256).
                         //   lineBarVisualizer.setDensity(70);

// Set you media player to the visualizer.
                         //   lineBarVisualizer.setPlayer(mediaPlayer.getAudioSessionId());
                            int audioSessionId = mediaPlayer.getAudioSessionId();
                            if (audioSessionId != -1)
                               // mVisualizer.setAudioSessionId(audioSessionId);
                                mWaveVisualizer.setAudioSessionId(audioSessionId);

                        } else {
                            if (!mediaPlayer.isPlaying())
                                mediaPlayer.start();

                        }

                        playPause = true;

                    } else {
                        btn.setText("Launch Streaming");

                        if (mediaPlayer.isPlaying()) {
                            mediaPlayer.pause();
                        }

                        playPause = false;
                    }
                }
            });
        }

        @Override
        protected void onPause() {
            super.onPause();

            if (mediaPlayer != null) {
                mediaPlayer.reset();
                mediaPlayer.release();
                mediaPlayer = null;
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
                        btn.setText("Launch Streaming");
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

                if (progressDialog.isShowing()) {
                    progressDialog.cancel();
                }

                mediaPlayer.start();
                initialStage = false;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                progressDialog.setMessage("Buffering...");
                progressDialog.show();
            }
        }
    }