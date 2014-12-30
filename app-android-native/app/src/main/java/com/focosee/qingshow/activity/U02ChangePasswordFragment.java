package com.focosee.qingshow.activity;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.focosee.qingshow.R;
import com.focosee.qingshow.config.QSAppWebAPI;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class U02ChangePasswordFragment extends Fragment {
    private Context context;
    private RequestQueue requestQueue;
    private SharedPreferences sharedPreferences;

    private EditText currentPasswordEditText;
    private EditText newPasswordEditText;
    private EditText confirmPasswordEditText;
    private TextView saveTextView;
    private TextView backTextView;

    public U02ChangePasswordFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_u02_change_password, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = (Context) getActivity().getApplicationContext();
        requestQueue = Volley.newRequestQueue(context);
        sharedPreferences = getActivity().getSharedPreferences("personal", Context.MODE_PRIVATE);

        currentPasswordEditText = (EditText) getActivity().findViewById(R.id.currentPasswordEditText);
        newPasswordEditText = (EditText) getActivity().findViewById(R.id.newPasswordEditText);
        confirmPasswordEditText = (EditText) getActivity().findViewById(R.id.confirmPasswordEditText);

        backTextView = (TextView) getActivity().findViewById(R.id.backTextView);
        backTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                U02SettingsFragment settingsFragment = new U02SettingsFragment();
                getFragmentManager().beginTransaction().replace(R.id.settingsScrollView, settingsFragment).commit();
            }
        });

        saveTextView = (TextView) getActivity().findViewById(R.id.saveTextView);
        saveTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!newPasswordEditText.getText().toString().equals(confirmPasswordEditText.getText().toString())) {
                    Toast.makeText(context, "请确认两次输入密码是否一致", Toast.LENGTH_LONG).show();
                } else {
                    HashMap<String, String> params = new HashMap<String, String>();
                    params.put("id", sharedPreferences.getString("id", ""));
                    params.put("password", sharedPreferences.getString("password", ""));
                    params.put("currentPassword", newPasswordEditText.getText().toString());
                    JSONObject jsonObject = new JSONObject(params);

                    JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST,
                            QSAppWebAPI.UPDATE_SERVICE_URL, jsonObject,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    Log.v("TAG", response.toString());
                                    try {
                                        if (response.getJSONObject("data") == null) {
                                            Log.v("TAG", "error");
                                            String errorCode = response.getJSONObject("metadata").getString("error");
                                            Log.v("TAG", "error" + errorCode);
                                            if (errorCode.equals("1001")) {
                                                Toast.makeText(context, "账号或密码错误", Toast.LENGTH_LONG).show();
                                            }
                                        } else {
                                            Toast.makeText(context, "更改成功", Toast.LENGTH_LONG).show();
                                        }
                                    } catch (Exception e) {
                                        Log.v("TAG", "exception");
                                        try {
                                            Log.v("TAG", "error");
                                            String errorCode = response.getJSONObject("metadata").getString("error");
                                            Log.v("TAG", "error" + errorCode);
                                            if (errorCode.equals("1001")) {
                                                Toast.makeText(context, "账号或密码错误", Toast.LENGTH_LONG).show();
                                            }
                                        } catch (Exception e1) {
                                            Log.v("TAG", "e1");
                                        }
                                    }

                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("TAG", error.getMessage(), error);
                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> map = new HashMap<String, String>();
                            map.put("id", sharedPreferences.getString("id", ""));
                            return map;
                        }

                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            String rawCookie = sharedPreferences.getString("Cookie", "");
                            if (rawCookie != null && rawCookie.length() > 0) {
                                HashMap<String, String> headers = new HashMap<String, String>();
                                headers.put("Cookie", rawCookie);
                                headers.put("Accept", "application/json");
                                headers.put("Content-Type", "application/json; charset=UTF-8");
                                return headers;
                            }
                            return super.getHeaders();
                        }
                    };
                    requestQueue.add(stringRequest);
                }
            }
        });
    }
}
