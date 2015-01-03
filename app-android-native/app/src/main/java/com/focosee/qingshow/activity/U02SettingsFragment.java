package com.focosee.qingshow.activity;


import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.focosee.qingshow.R;
import com.focosee.qingshow.app.QSApplication;
import com.focosee.qingshow.config.QSAppWebAPI;
import com.focosee.qingshow.entity.LoginResponse;
import com.focosee.qingshow.entity.People;
import com.focosee.qingshow.entity.UpdateResponse;
import com.focosee.qingshow.error.ErrorHandler;
import com.focosee.qingshow.request.QXStringRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class U02SettingsFragment extends Fragment {
    private Context context;
    private RequestQueue requestQueue;
    private SharedPreferences sharedPreferences;

    private TextView backTextView;
    private TextView saveTextView;
    private RelativeLayout personalRelativeLayout;
    private RelativeLayout backgroundRelativeLayout;
    private RelativeLayout sexRelativeLayout;
    private RelativeLayout hairRelativeLayout;
    private RelativeLayout changePasswordRelativeLayout;
    private RelativeLayout changeEmailRelativeLayout;
    private RelativeLayout informRelativeLayout;
    private RelativeLayout rulesRelativeLayout;
    private RelativeLayout helpRelativeLayout;
    private RelativeLayout aboutVIPRelativeLayout;

    private EditText nameEditText;
    private EditText ageEditText;
    private EditText heightEditText;
    private EditText weightEditText;

    public U02SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_u02_settings, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = (Context) getActivity().getApplicationContext();
        requestQueue = Volley.newRequestQueue(context);
        sharedPreferences = getActivity().getSharedPreferences("personal", Context.MODE_PRIVATE);

        backTextView = (TextView) getActivity().findViewById(R.id.backTextView);
        backTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });

        personalRelativeLayout = (RelativeLayout) getActivity().findViewById(R.id.personalRelativeLayout);
        personalRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image:/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 1);
            }
        });
        backgroundRelativeLayout = (RelativeLayout) getActivity().findViewById(R.id.backgroundRelativeLayout);
        backgroundRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image:/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 1);
            }
        });

        sexRelativeLayout = (RelativeLayout) getActivity().findViewById(R.id.sexRelativeLayout);
        sexRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                U02SexFragment sexFragment = new U02SexFragment();
                getFragmentManager().beginTransaction().replace(R.id.settingsScrollView, sexFragment).commit();
            }
        });
        hairRelativeLayout = (RelativeLayout) getActivity().findViewById(R.id.hairRelativeLayout);
        hairRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                U02HairFragment hairFragment = new U02HairFragment();
                getFragmentManager().beginTransaction().replace(R.id.settingsScrollView, hairFragment).commit();
            }
        });
        changePasswordRelativeLayout = (RelativeLayout) getActivity().findViewById(R.id.changePasswordRelativeLayout);
        changePasswordRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                U02ChangePasswordFragment fragment = new U02ChangePasswordFragment();
                getFragmentManager().beginTransaction().replace(R.id.settingsScrollView, fragment).commit();
            }
        });
        changeEmailRelativeLayout = (RelativeLayout) getActivity().findViewById(R.id.changeEmailRelativeLayout);
        changeEmailRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                U02ChangeIdFragment fragment = new U02ChangeIdFragment();
                getFragmentManager().beginTransaction().replace(R.id.settingsScrollView, fragment).commit();
            }
        });
        informRelativeLayout = (RelativeLayout) getActivity().findViewById(R.id.informRelativeLayout);
        informRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                U02NoticeFragment fragment = new U02NoticeFragment();
                getFragmentManager().beginTransaction().replace(R.id.settingsScrollView, fragment).commit();
            }
        });
        rulesRelativeLayout = (RelativeLayout) getActivity().findViewById(R.id.rulesRelativeLayout);
        rulesRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                U02TermsFragment hairFragment = new U02TermsFragment();
                getFragmentManager().beginTransaction().replace(R.id.settingsScrollView, hairFragment).commit();
            }
        });
        helpRelativeLayout = (RelativeLayout) getActivity().findViewById(R.id.helpRelativeLayout);
        helpRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                U02HelpFragment fragment = new U02HelpFragment();
                getFragmentManager().beginTransaction().replace(R.id.settingsScrollView, fragment).commit();
            }
        });
        aboutVIPRelativeLayout = (RelativeLayout) getActivity().findViewById(R.id.aboutVIPRelativeLayout);
        aboutVIPRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                U02AboutVIPFragment fragment = new U02AboutVIPFragment();
                getFragmentManager().beginTransaction().replace(R.id.settingsScrollView, fragment).commit();
            }
        });

        nameEditText = (EditText) getActivity().findViewById(R.id.nameEditText);
        ageEditText = (EditText) getActivity().findViewById(R.id.ageEditText);
        heightEditText = (EditText) getActivity().findViewById(R.id.heightEditText);
        weightEditText = (EditText) getActivity().findViewById(R.id.weightEditText);

        saveTextView = (TextView) getActivity().findViewById(R.id.saveTextView);
        saveTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, String> params = new HashMap<String, String>();
                if (!nameEditText.getText().toString().equals(""))
                    params.put("name", nameEditText.getText().toString());
                if (!ageEditText.getText().toString().equals(""))
                    params.put("age", ageEditText.getText().toString());
                if (!heightEditText.getText().toString().equals(""))
                    params.put("height", heightEditText.getText().toString());
                if (!weightEditText.getText().toString().equals(""))
                    params.put("weight", weightEditText.getText().toString());

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
                            QSApplication.get().setPeople(updateResponse.data.people);
                            Toast.makeText(context, "保存成功", Toast.LENGTH_LONG).show();
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
        });

        Button quitButton = (Button) getActivity().findViewById(R.id.quitButton);
        quitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharedPreferences.edit().clear().commit();
                Toast.makeText(context, "已退出登录", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getActivity(), U06LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
                StringRequest stringRequest = new StringRequest(Request.Method.POST, QSAppWebAPI.LOGOUT_SERVICE_URL,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("TAG", error.getMessage(), error);
                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        String rawCookie = sharedPreferences.getString("Cookie", "");
                        if (rawCookie != null && rawCookie.length() > 0) {
                            HashMap<String, String> headers = new HashMap<String, String>();
                            headers.put("Cookie", rawCookie);
                            return headers;
                        }
                        return super.getHeaders();
                    }
                };
                requestQueue.add(stringRequest);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            ContentResolver contentResolver = getActivity().getContentResolver();

        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
