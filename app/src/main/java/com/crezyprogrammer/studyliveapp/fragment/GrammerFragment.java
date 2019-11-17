package com.crezyprogrammer.studyliveapp.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.asp.fliptimerviewlibrary.CountDownClock;
import com.crezyprogrammer.studyliveapp.Model;
import com.crezyprogrammer.studyliveapp.R;
import com.crezyprogrammer.studyliveapp.VideoModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerFullScreenListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class GrammerFragment extends AppCompatActivity {
    @BindView(R.id.frag_video)
    YouTubePlayerView fragVideo;
    @BindView(R.id.next_live)
    RelativeLayout nextLive;
    @BindView(R.id.top)
    RelativeLayout top;
    @BindView(R.id.textView2)
    TextView textView2;
    @BindView(R.id.progressBar2)
    ProgressBar progressBar2;
    private YouTubePlayerView youTubePlayerView;
    private RelativeLayout video_layout;

    @BindView(R.id.textView3)
    TextView textView3;
    @BindView(R.id.timerProgramCountdown)
    CountDownClock timerProgramCountdown;
    @BindView(R.id.video_recycler)
    RecyclerView videoRecycler;
    private YouTubePlayer mYouTubePlayer;
    boolean b = false;
    boolean c = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_grammer);

        ButterKnife.bind(this);
        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext());
        videoRecycler.setLayoutManager(manager);
        getVideos();
        checkLive();
        Model.setI(1);
    }

    private void checkLive() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("admin").child("live/time/grammer");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean is_live = dataSnapshot.child("is_live").getValue(Boolean.class);
                if (is_live) {

                    fragVideo.setVisibility(View.VISIBLE);
                    nextLive.setVisibility(View.GONE);
                    String video_id = dataSnapshot.child("live_id").getValue().toString();


                    fragVideo.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
                        @Override
                        public void onReady(@NonNull YouTubePlayer youTubePlayer) {

                            youTubePlayer.loadVideo(video_id, 0);
                            mYouTubePlayer = youTubePlayer;

                        }


                        @Override
                        public void onStateChange(@NotNull YouTubePlayer youTubePlayer, @NotNull PlayerConstants.PlayerState state) {
                            super.onStateChange(youTubePlayer, state);
                            switch (state) {
                                case UNKNOWN:
                                case UNSTARTED:
                                case ENDED:
                                case PLAYING:
                                case PAUSED:
                                case BUFFERING:
                                case VIDEO_CUED:
                                default:
                            }
                        }
                    });


                }
                fragVideo.addFullScreenListener(new YouTubePlayerFullScreenListener() {
                    @Override
                    public void onYouTubePlayerEnterFullScreen() {
          //              Toast.makeText(getApplicationContext(), "full", Toast.LENGTH_SHORT).show();
                        //  getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
                    }

                    @Override
                    public void onYouTubePlayerExitFullScreen() {
                        //    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
                    }
                });


////////////////////////

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getVideos() {
        Query query = FirebaseDatabase.getInstance().getReference("admin").child("live").child("grammer").orderByChild("time");
        FirebaseRecyclerOptions<VideoModel> options = new FirebaseRecyclerOptions.Builder<VideoModel>().setQuery(query, snapshot -> new VideoModel(snapshot.child("id").getValue().toString(), snapshot.child("title").getValue().toString(), snapshot.child("category").getValue().toString())).build();
        FirebaseRecyclerAdapter<VideoModel, ViewHolder> adapter = new FirebaseRecyclerAdapter<VideoModel, ViewHolder>(options) {
            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.video_layout, parent, false));
            }

            @Override
            protected void onBindViewHolder(@NonNull ViewHolder viewHolder, int i, @NonNull VideoModel videoModel) {
                viewHolder.setDetails(videoModel.getId(), videoModel.getTitle(), videoModel.getCategory());
            }

            @Override
            public void onDataChanged() {
                super.onDataChanged();
                progressBar2.setVisibility(View.GONE);
            }
        };


        videoRecycler.setAdapter(adapter);
        adapter.startListening();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView thumblin;
        TextView title_text, category_text;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            thumblin = itemView.findViewById(R.id.thumblin);
            title_text = itemView.findViewById(R.id.title);
            category_text = itemView.findViewById(R.id.category);
            video_layout = itemView.findViewById(R.id.videos);
            youTubePlayerView = itemView.findViewById(R.id.frag_video);
        }

        public void setDetails(String id, String title, String category) {

            title_text.setText(title);
            category_text.setText(category);
            Picasso.get().load("https://img.youtube.com/vi/" + id + "/hqdefault.jpg").placeholder(R.drawable.logo).into(thumblin);
            video_layout.setOnClickListener(v -> {
                b = true;
           //     Toast.makeText(getApplicationContext(), "this " + b, Toast.LENGTH_SHORT).show();
                getTest();

                youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
                    @Override
                    public void onReady(@NonNull YouTubePlayer youTubePlayer) {

                        youTubePlayer.loadVideo(id, 0);
                        mYouTubePlayer = youTubePlayer;

                    }


                    @Override
                    public void onStateChange(@NotNull YouTubePlayer youTubePlayer, @NotNull PlayerConstants.PlayerState state) {
                        super.onStateChange(youTubePlayer, state);
                        switch (state) {
                            case UNKNOWN:
                            case UNSTARTED:
                            case ENDED:
                            case PLAYING:
                            case PAUSED:
                            case BUFFERING:
                            case VIDEO_CUED:
                            default:
                        }
                    }
                });


            });
            youTubePlayerView.addFullScreenListener(new YouTubePlayerFullScreenListener() {
                @Override
                public void onYouTubePlayerEnterFullScreen() {
             //       Toast.makeText(getApplicationContext(), "full", Toast.LENGTH_SHORT).show();
                    fragVideo.setVisibility(View.GONE);
                    textView2.setVisibility(View.GONE);

                    //  getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
                }

                @Override
                public void onYouTubePlayerExitFullScreen() {
                    fragVideo.setVisibility(View.VISIBLE);
                    textView2.setVisibility(View.VISIBLE);
                    //    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
                }
            });
        }
    }

    public void getTest() {
        b = true;
        nextLive.setVisibility(View.GONE);
        video_layout.setVisibility(View.GONE);
        youTubePlayerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        if (b) {
            timerProgramCountdown.setVisibility(View.VISIBLE);
            video_layout.setVisibility(View.VISIBLE);
            youTubePlayerView.setVisibility(View.GONE);
            b = false;
            try {
                mYouTubePlayer.pause();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else
        {
            try {
                mYouTubePlayer.pause();
                super.onBackPressed();
            } catch (Exception e) {
                super.onBackPressed();
                e.printStackTrace();
            }

        }

    }
}

