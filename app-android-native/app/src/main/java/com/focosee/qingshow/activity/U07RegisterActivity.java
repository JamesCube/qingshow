package com.focosee.qingshow.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.focosee.qingshow.R;
import com.focosee.qingshow.app.QSApplication;
import com.focosee.qingshow.config.QSAppWebAPI;
import com.focosee.qingshow.entity.LoginResponse;
import com.focosee.qingshow.entity.RegisterResponse;
import com.focosee.qingshow.entity.UpdateResponse;
import com.focosee.qingshow.error.ErrorHandler;
import com.focosee.qingshow.request.QXStringRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;


public class U07RegisterActivity extends Activity {
    private RequestQueue requestQueue;

    private Button submitButton;
    private EditText accountEditText;
    private EditText passwordEditText;
    private EditText confirmEditText;
    private RadioGroup sexRadioGroup;
    private RadioGroup clothesSizeRadioGroup;
    private RadioGroup shoesSizeRadioGroup;

    private SharedPreferences sharedPreferences;
    private Context context;

    private Gson gson;

    String rawCookie = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        context = getApplicationContext();
        sharedPreferences = getSharedPreferences("personal", Context.MODE_PRIVATE);

        submitButton = (Button) findViewById(R.id.submitButton);
        accountEditText = (EditText) findViewById(R.id.accountEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        confirmEditText = (EditText) findViewById(R.id.confirmEditText);
        sexRadioGroup = (RadioGroup) findViewById(R.id.sexRadioGroup);
        clothesSizeRadioGroup = (RadioGroup) findViewById(R.id.clothesSizeRadioGroup);
        shoesSizeRadioGroup = (RadioGroup) findViewById(R.id.shoesSizeRadioGroup);

        TextView backTextView = (TextView) findViewById(R.id.backTextView);
        backTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        requestQueue = Volley.newRequestQueue(context);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!passwordEditText.getText().toString().equals(confirmEditText.getText().toString())) {
                    Toast.makeText(context, "请确认两次密码是否一致", Toast.LENGTH_LONG).show();
                } else {
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, QSAppWebAPI.REGISTER_SERVICE_URL, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            RegisterResponse registerResponse = new Gson().fromJson(response, new TypeToken<RegisterResponse>() {
                            }.getType());

                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("id", accountEditText.getText().toString());
                            editor.putString("password", passwordEditText.getText().toString());
                            editor.putString("Cookie", rawCookie);

                            if (registerResponse == null || registerResponse.data == null) {
                                if (registerResponse == null) {
                                    Toast.makeText(context, "请重新尝试", Toast.LENGTH_LONG).show();
                                } else {
                                    ErrorHandler.handle(context, registerResponse.metadata.error);
                                }
                            } else {
                                //QSApplication.get().setPeople(registerResponse.data.people);
                                updateSettings();
                                Toast.makeText(context, "注册成功", Toast.LENGTH_LONG).show();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(context, "请重新尝试", Toast.LENGTH_LONG).show();
                        }
                    }) {
                        @Override
                        protected Response<String> parseNetworkResponse(NetworkResponse response) {
                            try {
                                Map<String, String> responseHeaders = response.headers;
                                rawCookie = responseHeaders.get("Set-Cookie").split(";")[0];//.split("=")[1];
                                String dataString = new String(response.data, "UTF-8");
                                return Response.success(dataString, HttpHeaderParser.parseCacheHeaders(response));
                            } catch (UnsupportedEncodingException e) {
                                return Response.error(new ParseError(e));
                            }
                        }
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> map = new HashMap<String, String>();
                            map.put("id", accountEditText.getText().toString());
                            map.put("password", passwordEditText.getText().toString());

                            return map;
                        }
                    };
                    requestQueue.add(stringRequest);
                }
            }
        });
    }

    private int getSexRadioButtonVal() {
        int whichChecked = sexRadioGroup.getCheckedRadioButtonId();
        int result = -1;
        switch (whichChecked) {
            case R.id.femaleRadioButton:
                result = 0;
                break;
            case R.id.maleButton:
                result = 1;
                break;
            default:
                break;
        }
        return result;
    }

    private int getClothesSizeRadioButtonVal() {
        int whichChecked = clothesSizeRadioGroup.getCheckedRadioButtonId();
        int result = -1;
        switch (whichChecked) {
            case R.id.xxsSizeRadioButton:
                result = 0;
                break;
            case R.id.xsSizeRadioButton:
                result = 1;
                break;
            case R.id.sSizeRadioButton:
                result = 2;
                break;
            case R.id.mSizeRadioButton:
                result = 3;
                break;
            case R.id.lSizeRadioButton:
                result = 4;
                break;
            case R.id.xlSizeRadioButton:
                result = 5;
                break;
            case R.id.xxlSizeRadioButton:
                result = 6;
                break;
            case R.id.xxxlSizeRadioButton:
                result = 7;
                break;
            default:
                break;
        }
        return result;
    }

    private int getShoesSizeRadioButtonVal() {
        int whichChecked = shoesSizeRadioGroup.getCheckedRadioButtonId();
        int result = -1;
        switch (whichChecked) {
            case R.id.size34RadioButton:
                result = 0;
                break;
            case R.id.size35RadioButton:
                result = 1;
                break;
            case R.id.size36RadioButton:
                result = 2;
                break;
            case R.id.size37RadioButton:
                result = 3;
                break;
            case R.id.size38RadioButton:
                result = 4;
                break;
            case R.id.size39RadioButton:
                result = 5;
                break;
            case R.id.size40RadioButton:
                result = 6;
                break;
            case R.id.size41RadioButton:
                result = 7;
                break;
            case R.id.size42RadioButton:
                result = 8;
                break;
            case R.id.size43RadioButton:
                result = 9;
                break;
            case R.id.size44RadioButton:
                result = 10;
                break;
            default:
                break;
        }
        return whichChecked;
    }

    private void updateSettings() {
        int gender = getSexRadioButtonVal();
        int clothesSize = getClothesSizeRadioButtonVal();
        int shoesSize = getShoesSizeRadioButtonVal();

        Map<String, String> params = new HashMap<String, String>();
        if (gender >= 0)
            params.put("gender", gender+"");
        if (clothesSize >= 0)
            params.put("clothingSize", clothesSize+"");
        if (shoesSize >= 0)
            params.put("shoeSize", shoesSize+"");

        QXStringRequest qxStringRequest = new QXStringRequest(params, Request.Method.POST, QSAppWebAPI.UPDATE_SERVICE_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                UpdateResponse updateResponse = new Gson().fromJson(response, new TypeToken<UpdateResponse>() {
                }.getType());

                if (updateResponse == null || updateResponse.data == null) {
                    if (updateResponse == null) {
                        Toast.makeText(context, "请重新尝试", Toast.LENGTH_LONG).show();
                    } else {
                        ErrorHandler.handle(context, updateResponse.metadata.error);
                    }
                } else {
                    //QSApplication.get().setPeople(updateResponse.data.people);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "请检查网络", Toast.LENGTH_LONG).show();
            }
        });
        requestQueue.add(qxStringRequest);
    }

}
