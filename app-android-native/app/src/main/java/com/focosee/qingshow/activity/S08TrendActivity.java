package com.focosee.qingshow.activity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.focosee.qingshow.R;
import com.focosee.qingshow.adapter.S08TrendListAdapter;
import com.focosee.qingshow.constants.config.QSAppWebAPI;
import com.focosee.qingshow.model.vo.mongo.MongoPreview;
import com.focosee.qingshow.httpapi.request.RequestQueueManager;
import com.focosee.qingshow.httpapi.response.dataparser.PreviewParser;
import com.focosee.qingshow.httpapi.request.QSJsonObjectRequest;
import com.focosee.qingshow.util.AppUtil;
import com.focosee.qingshow.widget.MPullRefreshListView;
import com.focosee.qingshow.widget.PullToRefreshBase;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

public class S08TrendActivity extends BaseActivity {

    private final String TAG = "S08TrendActivity";

    private MPullRefreshListView mPullRefreshListView;
    private ListView listView;

    private S08TrendListAdapter adapter;
    private int _currentPageIndex = 1;
    private ImageView _backImageBtn;


    private SimpleDateFormat _mDateFormat = new SimpleDateFormat("MM-dd HH:mm");

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(S04CommentActivity.COMMENT_NUM_CHANGE.equals(intent.getAction())){
                int numComments = adapter.getData().get(intent.getIntExtra("position", 0)).getNumComments();
                adapter.getData().get(intent.getIntExtra("position", 0)).__context.numComments = numComments + intent.getIntExtra("value",0);
                adapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_s08_trend);

        mPullRefreshListView = (MPullRefreshListView) findViewById(R.id.S08_content_list_view);
        listView = mPullRefreshListView.getRefreshableView();

        _backImageBtn = (ImageView) findViewById(R.id.S08_back_image_button);
        _backImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        adapter = new S08TrendListAdapter(this, new LinkedList<MongoPreview>(), getScreenSize(), ImageLoader.getInstance());
        listView.setAdapter(adapter);
        listView.setSmoothScrollbarEnabled(false);

        mPullRefreshListView.setPullRefreshEnabled(true);
        mPullRefreshListView.setPullLoadEnabled(true);
        mPullRefreshListView.setScrollLoadEnabled(true);
        mPullRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                doRefreshTask();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                doGetMoreTask();
            }
        });
        mPullRefreshListView.doPullRefreshing(true,500);
        registerReceiver(receiver, new IntentFilter(S04CommentActivity.COMMENT_NUM_CHANGE));
    }

    @Override
    public void reconn() {
        doRefreshTask();
    }

    private void setLastUpdateTime() {
        String text = formatDateTime(System.currentTimeMillis());
        mPullRefreshListView.setLastUpdatedLabel(text);
    }

    private String formatDateTime(long time) {
        if (0 == time) {
            return "";
        }

        return _mDateFormat.format(new Date(time));
    }

    private void doRefreshTask() {
        _getDataFromNet(true, "1", "10");
    }

    private void doGetMoreTask() {
        _getDataFromNet(false, String.valueOf(_currentPageIndex+1), "10");
    }

    private void _getDataFromNet(boolean refreshSign, String pageNo, String pageSize) {
        final boolean _tRefreshSign = refreshSign;
        QSJsonObjectRequest jor = new QSJsonObjectRequest(QSAppWebAPI.getPreviewTrendListApi(Integer.valueOf(pageNo), Integer.valueOf(pageSize)), null, new Response.Listener<JSONObject>(){
            @Override
            public void onResponse(JSONObject response) {
                try{
                    LinkedList<MongoPreview> results = PreviewParser.parseFeed(response);
                    if (_tRefreshSign) {
                       adapter.resetData(results);
                        _currentPageIndex = 1;
                    } else {
                        adapter.addItemLast(results);
                        _currentPageIndex++;
                    }
                    adapter.notifyDataSetChanged();
                    mPullRefreshListView.onPullDownRefreshComplete();
                    mPullRefreshListView.onPullUpRefreshComplete();
                    mPullRefreshListView.setHasMoreData(true);
                    setLastUpdateTime();

                }catch (Exception error){
                    Log.i("test", "error" + error.toString());
                    //Toast.makeText(getApplication(), "Error:" + error.getMessage().toString(), Toast.LENGTH_SHORT).show();
                    Toast.makeText(getApplication(), "已经是最后一页了", Toast.LENGTH_SHORT).show();
                    mPullRefreshListView.onPullDownRefreshComplete();
                    mPullRefreshListView.onPullUpRefreshComplete();
                    mPullRefreshListView.setHasMoreData(false);
                }

            }
        });
        //Toast.makeText(this,jor.get,Toast.LENGTH_LONG).show();
        RequestQueueManager.INSTANCE.getQueue().add(jor);


    }

    private Point getScreenSize(){
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }

    private int getScreenWidth(){
        return getScreenSize().x;
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("S08Trend"); //统计页面
        MobclickAgent.onResume(this);          //统计时长
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("S08Trend"); // 保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息
        MobclickAgent.onPause(this);
    }

}
