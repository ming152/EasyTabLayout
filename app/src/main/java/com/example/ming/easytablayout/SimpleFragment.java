package com.example.ming.easytablayout;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by 17111862 on 2018/2/11.
 */

public class SimpleFragment extends Fragment {
    TextView textView;
    private String mContent;

    public static SimpleFragment newInstance(String content) {

        Bundle args = new Bundle();
        args.putString("content", content);
        SimpleFragment fragment = new SimpleFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_simple, container, false);
        Bundle bundle = getArguments();
        if(bundle != null){
            mContent = bundle.getString("content");
        }
        textView = rootView.findViewById(R.id.fragment_tv);
        textView.setText(mContent);
        return rootView;
    }
}
