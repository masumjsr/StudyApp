package com.crezyprogrammer.studylive.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;

import com.crezyprogrammer.studyliveapp.MainActivity;
import com.crezyprogrammer.studyliveapp.R;
import com.crezyprogrammer.studyliveapp.fragment.ListenFragment;
import com.crezyprogrammer.studyliveapp.fragment.QuizPracticeFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class TestFragment extends Fragment {


    @BindView(R.id.imageView3)
    ImageView imageView3;
    @BindView(R.id.reading)
    RelativeLayout reading;
    @BindView(R.id.frameLayout2)
    LinearLayout frameLayout2;
    @BindView(R.id.listen)
    RelativeLayout listen;

    public TestFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_test, container, false);
        ButterKnife.bind(this, view);
        return view;
    }


    @OnClick({R.id.reading, R.id.listen})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.reading:
                QuizPracticeFragment nextFrag = new QuizPracticeFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .add(R.id.main_container, nextFrag, "findThisFragment").hide(((MainActivity) getActivity()).getActive())
                        .addToBackStack(null)

                        .commit();
                break;
            case R.id.listen:

                ListenFragment listenFragment = new ListenFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .add(R.id.main_container, listenFragment, "findThisFragment").hide(((MainActivity) getActivity()).getActive())
                        .addToBackStack(null)

                        .commit();
                break;
        }
    }
}
