package com.crezyprogrammer.studyliveapp.fragment;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.crezyprogrammer.studyliveapp.CommentModel;
import com.crezyprogrammer.studyliveapp.R;
import com.crezyprogrammer.studyliveapp.ReplayActivity;
import com.crezyprogrammer.studyliveapp.SignInActivity;
import com.facebook.shimmer.ShimmerFrameLayout;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class PostDetailFragment extends Activity {
    EditText replay_edt;
    @BindView(R.id.shimmer_view_container)
    ShimmerFrameLayout shimmerViewContainer;
    @BindView(R.id.post_time)
    TextView postTime;
    @BindView(R.id.post_text)
    TextView postText;
    @BindView(R.id.post_name)
    TextView postName;
    @BindView(R.id.divider2)
    View divider2;
    @BindView(R.id.like_icon)
    ImageView likeIcon;
    @BindView(R.id.comment_icon)
    ImageView commentIcon;
    @BindView(R.id.post_comment)
    TextView postComment;
    @BindView(R.id.post_like)
    TextView postLike;
    @BindView(R.id.post_icon)
    CircleImageView postIcon;
    @BindView(R.id.comment_post)
    TextView postButton2;
    @BindView(R.id.comment_edt)
    EditText editText;
    @BindView(R.id.comment_recycler)
    RecyclerView recyclerView;
    String name, text, post_id, time, image, total_like, total_comment, user_id;
    String total_replay;
    DatabaseReference databaseReference;
    FirebaseUser user;
    FirebaseRecyclerAdapter adapter;
    LinearLayoutManager manager;
    List<String> list = new ArrayList<>();
    List<String> list2 = new ArrayList<>();
    @BindView(R.id.main_ScrollView)
    ScrollView mainScrollView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        user = FirebaseAuth.getInstance().getCurrentUser();
        setContentView(R.layout.fragment_post_detail);
        ButterKnife.bind(this);
        getExtra();

        manager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(manager);
        populateRecycle();

        getExtra();


    }


    private void populateRecycle() {
        shimmerViewContainer.startShimmerAnimation();
        Query query = FirebaseDatabase.getInstance().getReference("post").child(post_id).child("comment");
        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<CommentModel>().setQuery(
                query, snapshot -> new CommentModel
                        (snapshot.child("name").getValue().toString(), snapshot.child("profile").getValue().toString(), snapshot.child("text").getValue().toString(), snapshot.getKey(), snapshot.child("id").getValue().toString())
        ).build();
        adapter = new FirebaseRecyclerAdapter<CommentModel, ViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolder viewHolder, int i, @NonNull CommentModel postModel) {
                viewHolder.setDetails(postModel.getName(), postModel.getProfile(), postModel.getText(), postModel.getKey(), i, postModel.getId());
            }


            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.comment, parent, false));
            }

            @Override
            public void onDataChanged() {
                shimmerViewContainer.stopShimmerAnimation();
                shimmerViewContainer.setVisibility(View.GONE);


            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name_text, text_text, like_text, comment_text, like_text_rep;
        ImageView profile_icon, like_icon, comment_icon;
        TextView replay_post;
        RecyclerView replay_recycler;

        boolean liked = false;
        boolean clicked = false;
        PowerMenu powerMenu;
        boolean reprep = false;
        ImageView more;
        OnMenuItemClickListener<PowerMenuItem> onMenuItemClickListener;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name_text = itemView.findViewById(R.id.post_name);
            text_text = itemView.findViewById(R.id.post_text);
            profile_icon = itemView.findViewById(R.id.post_icon);
            like_icon = itemView.findViewById(R.id.like_icon);
            comment_icon = itemView.findViewById(R.id.comment_icon);
            like_text = itemView.findViewById(R.id.post_like);
            like_text_rep = itemView.findViewById(R.id.post_like_rep);
            comment_text = itemView.findViewById(R.id.post_comment);
comment_icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_reply_black_24dp));
            replay_recycler = itemView.findViewById(R.id.replay_recycler);

            more = itemView.findViewById(R.id.more);


        }

        public void setDetails(String name, String profile, String text, String key, int postiton, String id) {


            //  Toast.makeText(getApplicationContext(), "name "+name, Toast.LENGTH_SHORT).show();
            //


            name_text.setText(name);
            text_text.setText(text);
            Picasso.get().load(profile).placeholder(R.drawable.ic_user).into(profile_icon);

            profile_icon.setOnClickListener(v -> {
                Intent intent1 = new Intent(getApplicationContext(), UserActivity.class);
                intent1.putExtra("user_id", id);
                intent1.putExtra("user_name", name);
                intent1.putExtra("user_profile", profile);
                getApplicationContext().startActivity(intent1);
            });


            more.setOnClickListener(v ->
            {
                Context context = getApplicationContext();

                powerMenu = new PowerMenu.Builder(context)
                        // .addItemList(list) // list has "Novel", "Poerty", "Art"
                        .setAnimation(MenuAnimation.SHOWUP_TOP_LEFT) // Animation start point (TOP | LEFT).
                        .setMenuRadius(10f) // sets the corner radius.
                        .setMenuShadow(10f) // sets the shadow.
                        .setTextColor(Color.GRAY)
                        .setTextGravity(Gravity.CENTER)
                        .setTextTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD))
                        .setSelectedTextColor(Color.WHITE)
                        .setMenuColor(Color.WHITE)
                        .setSelectedMenuColor(ContextCompat.getColor(context, R.color.colorPrimary))
                        .setOnMenuItemClickListener(onMenuItemClickListener)
                        .build();
                //    powerMenu.addItem(new PowerMenuItem());
                if ((user.getPhotoUrl() + "").equals(profile)) {
                    powerMenu.addItem(new PowerMenuItem("Edit"));
                    powerMenu.addItem(new PowerMenuItem("Delete"));
                } else powerMenu.addItem(new PowerMenuItem("Report"));
                powerMenu.showAsDropDown(v);
            });

            onMenuItemClickListener = (position, item) -> {
                /* Toast.makeText(getApplicationContext(), item.getTitle() +position, Toast.LENGTH_SHORT).show(); */
                powerMenu.setSelectedPosition(position); // change selected item
                switch (item.getTitle()) {
                    case "Report": {
                        if (user != null) {
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("admin/report").child(key);
                            databaseReference.child("user_id").setValue(user.getUid());
                            databaseReference.child("post_id").setValue(post_id);
                            Toast.makeText(getApplicationContext(), "Report Success", Toast.LENGTH_SHORT).show();

                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
                            builder.setTitle("Login Required");
                            builder.setMessage("You need to login to use the feature");
                            builder.setPositiveButton("ok", (dialog, which) -> getApplicationContext().startActivity(new Intent(getApplicationContext(), SignInActivity.class)));
                            builder.setNegativeButton("cancel", null);
                            builder.show();

                        }
                    }
                    break;
                    case "Delete":
                        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
                        builder.setTitle("Delete Post");
                        builder.setMessage("Are you sure");
                        builder.setPositiveButton("ok", (dialog, which) -> {
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("post").child(post_id).child("comment").child(key);
                            databaseReference.removeValue();
                            Toast.makeText(getApplicationContext(), "Post Deleted", Toast.LENGTH_SHORT).show();
                        });
                        builder.setNegativeButton("cancel", null);
                        builder.show();
                        break;
                    case "Edit": {
                        if (user != null) {

                            final AlertDialog dialogBuilder = new AlertDialog.Builder(getApplicationContext()).create();
                            LayoutInflater inflater = getLayoutInflater();
                            View dialogView = inflater.inflate(R.layout.post_dialog, null);

                            final EditText editText = dialogView.findViewById(R.id.write);
                            final Spinner spinner = dialogView.findViewById(R.id.spinner);
                            spinner.setVisibility(View.INVISIBLE);
                            TextView button1 = dialogView.findViewById(R.id.post_button);
                            ImageView button2 = dialogView.findViewById(R.id.close);
                            editText.setText(text);

                            button2.setOnClickListener(view -> dialogBuilder.dismiss());
                            button1.setOnClickListener(view -> {

                                // DO SOMETHINGS
                                ProgressDialog progressDialog = new ProgressDialog(getApplicationContext());
                                progressDialog.setMessage("loading");
                                progressDialog.show();

                                String text1 = editText.getText().toString();
                                if (!text.isEmpty()) {

                                    Map<String, Object> map = new HashMap<>();
                                    Long updated = 9999999999999L;
                                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("post").child(post_id).child("comment").child(key);

                                    databaseReference.child("text").setValue(text1);

                                    dialogBuilder.dismiss();
                                    progressDialog.dismiss();

                                } else {
                                    progressDialog.dismiss();
                                    Toast.makeText(getApplicationContext(), "Please Write Your Message and select a category", Toast.LENGTH_SHORT).show();
                                }

                            });


                            dialogBuilder.setView(dialogView);
                            dialogBuilder.show();
                        }

                    }

                    break;
                }
                powerMenu.dismiss();
            };


            DatabaseReference like_count = FirebaseDatabase.getInstance().getReference("post").child(post_id).child("comment").child(key).child("like");
            like_count.addValueEventListener(new ValueEventListener() {
                @Override

                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (user != null) {


                        if (dataSnapshot.child(user.getUid() + "").exists()) {
                            total_like = dataSnapshot.getChildrenCount() + "";
                            like_text.setText(total_like);
                            liked = true;
                            try {
                                like_icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_thumb_up_black_24dp));
                            } catch (Resources.NotFoundException e) {
                                e.printStackTrace();
                            }

                        } else {
                            liked = false;
                            like_text.setText("0");
                            try {
                                like_icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_thumb_up));
                            } catch (Resources.NotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            DatabaseReference replay_count = FirebaseDatabase.getInstance().getReference("post").child(post_id).child("comment").child(key).child("replay");
            replay_count.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (user != null) {


                        if (dataSnapshot.exists()) {
                            total_replay = dataSnapshot.getChildrenCount() + "";
                            comment_text.setText(total_replay);


                        } else comment_text.setText("0");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            comment_icon.setOnClickListener(v -> {

                Intent intent = new Intent(getApplicationContext(), ReplayActivity.class);
                intent.putExtra("key", key);
                intent.putExtra("name", name);
                intent.putExtra("user_id", id);
                intent.putExtra("text", text);
                intent.putExtra("image", profile);
                intent.putExtra("post_id", post_id);
            startActivity(intent);

            });


            like_icon.setOnClickListener(v ->

            {  Log.i("123321",post_id);
                if(user!=null){
                ProgressDialog progressDialog = new ProgressDialog(PostDetailFragment.this);
                progressDialog.setMessage("loading");
                progressDialog.show();
                DatabaseReference like_database = FirebaseDatabase.getInstance().getReference("post").child(post_id).child("comment").child(key).child("like").child(user.getUid());

//                like_icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_thumb_up_black_24dp));
                if (liked) {

                    like_icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_thumb_up));
                    like_database.removeValue();

                    liked = false;
                } else {
                    like_icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_thumb_up_black_24dp));
                    liked = true;
                    like_database.child(user.getUid()).setValue(true);


                }
                progressDialog.dismiss();

            }

                else {

                    AlertDialog.Builder builder = new AlertDialog.Builder(PostDetailFragment.this);
                    builder.setTitle("Login Required");
                    builder.setMessage("You need to login to use the feature");
                    builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                          startActivity(new Intent(getApplicationContext(), SignInActivity.class));
                        }
                    });
                    builder.setNegativeButton("cancel", null);
                    builder.show();

                }
        }

            );
        }
    }


    private void getExtra() {
        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        text = intent.getStringExtra("text");
        image = intent.getStringExtra("image");
        time = intent.getStringExtra("time");
        post_id = intent.getStringExtra("post_id");
        user_id = intent.getStringExtra("user_id");
        total_like = intent.getStringExtra("total_like");
        total_comment = intent.getStringExtra("total_comment");
        postName.setText(name);
        postTime.setText(time);
        postText.setText(Html.fromHtml(text));
        postLike.setText(total_like);
        postComment.setText(total_comment);
        Picasso.get().load(image).placeholder(R.drawable.ic_user).into(postIcon);
        databaseReference = FirebaseDatabase.getInstance().getReference("post").child(post_id);
        postIcon.setOnClickListener(v -> {
            Intent intent1 = new Intent(getApplicationContext(), UserActivity.class);
            intent1.putExtra("user_id", user_id);
            intent1.putExtra("user_name", name);
            intent1.putExtra("user_profile", image);
            getApplicationContext().startActivity(intent1);
        });
    }


    @OnClick(R.id.comment_post)
    public void onViewClicked() {
        if (user != null) {
            String comment = editText.getText().toString();
            InputMethodManager inputManager = (InputMethodManager) getApplicationContext().getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            editText.setText("");

            if (!comment.isEmpty()) {

                Long updated = 9999999999999L;
                databaseReference.child("updated").setValue(updated - System.currentTimeMillis());
                DatabaseReference commentReference = databaseReference.child("comment").push();
                Map<String, Object> map = new HashMap<>();
                map.put("name", user.getDisplayName());
                map.put("text", comment);
                map.put("id", user.getUid());
                map.put("profile", user.getPhotoUrl() + "");
                commentReference.setValue(map).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        reference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.child("quiz_point").exists()) {
                                    int i = dataSnapshot.child("quiz_point").getValue(Integer.class);
                                    reference.child("quiz_point").setValue(i + 2);

                                } else {
                                    reference.child("quiz_point").setValue(2);
                                }
                                if (dataSnapshot.child("comment_point").exists()) {
                                    int i = dataSnapshot.child("comment_point").getValue(Integer.class);
                                    reference.child("comment_point").setValue(i + 2);
                                } else reference.setValue(2);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }

                        }
                        );


                    }
                });
                mainScrollView .post(() -> mainScrollView.fullScroll(ScrollView.FOCUS_DOWN));
            }
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
            builder.setTitle("Login Required");
            builder.setMessage("You need to login to use the feature");
            builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    getApplicationContext().startActivity(new Intent(getApplicationContext(), SignInActivity.class));
                }
            });
            builder.setNegativeButton("cancel", null);
            builder.show();

        }
    }


}

