package com.focosee.qingshow.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.focosee.qingshow.R;
import com.focosee.qingshow.adapter.P03BrandListAdapter;
import com.focosee.qingshow.app.QSApplication;
import com.focosee.qingshow.config.QSAppWebAPI;
import com.focosee.qingshow.entity.BrandEntity;
import com.focosee.qingshow.widget.MNavigationView;
import com.focosee.qingshow.widget.MPullRefreshListView;
import com.focosee.qingshow.widget.MPullRefreshMultiColumnListView;
import com.focosee.qingshow.widget.PullToRefreshBase;
import com.huewu.pla.lib.MultiColumnListView;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONObject;

import java.util.ArrayList;

public class P03BrandListActivity extends Activity {

    private MNavigationView navigationView;
    private MPullRefreshListView pullRefreshListView;
    private ListView listView;
    private int pageIndex = 1;

    private P03BrandListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_p03_brand_list);

        navigationView = (MNavigationView) findViewById(R.id.P03_brand_list_navigation);
        pullRefreshListView = (MPullRefreshListView) findViewById(R.id.P03_brand_list_list_view);
        listView = pullRefreshListView.getRefreshableView();

        pullRefreshListView.setPullRefreshEnabled(true);
        pullRefreshListView.setPullLoadEnabled(true);
        pullRefreshListView.setScrollLoadEnabled(true);

        adapter = new P03BrandListAdapter(this, new ArrayList<BrandEntity>(), ImageLoader.getInstance());

        listView.setAdapter(adapter);

        pullRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                refreshData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                loadMoreData();
            }
        });
        pullRefreshListView.doPullRefreshing(true, 0);
    }

    private void loadMoreData() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(QSAppWebAPI.getBrandListApi("0",String.valueOf(pageIndex + 1)),null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                pageIndex++;
                ArrayList<BrandEntity> moreData = BrandEntity.getBrandListFromResponse(response);
                adapter.addData(moreData);
                adapter.notifyDataSetChanged();

                pullRefreshListView.onPullUpRefreshComplete();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                handleErrorMsg(error);
            }
        });
        QSApplication.QSRequestQueue().add(jsonObjectRequest);
    }

    private void refreshData() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(QSAppWebAPI.getBrandListApi("0","1"),null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                pageIndex = 1;
                ArrayList<BrandEntity> newData = BrandEntity.getBrandListFromResponse(response);
                adapter.resetData(newData);
                adapter.notifyDataSetChanged();

                pullRefreshListView.onPullDownRefreshComplete();
                pullRefreshListView.setHasMoreData(true);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                handleErrorMsg(error);
            }
        });
        QSApplication.QSRequestQueue().add(jsonObjectRequest);
    }

    private void handleErrorMsg(VolleyError error) {
        Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show();
        Log.i("P03BrandListActivity", error.toString());
    }

    private ArrayList<BrandEntity> __createFakeData() {
        ArrayList<BrandEntity> tempData = new ArrayList<BrandEntity>();
        for (int i = 0; i < 3; i++) {
            BrandEntity brandEntity = new BrandEntity();
            brandEntity.name = "品牌" + String.valueOf(i);
            brandEntity.logo = "http://img2.imgtn.bdimg.com/it/u=2439868726,3891592022&fm=21&gp=0.jpg";
            brandEntity.slogan = "http://img1.imgtn.bdimg.com/it/u=3411049717,3668206888&fm=21&gp=0.jpg";
            tempData.add(brandEntity);
        }
        return tempData;
    }
}
