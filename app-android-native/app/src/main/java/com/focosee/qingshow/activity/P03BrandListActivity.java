package com.focosee.qingshow.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.AdapterView;
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
import com.focosee.qingshow.request.MJsonObjectRequest;
import com.focosee.qingshow.widget.MNavigationView;
import com.focosee.qingshow.widget.MPullRefreshListView;
import com.focosee.qingshow.widget.PullToRefreshBase;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONObject;

import java.util.ArrayList;

public class P03BrandListActivity extends Activity {

    private MNavigationView navigationView;
    private MPullRefreshListView pullRefreshListView;
    private ListView listView;
    private int brandType = 0;
    private int pageIndex = 1;

    private P03BrandListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_p03_brand_list);

        navigationView = (MNavigationView) findViewById(R.id.P03_brand_list_navigation);
        navigationView.getBtn_left().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                P03BrandListActivity.this.finish();
            }
        });

        findViewById(R.id.P03_brand_list_online_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                brandType = 0;
                refreshData();
            }
        });
        findViewById(R.id.P03_brand_list_offline_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                brandType = 1;
                refreshData();
            }
        });

        pullRefreshListView = (MPullRefreshListView) findViewById(R.id.P03_brand_list_list_view);
        listView = pullRefreshListView.getRefreshableView();

        pullRefreshListView.setPullRefreshEnabled(true);
        pullRefreshListView.setPullLoadEnabled(true);
        pullRefreshListView.setScrollLoadEnabled(true);

        adapter = new P03BrandListAdapter(this, new ArrayList<BrandEntity>(), ImageLoader.getInstance());

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(P03BrandListActivity.this, P04BrandActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(P04BrandActivity.INPUT_BRAND, ((BrandEntity) adapter.getItem(position)));
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

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
        MJsonObjectRequest jsonObjectRequest = new MJsonObjectRequest(QSAppWebAPI.getBrandListApi(String.valueOf(brandType),String.valueOf(pageIndex + 1)),null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if(checkErrorExist(response)){
                    pullRefreshListView.onPullUpRefreshComplete();
                    Toast.makeText(P03BrandListActivity.this, "没有更多了！", Toast.LENGTH_SHORT).show();
                    return;
                }
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
        QSApplication.get().QSRequestQueue().add(jsonObjectRequest);
    }

    private void refreshData() {
        MJsonObjectRequest jsonObjectRequest = new MJsonObjectRequest(QSAppWebAPI.getBrandListApi(String.valueOf(brandType),"1"),null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if(checkErrorExist(response)){
                    Toast.makeText(P03BrandListActivity.this, "没有数据！", Toast.LENGTH_SHORT).show();
                }
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
        QSApplication.get().QSRequestQueue().add(jsonObjectRequest);
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
            brandEntity.cover = "http://img1.imgtn.bdimg.com/it/u=3411049717,3668206888&fm=21&gp=0.jpg";
            tempData.add(brandEntity);
        }
        return tempData;
    }

    private boolean checkErrorExist(JSONObject response) {
        try {
            return ((JSONObject) response.get("metadata")).has("error");
        } catch (Exception e) {
            return true;
        }
    }
}
