package com.crezyprogrammer.studyliveapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.crezyprogrammer.studyliveapp.fragment.UserActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.skydoves.powermenu.MenuAnimation;
import com.skydoves.powermenu.OnMenuItemClickListener;
import com.skydoves.powermenu.PowerMenu;
import com.skydoves.powermenu.PowerMenuItem;
import com.squareup.picasso.Picasso;
import com.yalantis.contextmenu.lib.ContextMenuDialogFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class ReplayActivity extends AppCompatActivity {
    String  image;
    @BindView(R.id.replay_recycler)
    RecyclerView replay_recycler;
    String post_id, key,user_id;
    PowerMenu powerMenu;
    FirebaseUser user;
    @BindView(R.id.more)
    ImageView more;
    @BindView(R.id.replay_edt_box)
    EditText replay_edt;
    @BindView(R.id.rep)
    TextView rep;
    @BindView(R.id.post_name)
    TextView postName;
    @BindView(R.id.post_text)
    TextView postText;
    @BindView(R.id.post_icon)
    CircleImageView postIcon;
    private ContextMenuDialogFragment mMenuDialogFragment;
    OnMenuItemClickListener<PowerMenuItem> onMenuItemClickListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_replay);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        post_id = intent.getStringExtra("post_id");
        key = intent.getStringExtra("key");
        getExtra();
        user = FirebaseAuth.getInstance().getCurrentUser();
        LinearLayoutManager manager = new LinearLayoutManager(this);
        replay_recycler.setLayoutManager(manager);
        setupReplayAdapter(key);


    }

    private void setupReplayAdapter(final String key) {
        Query query = FirebaseDatabase.getInstance().getReference("post").child(post_id).child("comment").child(key).child("replay");
        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<CommentModel>().setQuery(
                query, (DataSnapshot snapshot) -> {

                    try {
                        return new CommentModel
                                (snapshot.child("name").getValue().toString(), snapshot.child("profile").getValue().toString(), snapshot.child("text").getValue().toString(), snapshot.getKey(),snapshot.child("id").getValue().toString());
                    } catch (Exception e) {
                        return new CommentModel
                                ("no ", "", "no", snapshot.getKey(),"");

                    }
                }
        ).build();
        FirebaseRecyclerAdapter replayAdapter = new FirebaseRecyclerAdapter<CommentModel, ReplayViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ReplayViewHolder replayViewHolder, int i, @NonNull CommentModel commentModel) {
                replayViewHolder.replay(commentModel.getName(), commentModel.getProfile(), commentModel.getText(), key, commentModel.getKey(),commentModel.getId());
            }


            @NonNull
            @Override
            public ReplayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new ReplayViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_replay, parent, false));
            }
        };
        replay_recycler.setAdapter(replayAdapter);
        replayAdapter.startListening();


    }
    private void getExtra() {
        Intent intent = getIntent();
     String  name = intent.getStringExtra("name");
     String  text = intent.getStringExtra("text");
user_id=intent.getStringExtra("user_id");
        image = intent.getStringExtra("image");


        postName.setText(name);
        postText.setText(Html.fromHtml(text));
        Picasso.get().load(image).placeholder(R.drawable.ic_user).into(postIcon);
        postIcon.setOnClickListener(v -> {
            Intent intent1=new Intent(getApplicationContext(), UserActivity.class);
            intent1.putExtra("user_id",user_id);
            intent1.putExtra("user_name",name);
            intent1.putExtra("user_profile",image);
           startActivity(intent1);
        });
    }

    @OnClick({R.id.more, R.id.rep})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.more:
            {


                powerMenu = new PowerMenu.Builder(ReplayActivity.this)
                        // .addItemList(list) // list has "Novel", "Poerty", "Art"
                        .setAnimation(MenuAnimation.SHOWUP_TOP_LEFT) // Animation start point (TOP | LEFT).
                        .setMenuRadius(10f) // sets the corner radius.
                        .setMenuShadow(10f) // sets the shadow.
                        .setTextColor(Color.GRAY)
                        .setTextGravity(Gravity.CENTER)
                        .setTextTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD))
                        .setSelectedTextColor(Color.WHITE)
                        .setMenuColor(Color.WHITE)
                        .setSelectedMenuColor(ContextCompat.getColor(ReplayActivity.this, R.color.colorPrimary))
                        .setOnMenuItemClickListener(onMenuItemClickListener)
                        .build();
                //    powerMenu.addItem(new PowerMenuItem());
                if ((user.getPhotoUrl() + "").equals(image)) {
                    powerMenu.addItem(new PowerMenuItem("Edit"));
                    powerMenu.addItem(new PowerMenuItem("Delete"));
                } else powerMenu.addItem(new PowerMenuItem("Report"));
                powerMenu.showAsDropDown(view);
            }

                break;
            case R.id.rep:


           if ((user != null)) {

               if (!replay_edt.getText().toString().isEmpty()) {
                   DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("post").child(post_id).child("comment").child(key).child("replay").push();
                   databaseReference.child("name").setValue(user.getDisplayName());
                   databaseReference.child("id").setValue(user.getUid());
                   databaseReference.child("profile").setValue("" + user.getPhotoUrl());
                   databaseReference.child("text").setValue(replay_edt.getText().toString());
                   replay_edt.setText("");

//
               } else {
                   Toast.makeText(getApplicationContext(), "Please Write something", Toast.LENGTH_SHORT).show();
               }
//
           } else {
               AlertDialog.Builder builder = new AlertDialog.Builder(ReplayActivity.this);
               builder.setTitle("Login Required");
               builder.setMessage("You need to login to use the feature");
               builder.setPositiveButton("ok", (dialog, which) -> startActivity(new Intent(getApplicationContext(), SignInActivity.class)));
               builder.setNegativeButton("cancel", null);
               builder.show();
//
           }
//


                break;
        }
    }

    public class ReplayViewHolder extends RecyclerView.ViewHolder {
        TextView replay_text, like_text_rep;
        ImageView post_icon, like_icon_rep, more;
        TextView post_name;
        EditText replay_edt;
        boolean liked2 = false;
        TextView replay_post;

        public ReplayViewHolder(@NonNull View itemView) {
            super(itemView);
            replay_text = itemView.findViewById(R.id.post_text);
            like_text_rep = itemView.findViewById(R.id.post_like_rep);
            like_icon_rep = itemView.findViewById(R.id.like_icon_rep);
            post_icon = itemView.findViewById(R.id.post_icon);
            post_name = itemView.findViewById(R.id.post_name);
            more = itemView.findViewById(R.id.more);


        }

        public void replay(String name, String profile, String comment_text, String key, String key_rep,String id) {
post_icon.setOnClickListener(v -> {
    Intent intent1=new Intent(getApplicationContext(),UserActivity.class);
    intent1.putExtra("user_id",id);
    intent1.putExtra("user_name",name);
    intent1.putExtra("user_profile",profile);
   startActivity(intent1);
});

            more.setOnClickListener(v -> {



                powerMenu = new PowerMenu.Builder(ReplayActivity.this)
                        // .addItemList(list) // list has "Novel", "Poerty", "Art"
                        .setAnimation(MenuAnimation.SHOWUP_TOP_LEFT) // Animation start point (TOP | LEFT).
                        .setMenuRadius(10f) // sets the corner radius.
                        .setMenuShadow(10f) // sets the shadow.
                        .setTextColor(Color.GRAY)
                        .setTextGravity(Gravity.CENTER)
                        .setTextTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD))
                        .setSelectedTextColor(Color.WHITE)
                        .setMenuColor(Color.WHITE)
                        .setSelectedMenuColor(ContextCompat.getColor(ReplayActivity.this, R.color.colorPrimary))
                        .setOnMenuItemClickListener(onMenuItemClickListener)
                        .build();
                //    powerMenu.addItem(new PowerMenuItem());
                if ((user.getPhotoUrl() + "").equals(profile)) {

                    powerMenu.addItem(new PowerMenuItem("Delete"));
                } else powerMenu.addItem(new PowerMenuItem("Report"));
                powerMenu.showAsDropDown(v);
            });

            onMenuItemClickListener = (position, item) -> {
                /* Toast.makeText(getActivity(), item.getTitle() +position, Toast.LENGTH_SHORT).show(); */
                powerMenu.setSelectedPosition(position); // change selected item
                switch (item.getTitle()) {
                    case "Report": {
                        if (user != null) {
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("admin/report").child(key);
                            databaseReference.child("user_id").setValue(user.getUid());
                            databaseReference.child("post_id").setValue(post_id);
                            databaseReference.child("comment_id").setValue(key);
                            databaseReference.child("replay_id").setValue(key_rep);
                            Toast.makeText(getApplicationContext(), "Report Success", Toast.LENGTH_SHORT).show();

                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(ReplayActivity.this);
                            builder.setTitle("Login Required");
                            builder.setMessage("You need to login to use the feature");
                            builder.setPositiveButton("ok", (dialog, which) -> getApplicationContext().startActivity(new Intent(getApplicationContext(), SignInActivity.class)));
                            builder.setNegativeButton("cancel", null);
                            builder.show();

                        }
                    }
                    break;
                    case "Delete":
                        AlertDialog.Builder builder = new AlertDialog.Builder(ReplayActivity.this);
                        builder.setTitle("Delete Post");
                        builder.setMessage("Are you sure");
                        builder.setPositiveButton("ok", (dialog, which) -> {
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("post").child(post_id).child("comment").child(key).child("replay").child(key_rep);
                            databaseReference.removeValue();
                            Toast.makeText(getApplicationContext(), "Post Deleted", Toast.LENGTH_SHORT).show();
                        });
                        builder.setNegativeButton("cancel", null);
                        builder.show();
                        break;
                }


                powerMenu.dismiss();
            };


            DatabaseReference like_count_rep = FirebaseDatabase.getInstance().getReference("post").child(post_id).child("comment").child(key).child("replay").child(key_rep).child("like");
            like_count_rep.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (user != null) {


                        if (dataSnapshot.child(user.getUid() + "").exists()) {
                            String total_like2 = dataSnapshot.getChildrenCount() + "";
                            like_text_rep.setText(total_like2);
                            liked2 = true;
                            like_icon_rep.setImageDrawable(getResources().getDrawable(R.drawable.ic_thumb_up_black_24dp));


                        } else {
                            liked2 = false;
                            like_text_rep.setText("0");
                            like_icon_rep.setImageDrawable(getResources().getDrawable(R.drawable.ic_thumb_up));
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            like_icon_rep.setOnClickListener(v ->

            {
                DatabaseReference like_database_rep = FirebaseDatabase.getInstance().getReference("post").child(post_id).child("comment").child(key).child("replay").child(key_rep).child("like").child(user.getUid());

//                like_icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_thumb_up_black_24dp));
                if (liked2) {

                    like_icon_rep.setImageDrawable(getResources().getDrawable(R.drawable.ic_thumb_up));
                    like_database_rep.removeValue();

                    //  setupReplayAdapter(key);

                    liked2 = false;
                } else {
                    like_icon_rep.setImageDrawable(getResources().getDrawable(R.drawable.ic_thumb_up_black_24dp));
                    liked2 = true;
                    like_database_rep.child(user.getUid()).setValue(true);


                    //   setupReplayAdapter(key);


                }

            });


            replay_text.setText(comment_text);
            try {
                Picasso.get().load(profile).placeholder(R.drawable.ic_user).into(post_icon);
            } catch (Exception e) {
                e.printStackTrace();
            }
            post_name.setText(name);


        }
    }
}
