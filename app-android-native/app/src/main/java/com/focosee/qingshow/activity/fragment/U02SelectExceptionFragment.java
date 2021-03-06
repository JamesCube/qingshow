package com.focosee.qingshow.activity.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import com.android.volley.RequestQueue;
import com.focosee.qingshow.R;
import com.focosee.qingshow.command.Callback;
import com.focosee.qingshow.command.UserCommand;
import com.focosee.qingshow.httpapi.request.RequestQueueManager;
import com.focosee.qingshow.model.U02Model;
import com.focosee.qingshow.model.vo.mongo.MongoPeople;
import com.google.gson.Gson;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import butterknife.ButterKnife;
import butterknife.InjectView;

public class U02SelectExceptionFragment extends Fragment {
    private static final String[] datas = {"显瘦", "显高", "显身材", "遮臀部", "遮肚腩", "遮手臂"};
    @InjectView(R.id.backTextView)
    ImageButton backTextView;
    @InjectView(R.id.fragment_u02_select_exception_listview)
    ListView listView;


    private Context context;
    private RequestQueue requestQueue;

    private MongoPeople user;
    private ArrayAdapter<String> adapter;
    List<Integer> expectations = new ArrayList<>();

    public U02SelectExceptionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (null != getArguments()) {
            user = (MongoPeople) getArguments().getSerializable("user");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_u02_select_exception, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getActivity().getApplicationContext();
        requestQueue = RequestQueueManager.INSTANCE.getQueue();

        backTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                U02SettingsFragment settingsFragment;
                settingsFragment = new U02SettingsFragment();
                getFragmentManager().beginTransaction().setCustomAnimations(R.anim.push_left_in, 0, 0, 0).
                        replace(R.id.settingsScrollView, settingsFragment).commit();
            }
        });

        adapter = new ArrayAdapter<>(getActivity(), R.layout.item_u02_exception, datas);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setAdapter(adapter);
        setDatas();
    }

    public void setDatas() {
        for (int i : user.expectations) {
            listView.setItemChecked(i, true);
        }
    }

    private void saveUser() {

        for (int i = 0; i < listView.getCount(); i++) {
            if (listView.isItemChecked(i)) {
                if (!expectations.contains(i))
                    expectations.add(i);
            }
        }
        Map params = new HashMap();
        try {
            params.put("expectations", new JSONArray(new Gson().toJson(expectations)));
        } catch (JSONException e) {
        }
        U02Model.INSTANCE.set_class(U02SettingsFragment.class);
        UserCommand.update(params, new Callback());
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("U02SelectExceptionFragment"); //统计页面
    }

    public void onPause() {
        saveUser();
        super.onPause();
        MobclickAgent.onPageEnd("U02SelectExceptionFragment");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
