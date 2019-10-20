package com.crezyprogrammer.studyliveapp.fragment;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.crezyprogrammer.studyliveapp.MainActivity;
import com.crezyprogrammer.studyliveapp.PostModel;
import com.crezyprogrammer.studyliveapp.R;
import com.devs.readmoreoption.ReadMoreOption;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {


    @BindView(R.id.post_edit)
    EditText postEdit;
    @BindView(R.id.profile)
    CircleImageView profile;
    @BindView(R.id.frameLayout)
    ConstraintLayout frameLayout;
    FirebaseUser user;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    MainActivity activity;
    FirebaseRecyclerAdapter adapter;
    String total_like,total_comment;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);
        MainActivity activity = ((MainActivity) getActivity());
        BottomNavigationView navBar = getActivity().findViewById(R.id.navigation);
        navBar.setVisibility(View.VISIBLE);
        user = FirebaseAuth.getInstance().getCurrentUser();
        postSetup();
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(manager);
        //setupRecycler();
        return view;
    }

    private void setupRecycler() {
        Query query = FirebaseDatabase.getInstance().getReference("post").orderByChild("updated");
        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<PostModel>().setQuery(
                query, snapshot -> new PostModel(snapshot.child("name").getValue().toString(), snapshot.child("image").getValue().toString(), snapshot.child("post_id").getValue().toString(),
                        snapshot.child("text").getValue().toString(), snapshot.child("user_id").getValue().toString(), snapshot.child("time").getValue(Long.class))).build();

        adapter = new FirebaseRecyclerAdapter<PostModel, ViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolder viewHolder, int i, @NonNull PostModel postModel) {
                viewHolder.setDetails(postModel.getName(), postModel.getImage(), postModel.getPost_id(), postModel.getText(), postModel.getTime(), postModel.getUser_id());
            }


            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.post, parent, false));
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name_txt, time_txt, text_txt, comment_text, like_text;
        ImageView photo, comment_icon, like_icon;
        boolean liked = false;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name_txt = itemView.findViewById(R.id.post_name);
            text_txt = itemView.findViewById(R.id.post_text);
            like_icon = itemView.findViewById(R.id.like_icon);
            time_txt = itemView.findViewById(R.id.post_time);
            photo = itemView.findViewById(R.id.post_icon);
            comment_icon = itemView.findViewById(R.id.comment_icon);
            comment_text = itemView.findViewById(R.id.post_comment);
            like_text = itemView.findViewById(R.id.post_like);
        }

        public void setDetails(String name, String image, String post_id, String text, long time, String user_id) {
            name_txt.setText(name);
            //
            like_icon.setOnClickListener(v ->

            {
                DatabaseReference like_database=FirebaseDatabase.getInstance().getReference("post").child(post_id).child("like");

//                like_icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_thumb_up_black_24dp));
           if(liked){
               liked=false;
               like_database.child(user.getUid()).removeValue();
               like_icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_thumb_up));

           }
           else {liked=true;
               like_database.child(user_id).setValue(true);
               like_icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_thumb_up_black_24dp));

           }

            });
            Picasso.get().load(image).placeholder(R.drawable.ic_user).into(photo);
            time_txt.setText(reformat(time));
            comment_icon.setOnClickListener(v -> {


                PostDetailFragment nextFrag = new PostDetailFragment();

                Bundle args = new Bundle();
                args.putString("text", text);
                args.putString("name", name);
                args.putString("image", image);
                args.putString("post_id", post_id);
                args.putString("total_comment", total_comment);
                args.putString("total_like", total_like);
                args.putString("time", reformat(time));
                nextFrag.setArguments(args);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .add(R.id.main_container, nextFrag, "findThisFragment").hide(((MainActivity) getActivity()).getActive())
                        .addToBackStack(null)

                        .commit();

            });


            // OR using options to customize

            ReadMoreOption readMoreOption = new ReadMoreOption.Builder(getActivity())
                    .textLength(200, ReadMoreOption.TYPE_CHARACTER) // OR
                    //.textLength(300, ReadMoreOption.TYPE_CHARACTER)
                    .moreLabel("MORE")
                    .lessLabel("LESS")
                    .moreLabelColor(Color.RED)
                    .lessLabelColor(Color.BLUE)
                    .labelUnderLine(true)
                    .expandAnimation(true)
                    .build();

            readMoreOption.addReadMoreTo(text_txt, text);


            DatabaseReference comment_count = FirebaseDatabase.getInstance().getReference("post").child(post_id).child("comment");
            comment_count.addValueEventListener(new ValueEventListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    try {
                        total_comment=dataSnapshot.getChildrenCount()+"";
                        comment_text.setText(total_comment);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }


                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            DatabaseReference like_count = FirebaseDatabase.getInstance().getReference("post").child(post_id).child("like");
            like_count.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    total_like=dataSnapshot.getChildrenCount() + "";
                    like_text.setText(total_like);
                    if (dataSnapshot.child(user.getUid() + "").exists()) {
                        liked = true;
                        like_icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_thumb_up_black_24dp));

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        private String reformat(long time) {
            long system = System.currentTimeMillis();
            long difference = system - time;
            Log.i("123321", difference + " dif");
            if (difference < 1000 * 60 * 60) {
                return difference / (1000 * 60) + " min ago";
            } else if (difference < 1000 * 60 * 60 * 24) {
                return difference / (1000 * 60 * 60) + " hour ago";
            } else {
                SimpleDateFormat format = new SimpleDateFormat("dd MMM hh:mm a");
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(time);
                return format.format(calendar.getTime());
            }
        }
    }

    private void postSetup() {
        Picasso.get().load(user.getPhotoUrl()).placeholder(R.drawable.ic_user).into(profile);

    }

    @OnClick(R.id.post_edit)
    public void onViewClicked() {
        final AlertDialog dialogBuilder = new AlertDialog.Builder(getActivity()).create();
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.post_dialog, null);

        final EditText editText = (EditText) dialogView.findViewById(R.id.write);
        TextView button1 = dialogView.findViewById(R.id.post_button);
        ImageView button2 = dialogView.findViewById(R.id.close);

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBuilder.dismiss();


            }
        });
        button1.setOnClickListener(view -> {
            // DO SOMETHINGS
            ProgressDialog progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("loading");
            progressDialog.show();
            long time = System.currentTimeMillis();
            String text = editText.getText().toString();
            if (!text.isEmpty()) {

                Map<String, Object> map = new HashMap<>();
                Long updated = 9999999999999L;
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("post").push();
                map.put("time", time);
                map.put("text", text);
                map.put("post_id", databaseReference.getKey());
                map.put("updated", updated - time);
                map.put("name", user.getDisplayName());
                map.put("user_id", user.getUid());
                map.put("image", user.getPhotoUrl() + "");
                Log.i("123321", user.getPhotoUrl() + "");

                databaseReference.setValue(map)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                dialogBuilder.dismiss();
                                progressDialog.dismiss();
                                Toast.makeText(getActivity(), "Successfully posted", Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                Toast.makeText(getActivity(), "Please Write Your Message", Toast.LENGTH_SHORT).show();
            }

        });


        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
    }

}
