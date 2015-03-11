package com.focosee.qingshow.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.focosee.qingshow.R;
import com.focosee.qingshow.adapter.HeadScrollAdapter;
import com.focosee.qingshow.adapter.P03BrandListAdapter;
import com.focosee.qingshow.adapter.U01BrandListAdapter;
import com.focosee.qingshow.constants.config.QSAppWebAPI;
import com.focosee.qingshow.model.vo.mongo.MongoBrand;
import com.focosee.qingshow.model.vo.mongo.MongoPeople;
import com.focosee.qingshow.httpapi.request.RequestQueueManager;
import com.focosee.qingshow.httpapi.response.MetadataParser;
import com.focosee.qingshow.httpapi.response.dataparser.BrandParser;
import com.focosee.qingshow.httpapi.request.QSJsonObjectRequest;
import com.focosee.qingshow.widget.ILoadingLayout;
import com.focosee.qingshow.widget.MPullRefreshListView;
import com.focosee.qingshow.widget.PullToRefreshBase;
import com.nostra13.universalimageloader.core.ImageLoader;
import org.json.JSONObject;
import java.util.ArrayList;

/**
 * Created by rong5690001 on 2015/1/19.
 */
public class U01BrandFragment extends Fragment{

    private HeadScrollAdapter headScrollAdapter;
    public static String ACTION_MESSAGE = "refresh_U01BrandFragment";
    public MPullRefreshListView mPullRefreshListView;
    private U01BrandListAdapter mAdapter;
    private boolean noMoreData = false;
    public ListView brandListView;

    private int _currentPageIndex = 1;
    private MongoPeople people;
    private QSJsonObjectRequest jsonObjectRequest;
    private TextView numToatal;

    private static U01BrandFragment instance;

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if(ACTION_MESSAGE.equals(intent.getAction())){
                doRefreshTask();
            }
            if(U01PersonalActivity.BACKTHEPOSIONONE.equals(intent.getAction())){
                brandListView.smoothScrollToPosition(0);
                headScrollAdapter.setHeadY(0);
            }
        }
    };

    public static U01BrandFragment newInstance(){

//            if (instance == null) {
                instance = new U01BrandFragment();
//            }

            return instance;
    }

    public U01BrandFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {

        }
    }

    @Override
    public void onAttach(Activity activity) {
        activity.registerReceiver(receiver, new IntentFilter(ACTION_MESSAGE));
        activity.registerReceiver(receiver, new IntentFilter(U01PersonalActivity.BACKTHEPOSIONONE));
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        headScrollAdapter = new HeadScrollAdapter(((U01PersonalActivity)getActivity()).headRelativeLayout, getActivity());
        people = ((U01PersonalActivity) getActivity()).getMongoPeople();
        if(people == null){
            people = new MongoPeople();
        }

        View view =  inflater.inflate(R.layout.activity_personal_pager_brand, container, false);

        mPullRefreshListView = (MPullRefreshListView) view.findViewById(R.id.pager_P04_item_list);
        mPullRefreshListView.setOnScrollListener(headScrollAdapter);
        mAdapter = new U01BrandListAdapter(getActivity(), new ArrayList<MongoBrand>(), ImageLoader.getInstance());

        brandListView = mPullRefreshListView.getRefreshableView();
        brandListView.setOnTouchListener(headScrollAdapter);
        brandListView.setAdapter(mAdapter);

        mPullRefreshListView.setPullRefreshEnabled(false);
        mPullRefreshListView.setScrollLoadEnabled(true);

        mPullRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                doGetMoreTask();
            }
        });

        doRefreshTask();

        numToatal = (TextView)getActivity().findViewById(R.id.brandCountTextView);

        return view;
    }

    private void doRefreshTask() {
        _getDataFromNet(true, 1, 10);
    }

    private void doGetMoreTask() {
        _getDataFromNet(false, _currentPageIndex+1, 10);
    }

    private void _getDataFromNet(boolean refreshSign, final int pageNo, int pageSize) {
        final boolean _tRefreshSign = refreshSign;
        jsonObjectRequest = new QSJsonObjectRequest(QSAppWebAPI.getBrandFollowedApi(people.get_id(), pageNo, pageSize), null, new Response.Listener<JSONObject>(){
            @Override
            public void onResponse(JSONObject response) {

                if(MetadataParser.hasError(response)){
                    if(pageNo == 1){
                        noMoreData = true;
                        if(mPullRefreshListView.getFooterLoadingLayout() != null)
                            mPullRefreshListView.getFooterLoadingLayout().setState(ILoadingLayout.State.NONE);
                    }else {
                        if(noMoreData){
                            return;
                        }
                        mPullRefreshListView.onPullUpRefreshComplete();
                        mPullRefreshListView.setHasMoreData(false);
                    }
                }

                try{
                    ArrayList<MongoBrand> results = BrandParser.parseQueryBrands(response);
                    if (_tRefreshSign) {
                        numToatal.setText(MetadataParser.getNumTotal(response));
                        mAdapter.resetData(results);
                        _currentPageIndex = 1;
                    } else {
                        mAdapter.addData(results);
                        _currentPageIndex++;
                    }
                    mAdapter.notifyDataSetChanged();
                    mPullRefreshListView.onPullDownRefreshComplete();
                    mPullRefreshListView.onPullUpRefreshComplete();
                    mPullRefreshListView.setHasMoreData(true);

                }catch (Exception error){
                    mPullRefreshListView.onPullUpRefreshComplete();
                    mPullRefreshListView.setHasMoreData(false);
                }

            }
        });
        RequestQueueManager.INSTANCE.getQueue().add(jsonObjectRequest);
    }

    @Override
    public void onPause() {
        if(null != jsonObjectRequest)
            RequestQueueManager.INSTANCE.getQueue().cancelAll(jsonObjectRequest);
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        if(null != jsonObjectRequest)
            RequestQueueManager.INSTANCE.getQueue().cancelAll(jsonObjectRequest);
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(receiver);
        super.onDestroy();
    }
}