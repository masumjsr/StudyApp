package com.crezyprogrammer.studyliveapp.fragment;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.crezyprogrammer.studyliveapp.CommentModel;
import com.crezyprogrammer.studyliveapp.PostModel;
import com.crezyprogrammer.studyliveapp.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class PostDetailFragment extends Fragment {


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
    @BindView(R.id.constraintLayout)
    ConstraintLayout constraintLayout;
    @BindView(R.id.post_icon)
    CircleImageView postIcon;
    @BindView(R.id.comment_post)
    TextView postButton2;
    @BindView(R.id.comment_edt)
    EditText editText;
    @BindView(R.id.comment_recycler)
    RecyclerView recyclerView;
    String name,text,post_id,time,image,total_like,total_comment;
    DatabaseReference databaseReference;
    FirebaseUser user;
FirebaseRecyclerAdapter adapter;
    public PostDetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        BottomNavigationView navBar = getActivity().findViewById(R.id.navigation);
        navBar.setVisibility(View.GONE);
user= FirebaseAuth.getInstance().getCurrentUser();
        View view = inflater.inflate(R.layout.fragment_post_detail, container, false);
        ButterKnife.bind(this, view);
        getExtra();
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        LinearLayoutManager manager=new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(manager);
        populateRecycle();
        return view;

    }

    private void populateRecycle() {
        Query query=FirebaseDatabase.getInstance().getReference("post").child(post_id).child("comment");
        FirebaseRecyclerOptions options=new FirebaseRecyclerOptions.Builder<CommentModel>().setQuery(
                query,snapshot -> new CommentModel
                        (snapshot.child("name").getValue().toString(),snapshot.child("profile").getValue().toString(),snapshot.child("text").getValue().toString())
        ).build();
        adapter=new FirebaseRecyclerAdapter<CommentModel,ViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolder viewHolder, int i, @NonNull CommentModel postModel) {
                viewHolder.setDetails(postModel.getName(),postModel.getProfile(),postModel.getText());
            }


            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.comment,parent,false));
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
    public class   ViewHolder extends RecyclerView.ViewHolder {
        TextView name_text,text_text;
        ImageView profile_icon;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name_text=itemView.findViewById(R.id.post_name);
            text_text=itemView.findViewById(R.id.post_text);
            profile_icon=itemView.findViewById(R.id.post_icon);
        }

        public void setDetails(String name, String profile, String text) {
            name_text.setText(name);
            text_text.setText(text);
            Picasso.get().load(profile).placeholder(R.drawable.ic_user).into(profile_icon);
        }
    }

    private void getExtra() {
    name=getArguments().getString("name");
    text=getArguments().getString("text");
    image=getArguments().getString("image");
    time=getArguments().getString("time");
    post_id=getArguments().getString("post_id");
    total_like=getArguments().getString("total_like");
    total_comment=getArguments().getString("total_comment");
    postName.setText(name);
    postTime.setText(time);
    postText.setText(text);
    postLike.setText(total_like);
    postComment.setText(total_comment);
        Picasso.get().load(image).placeholder(R.drawable.ic_user).into(postIcon);
        databaseReference= FirebaseDatabase.getInstance().getReference("post").child(post_id);
    }


    @OnClick(R.id.comment_post)
    public void onViewClicked() {
        String comment=editText.getText().toString();
        if(!comment.isEmpty()){
            ProgressDialog progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("loading");
            progressDialog.show();
            Long updated=9999999999999L;
            databaseReference.child("updated").setValue(updated-System.currentTimeMillis());
            DatabaseReference commentReference=databaseReference.child("comment").push();
            Map<String, Object> map = new HashMap<>();
            map.put("name",user.getDisplayName());
            map.put("text",comment);
            map.put("profile",user.getPhotoUrl()+"");
            commentReference.setValue(map).addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), "Success", Toast.LENGTH_SHORT).show();
                }
            });

        }

    }
}
