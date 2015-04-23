package com.focosee.qingshow.activity.fragment;


import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.focosee.qingshow.R;
import com.focosee.qingshow.httpapi.request.RequestQueueManager;

public class U02HelpFragment extends Fragment {
    private Context context;
    private RequestQueue requestQueue;

    private TextView backTextView;


    public U02HelpFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_u02_help, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = (Context) getActivity().getApplicationContext();
        requestQueue = RequestQueueManager.INSTANCE.getQueue();

        backTextView = (TextView) getActivity().findViewById(R.id.backTextView);
        backTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                U02SettingsFragment settingsFragment = new U02SettingsFragment();
                getFragmentManager().beginTransaction().replace(R.id.settingsScrollView, settingsFragment).commit();
            }
        });
    }

}