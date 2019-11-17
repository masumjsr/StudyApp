package com.crezyprogrammer.studyliveapp.fragment;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.crezyprogrammer.studyliveapp.MainActivity;
import com.crezyprogrammer.studyliveapp.Model;
import com.crezyprogrammer.studyliveapp.PostModel;
import com.crezyprogrammer.studyliveapp.R;
import com.crezyprogrammer.studyliveapp.SignInActivity;
import com.devs.readmoreoption.ReadMoreOption;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.paging.DatabasePagingOptions;
import com.firebase.ui.database.paging.FirebaseRecyclerPagingAdapter;
import com.firebase.ui.database.paging.LoadingState;
import com.google.android.material.bottomnavigation.BottomNavigationView;
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
    TextView postEdit;
    @BindView(R.id.profile)
    CircleImageView profile;
    FirebaseUser user;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    MainActivity activity;
    FirebaseRecyclerAdapter adapter;
    String total_like, total_comment;
    String total_like2, total_comment2;
    @BindView(R.id.shimmer_view_container)
    ShimmerFrameLayout shimmerViewContainer;
    @BindView(R.id.swipe)
    SwipeRefreshLayout swipe;
    @BindView(R.id.frameLayout)
    LinearLayout frameLayout;
    @BindView(R.id.divider)
    View divider;
    @BindView(R.id.textView9)
    TextView textView9;
    @BindView(R.id.pin_title)
    TextView pinTitle;
    @BindView(R.id.pin_image)
    ImageView pinImage;
    @BindView(R.id.top)
    LinearLayout top;
    @BindView(R.id.scrollView2)
    ScrollView scrollView2;
    PagedList.Config config;
    FirebaseRecyclerPagingAdapter<PostModel, ViewHolder> mAdapter;
    public HomeFragment() {
        // Required empty public constructor
    }

    //oncreate
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);
        MainActivity activity = ((MainActivity) getActivity());
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        Spinner spinner = toolbar.findViewById(R.id.spinner2);
        BottomNavigationView navBar = getActivity().findViewById(R.id.navigation);
        navBar.setVisibility(View.VISIBLE);
        frameLayout.requestFocus();
        user = FirebaseAuth.getInstance().getCurrentUser();
        postSetup();
        noticeSetup();

        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(manager);
       // recyclerView.setHasFixedSize(true);

        // setupRecycler(FirebaseDatabase.getInstance().getReference("post").orderByChild("updated"));

        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
               setupRecycler(FirebaseDatabase.getInstance().getReference("post").orderByChild("updated"));

            }
        });
        Model.setI(0);


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    ((TextView) parent.getChildAt(0)).setTypeface(Typeface.DEFAULT_BOLD);
                    ((TextView) parent.getChildAt(0)).setTextSize(18);
                } catch (Exception e) {
                    e.printStackTrace();
                }


                if (position == 0)

                    setupRecycler(FirebaseDatabase.getInstance().getReference("post").orderByChild("updated"));
                else {
                    String txt = spinner.getSelectedItem().toString();
                    setupRecycler(FirebaseDatabase.getInstance().getReference("post").orderByChild("category").equalTo(txt));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });


        return view;
    }

    private void setupRecycler1(Query query) {
        config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(2)
                .setPageSize(2)
                .build();
        shimmerViewContainer.startShimmerAnimation();
        DatabasePagingOptions<PostModel> options = new DatabasePagingOptions.Builder<PostModel>()
                .setLifecycleOwner(this)
                .setQuery(query, config, PostModel.class)
                .build();


        mAdapter = new FirebaseRecyclerPagingAdapter<PostModel, ViewHolder>(options) {
            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.post, parent, false));
            }

            @Override
            protected void onBindViewHolder(@NonNull ViewHolder holder,
                                            int position,
                                            @NonNull PostModel postModel) {

                holder.setDetails(postModel.getName(), postModel.getImage(), postModel.getPost_id(), postModel.getText(), postModel.getTime(), postModel.getUser_id(), postModel.getCategory());

            }

            @Override
            protected void onLoadingStateChanged(@NonNull LoadingState state) {
                switch (state) {
                    case LOADING_INITIAL:
                    case LOADING_MORE:
                        // Do your loading animation
                    //    swipe.setRefreshing(true);
                        break;

                    case LOADED:
                        // Stop Animation
                        shimmerViewContainer.stopShimmerAnimation();
                        shimmerViewContainer.setVisibility(View.GONE);
                        swipe.setRefreshing(false);
                        break;

                    case FINISHED:
                        //Reached end of Data set
                        swipe.setRefreshing(false);
                        break;

                    case ERROR:
                        retry();
                        break;
                }
            }

            @Override
            protected void onError(@NonNull DatabaseError databaseError) {
                super.onError(databaseError);
                swipe.setRefreshing(false);
                databaseError.toException().printStackTrace();
            }
        };

        //Set Adapter to RecyclerView
     //   mAdapter.startListening();
        recyclerView.setAdapter(mAdapter);


    }

        private void noticeSetup () {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("admin/pin");
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    pinTitle.setText(dataSnapshot.child("title").getValue().toString());
                    Picasso.get().load(dataSnapshot.child("image").getValue().toString()).into(pinImage);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }


    private void setupRecycler(Query query) {
        shimmerViewContainer.startShimmerAnimation();


        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<PostModel>().setQuery(
                query, snapshot -> new PostModel(snapshot.child("name").getValue().toString(), snapshot.child("image").getValue().toString(), snapshot.child("post_id").getValue().toString(),
                        snapshot.child("text").getValue().toString(), snapshot.child("user_id").getValue().toString(), snapshot.child("category").getValue().toString(), snapshot.child("time").getValue(Long.class))).build();

        adapter = new FirebaseRecyclerAdapter<PostModel, ViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolder viewHolder, int i, @NonNull PostModel postModel) {

                viewHolder.setDetails(postModel.getName(), postModel.getImage(), postModel.getPost_id(), postModel.getText(), postModel.getTime(), postModel.getUser_id(), postModel.getCategory());
            }


            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.post, parent, false));
            }

            @Override
            public void onDataChanged() {
                //   Toast.makeText(getActivity(), "finish", Toast.LENGTH_SHORT).show();
                shimmerViewContainer.stopShimmerAnimation();
                shimmerViewContainer.setVisibility(View.GONE);
                swipe.setRefreshing(false);
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

   public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name_txt, time_txt, text_txt, comment_text, like_text, category_text,like_text2;
        ImageView photo, comment_icon, like_icon, more,like_icon2;
        boolean liked = false;
        boolean liked2 = false;
        PowerMenu powerMenu;
      CardView like,improve,comment;


        OnMenuItemClickListener<PowerMenuItem> onMenuItemClickListener;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            category_text = itemView.findViewById(R.id.category);
            more = itemView.findViewById(R.id.more);
            name_txt = itemView.findViewById(R.id.post_name);
            text_txt = itemView.findViewById(R.id.post_text);
            like_icon = itemView.findViewById(R.id.like_icon);
            like_icon2 = itemView.findViewById(R.id.like_icon2);
            time_txt = itemView.findViewById(R.id.post_time);
            photo = itemView.findViewById(R.id.post_icon);
            comment_icon = itemView.findViewById(R.id.comment_icon);
            comment_text = itemView.findViewById(R.id.post_comment);
            like_text = itemView.findViewById(R.id.post_like);
            like_text2 = itemView.findViewById(R.id.post_like2);
            like=itemView.findViewById(R.id.like);
            improve=itemView.findViewById(R.id.improve);
            comment=itemView.findViewById(R.id.comment);


        }

        public void setDetails(String name, String image, String post_id, String text, long time, String user_id, String category) {
            name_txt.setText(name);
           // text_txt.setText(Html.fromHtml(text));
            time_txt.setText(reformat(time));
            category_text.setText(category);

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

            //readMoreOption.addReadMoreTo(text_txt, Html.fromHtml(text));
            text_txt.setText(Html.fromHtml(text));

            DatabaseReference comment_count = FirebaseDatabase.getInstance().getReference("post").child(post_id).child("comment");
            comment_count.addValueEventListener(new ValueEventListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    try {
                        total_comment = dataSnapshot.getChildrenCount() + "";
                        comment_text.setText(total_comment);
                    } catch (Exception e) {
                        Toast.makeText(getActivity(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }

                }


                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            DatabaseReference like_count = FirebaseDatabase.getInstance().getReference("post").child(post_id).child("like");
            like_count.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    total_like = dataSnapshot.getChildrenCount() + "";
                    like_text.setText(total_like);
                    if (user != null) {


                        if (dataSnapshot.child(user.getUid() + "").exists()) {
                            liked = true;
                            like_icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_heart2));

                        }
                        else {
                            liked=false;
                            like_icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_like));
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });



 DatabaseReference like_count2 = FirebaseDatabase.getInstance().getReference("post").child(post_id).child("like2");
            like_count2.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    total_like2 = dataSnapshot.getChildrenCount() + "";
                    like_text2.setText(total_like2);
                    if (user != null) {


                        if (dataSnapshot.child(user.getUid() ).exists()) {
                            liked2 = true;
                            like_icon2.setImageDrawable(getResources().getDrawable(R.drawable.l2));

                        }
                        else {
                            liked2=false;
                            like_icon2.setImageDrawable(getResources().getDrawable(R.drawable.l1));
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });



            like.setOnClickListener(v ->

            {Log.i("123321",post_id);
                ProgressDialog progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage("loading");
                progressDialog.show();
                if (user != null) {
                    DatabaseReference like_database = FirebaseDatabase.getInstance().getReference("post").child(post_id).child("like").child(user.getUid());


//                like_icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_thumb_up_black_24dp));
                    if (liked) {
                        like_icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_like));
                        liked = false;
                        like_database.removeValue();


                    } else {
                        liked = true;
                        like_database.child(user_id).setValue("true");
                        like_icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_heart2));
                        like_database.setValue(true);

                    }
                    progressDialog.dismiss();

                } else {
                    progressDialog.dismiss();
                    Builder builder = new Builder(getActivity());
                    builder.setTitle("Login Required");
                    builder.setMessage("You need to login to use the feature");
                    builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            getActivity().startActivity(new Intent(getActivity(), SignInActivity.class));
                        }
                    });
                    builder.setNegativeButton("cancel", null);
                    builder.show();

                }

            });



            improve.setOnClickListener(v ->

            {
                ProgressDialog progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage("loading");
                progressDialog.show();
                if (user != null) {
                    DatabaseReference like_database2 = FirebaseDatabase.getInstance().getReference("post").child(post_id).child("like2").child(user.getUid());


//                like_icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_thumb_up_black_24dp));
                    if (liked2) {

                        like_icon2.setImageDrawable(getResources().getDrawable(R.drawable.l1));
                        liked2 = false;
                        like_database2.removeValue();


                  }
                    else {
                        liked2 = true;
                        like_database2.child(user_id).setValue("true");
                        like_icon2.setImageDrawable(getResources().getDrawable(R.drawable.l2));


                    }
                    progressDialog.dismiss();

                } else {
                    progressDialog.dismiss();
                    Builder builder = new Builder(getActivity());
                    builder.setTitle("Login Required");
                    builder.setMessage("You need to login to use the feature");
                    builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            getActivity().startActivity(new Intent(getActivity(), SignInActivity.class));
                        }
                    });
                    builder.setNegativeButton("cancel", null);
                    builder.show();

                }

            });


            photo.setOnClickListener(v -> intentUser( user_id,name,image));



            Picasso.get().load(image).placeholder(R.drawable.ic_user).into(photo);
            time_txt.setText(reformat(time));
            comment.setOnClickListener(v -> {

                MainActivity activity = (MainActivity) (getActivity());
                activity.setComment(true);
                PostDetailFragment nextFrag = new PostDetailFragment();

                Intent intent = new Intent(getActivity(), PostDetailFragment.class);
                intent.putExtra("text", text);
                intent.putExtra("name", name);
                intent.putExtra("image", image);
                intent.putExtra("post_id", post_id);
                intent.putExtra("total_comment", total_comment);
                intent.putExtra("total_like", total_like);
                intent.putExtra("time", reformat(time));
                intent.putExtra("user_id",user_id);
                getActivity().startActivity(intent);


                // OR using options to customize




            });

            more.setOnClickListener(v ->
            {
                Context context = getActivity();

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
                if (user.getUid().equals(user_id)) {
                    powerMenu.addItem(new PowerMenuItem("Edit"));
                    powerMenu.addItem(new PowerMenuItem("Delete"));
                } else powerMenu.addItem(new PowerMenuItem("Report"));
                powerMenu.showAsDropDown(v);
            });

            onMenuItemClickListener = (position, item) -> {
        //        Toast.makeText(getActivity(), item.getTitle() + position, Toast.LENGTH_SHORT).show();
                powerMenu.setSelectedPosition(position); // change selected item
                switch (item.getTitle()) {
                    case "Report": {
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("admin/report").child(post_id);
                        databaseReference.child("user_id").setValue(user.getUid());
                        Toast.makeText(getActivity(), "Report Success", Toast.LENGTH_SHORT).show();
                    }
                    break;
                    case "Delete":
                        Builder builder = new Builder(getActivity());
                        builder.setTitle("Delete Post");
                        builder.setMessage("Are you sure");
                        builder.setPositiveButton("ok", (dialog, which) -> {
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("post").child(post_id);
                            databaseReference.removeValue();
                            Toast.makeText(getActivity(), "Post Deleted", Toast.LENGTH_SHORT).show();
                        });
                        builder.setNegativeButton("cancel", null);
                        builder.show();
                        break;
                    case "Edit": {
                        final AlertDialog dialogBuilder = new Builder(getActivity()).create();
                        LayoutInflater inflater = getActivity().getLayoutInflater();
                        View dialogView = inflater.inflate(R.layout.post_dialog, null);

                        final EditText editText = (EditText) dialogView.findViewById(R.id.write);
                        final Spinner spinner = dialogView.findViewById(R.id.spinner);
                        TextView button1 = dialogView.findViewById(R.id.post_button);
                        ImageView button2 = dialogView.findViewById(R.id.close);
                        editText.setText(text);

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

                            String text1 = editText.getText().toString();
                            if (!text.isEmpty() && spinner.getSelectedItemPosition() != 0) {

                                Map<String, Object> map = new HashMap<>();
                                Long updated = 9999999999999L;
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("post").child(post_id);
                                databaseReference.child("time").setValue(time);
                                databaseReference.child("text").setValue(text1);
                                databaseReference.child("updated").setValue(updated - System.currentTimeMillis());
                                databaseReference.child("category").setValue(spinner.getSelectedItem().toString());
                                databaseReference.child("name").setValue(user.getDisplayName());
                                Toast.makeText(getActivity(), "Successfully posted", Toast.LENGTH_SHORT).show();

                                dialogBuilder.dismiss();
                                progressDialog.dismiss();

                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(getActivity(), "Please Write Your Message and select a category", Toast.LENGTH_SHORT).show();
                            }

                        });


                        dialogBuilder.setView(dialogView);
                        dialogBuilder.show();
                    }


                    break;
                }
                powerMenu.dismiss();
            };
        }

        private void intentUser(String user_id,String name,String image) {

        Intent intent=new Intent(getActivity(),UserActivity.class);
        intent.putExtra("user_id",user_id);
        intent.putExtra("user_name",name);
        intent.putExtra("user_profile",image);
        getActivity().startActivity(intent);
        }

        private String reformat(long time) {
            long system = System.currentTimeMillis();
            long difference = system - time;
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
        if (user != null) {


            Picasso.get().load(user.getPhotoUrl()).placeholder(R.drawable.ic_user).into(profile);
        }
    }

    @OnClick(R.id.post_edit)
    public void onViewClicked() {
        if (user != null) {
            final AlertDialog dialogBuilder = new Builder(getActivity()).create();
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.post_dialog, null);

            final EditText editText = (EditText) dialogView.findViewById(R.id.write);
            final Spinner spinner = dialogView.findViewById(R.id.spinner);
            TextView button1 = dialogView.findViewById(R.id.post_button);
            ImageView button2 = dialogView.findViewById(R.id.close);

            button2.setOnClickListener(view -> dialogBuilder.dismiss());
            button1.setOnClickListener(view -> {
                // DO SOMETHINGS
                ProgressDialog progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage("loading");
                progressDialog.show();
                long time = System.currentTimeMillis();
                String text = editText.getText().toString().replaceAll("\\n", "<br />");
                if (!text.isEmpty() && spinner.getSelectedItemPosition() != 0) {

                    Map<String, Object> map = new HashMap<>();
                    Long updated = 9999999999999L;
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("post").child((updated - time) + "");
                    map.put("time", time);
                    map.put("text", text);
                    map.put("post_id", databaseReference.getKey());
                    map.put("updated", updated - time);
                    map.put("category", spinner.getSelectedItem().toString());
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
                                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.child("quiz_point").exists()) {
                                                int i = dataSnapshot.child("quiz_point").getValue(Integer.class);
                                                reference.child("quiz_point").setValue(i + 1);

                                            } else {
                                                reference.child("quiz_point").setValue(1);
                                            }

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                }
                            });
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), "Please Write Your Message and select a category", Toast.LENGTH_SHORT).show();
                }

            });


            dialogBuilder.setView(dialogView);
            dialogBuilder.show();
        } else {
            Builder builder = new Builder(getActivity());
            builder.setTitle("Login Required");
            builder.setMessage("You need to login to use the feature");
            builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    getActivity().startActivity(new Intent(getActivity(), SignInActivity.class));
                }
            });
            builder.setNegativeButton("cancel", null);
            builder.show();

        }
    }



    @Override
    public void onStart() {
        super.onStart();
     //mAdapter.startListening();
    }

    //Stop Listening Adapter
    @Override
    public void onStop() {
        super.onStop();
//        mAdapter.stopListening();
    }}