package com.focosee.qingshow.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;
import com.android.volley.Response;
import com.focosee.qingshow.QSApplication;
import com.focosee.qingshow.R;
import com.focosee.qingshow.command.Callback;
import com.focosee.qingshow.command.SystemCommand;
import com.focosee.qingshow.command.UserCommand;
import com.focosee.qingshow.constants.config.QSAppWebAPI;
import com.focosee.qingshow.httpapi.gson.QSGsonFactory;
import com.focosee.qingshow.httpapi.request.QSJsonObjectRequest;
import com.focosee.qingshow.httpapi.request.RequestQueueManager;
import com.focosee.qingshow.httpapi.response.MetadataParser;
import com.focosee.qingshow.httpapi.response.dataparser.CategoryParser;
import com.focosee.qingshow.model.CategoriesModel;
import com.focosee.qingshow.model.QSModel;
import com.focosee.qingshow.model.vo.mongo.MongoCategories;
import com.focosee.qingshow.model.vo.mongo.MongoPeople;
import com.focosee.qingshow.util.AppUtil;
import com.focosee.qingshow.util.ValueUtil;
import com.focosee.qingshow.util.exception.CrashHandler;
import com.google.gson.Gson;
import com.umeng.analytics.MobclickAgent;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;
import cn.jpush.android.api.InstrumentedActivity;

public class LaunchActivity extends InstrumentedActivity {

    private Class _class;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFormat(PixelFormat.RGBA_8888);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DITHER);
        //友盟接口
//        MobclickAgent.updateOnlineConfig(this);

        String api_name = QSApplication.instance().getPreferences().getString(QSAppWebAPI.host_name, "");
        String api_payment = QSApplication.instance().getPreferences().getString(QSAppWebAPI.host_address_payment, "");
        String api_appweb = QSApplication.instance().getPreferences().getString(QSAppWebAPI.host_address_appweb, "");

        if(TextUtils.isEmpty(api_name) || TextUtils.isEmpty(api_payment) || TextUtils.isEmpty(api_appweb)){
            systemGet();
        }else{
            init();
            getUser();
        }

        systemLog();

        setContentView(R.layout.activity_launch);
    }

    private void init(){
        QSAppWebAPI.HOST_ADDRESS_PAYMENT = QSApplication.instance().getPreferences().getString(QSAppWebAPI.host_address_payment, "");
        QSAppWebAPI.HOST_ADDRESS_APPWEB = QSApplication.instance().getPreferences().getString(QSAppWebAPI.host_address_appweb, "");
        String deviceUid = QSApplication.instance().getPreferences().getString("deviceUid", "");
        if ("".equals(deviceUid) || !deviceUid.equals(((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId())) {
            userFollow();
            _class = G02WelcomeActivity.class;
        } else {
            _class = S01MatchShowsActivity.class;
        }

        String id = QSApplication.instance().getPreferences().getString("id", "");
        if (TextUtils.isEmpty(id)) {
            MongoPeople _user = new MongoPeople();
            _user._id = id;
            QSModel.INSTANCE.setUser(_user);
        }
    }

    private void systemLog(){
        if(!TextUtils.isEmpty(QSApplication.instance().getPreferences().getString(ValueUtil.CRASH_LOG,""))){
            Gson gson = QSGsonFactory.create();
            CrashHandler.CrashModel crashModel = gson.fromJson(QSApplication.instance().getPreferences().getString(ValueUtil.CRASH_LOG, ""), CrashHandler.CrashModel.class);
            try {
                SystemCommand.systemLog(new JSONObject(gson.toJson(crashModel)));
            } catch (JSONException e) {
                Log.e(LaunchActivity.class.getSimpleName(), "systemLog:" + e.getMessage());
            }
        }
    }

    private void getCategories() {

        QSJsonObjectRequest jsonObjectRequest = new QSJsonObjectRequest(QSAppWebAPI.getQueryCategories(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (!MetadataParser.hasError(response)) {
                    Map<String, MongoCategories> categoriesMap = new HashMap<>();
                    for (MongoCategories categories : CategoryParser.parseQuery(response)) {
                        categoriesMap.put(categories._id, categories);
                    }
                    CategoriesModel.INSTANCE.setCategories(categoriesMap);
                }
            }
        });
        RequestQueueManager.INSTANCE.getQueue().add(jsonObjectRequest);
    }

    public void jump() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                    handler.sendEmptyMessage(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            Intent mainIntent = new Intent(LaunchActivity.this, _class);
            LaunchActivity.this.startActivity(mainIntent);
            LaunchActivity.this.finish();
            return true;
        }
    });

    private void userFollow() {
        Map params = new HashMap();
        params.put("version", AppUtil.getVersion());
        params.put("deviceUid", ((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId());
        params.put("osType", 1);
        params.put("osVersion", android.os.Build.VERSION.RELEASE);
        JSONObject jsonObject = new JSONObject(params);
        QSJsonObjectRequest jsonObjectRequest = new QSJsonObjectRequest(QSAppWebAPI.getSpreadFirstlanuchApi(), jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (!MetadataParser.hasError(response)) {
                    SharedPreferences.Editor editor = QSApplication.instance().getPreferences().edit();
                    editor.putString("deviceUid",
                            ((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId());
                    editor.commit();
                }

            }
        });
        RequestQueueManager.INSTANCE.getQueue().add(jsonObjectRequest);
    }

    private void systemGet(){

        String url = "http://chinshow.com/services/system/get?client=android&version=" + AppUtil.getVersion();
        QSJsonObjectRequest jsonObjectRequest = new QSJsonObjectRequest(url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(LaunchActivity.class.getSimpleName(), "response:" + response);
                try {
                    QSAppWebAPI.HOST_ADDRESS_PAYMENT = response.getJSONObject("data").getJSONObject("deployment").getString("paymentServiceRoot");
                    QSAppWebAPI.HOST_ADDRESS_APPWEB = response.getJSONObject("data").getJSONObject("deployment").getString("appWebRoot");
                    SharedPreferences.Editor editor = QSApplication.instance().getPreferences().edit();
                    editor.putString(QSAppWebAPI.host_name, response.getJSONObject("data").getJSONObject("deployment").getString("appServiceRoot"));
                    editor.putString(QSAppWebAPI.host_address_payment, QSAppWebAPI.HOST_ADDRESS_PAYMENT);
                    editor.putString(QSAppWebAPI.host_address_appweb, QSAppWebAPI.HOST_ADDRESS_APPWEB);
                    editor.commit();
                    init();
                } catch (JSONException e) {

                }

                getUser();
            }
        });
        RequestQueueManager.INSTANCE.getQueue().add(jsonObjectRequest);
    }

    private void getUser(){
        UserCommand.refresh(new Callback() {
            @Override
            public void onComplete() {
                super.onComplete();
                getCategories();
                jump();
            }

            @Override
            public void onError() {
                super.onError();
                getCategories();
                jump();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
