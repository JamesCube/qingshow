package com.focosee.qingshow.activity.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.focosee.qingshow.QSApplication;
import com.focosee.qingshow.R;
import com.focosee.qingshow.activity.U06LoginActivity;
import com.focosee.qingshow.activity.U09TradeListActivity;
import com.focosee.qingshow.activity.U10AddressListActivity;
import com.focosee.qingshow.command.Callback;
import com.focosee.qingshow.command.UserCommand;
import com.focosee.qingshow.constants.config.QSAppWebAPI;
import com.focosee.qingshow.httpapi.request.QSMultipartEntity;
import com.focosee.qingshow.httpapi.request.QSMultipartRequest;
import com.focosee.qingshow.httpapi.request.QSStringRequest;
import com.focosee.qingshow.httpapi.request.RequestQueueManager;
import com.focosee.qingshow.httpapi.response.MetadataParser;
import com.focosee.qingshow.httpapi.response.dataparser.UserParser;
import com.focosee.qingshow.httpapi.response.error.ErrorHandler;
import com.focosee.qingshow.model.QSModel;
import com.focosee.qingshow.model.vo.mongo.MongoPeople;
import com.focosee.qingshow.persist.CookieSerializer;
import com.focosee.qingshow.util.AppUtil;
import com.focosee.qingshow.widget.ActionSheet;
import com.nostra13.universalimageloader.core.ImageLoader;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class U02SettingsFragment extends MenuFragment implements View.OnFocusChangeListener, ActionSheet.ActionSheetListener {

    private static final String[] sexArgs = {"男", "女"};
    private static final String[] bodyTypeArgs = {"A型", "H型", "V型", "X型"};
    private static final String[] dressStyles = {"日韩系", "欧美系"};
    private static final String[] expectations = {"显瘦", "显高", "显身材", "遮臀部", "遮肚腩", "遮手臂"};
    private static final String TAG_BODYTYPE = "bodyType";
    private static final String TAG_SEX = "sex";
    private static final String TAG_DRESSSTYLE = "dressStyle";
    private static final String TAG_EXPECTATIONS = "expectations";
    private static final int TYPE_PORTRAIT = 10000;//上传头像
    private static final int TYPE_BACKGROUD = 10001;//上传背景

    private Context context;
    private RequestQueue requestQueue;

    private RelativeLayout personalRelativeLayout;
    private RelativeLayout backgroundRelativeLayout;
    private RelativeLayout sexRelativeLayout;
    private RelativeLayout bodyTypeRelativeLayout;
    private RelativeLayout changePasswordRelativeLayout;
    private RelativeLayout tradeRelativeLayout;
    private RelativeLayout addresslistRelativeLayout;
    private RelativeLayout dressStyleRelativeLayout;
    private RelativeLayout effectRelativeLayout;

    private ImageView portraitImageView;
    private ImageView backgroundImageView;

    private EditText nameEditText;
    private TextView sexTextView;
    private EditText heightEditText;
    private EditText weightEditText;
    private TextView bodyTypeTextView;
    private TextView dressStyleEditText;
    private TextView effectEditText;
    private TextView changePwText;
    public static U02SettingsFragment instance;

    private MongoPeople people;

    public static U02SettingsFragment newIntance() {
        if (null == instance) {
            instance = new U02SettingsFragment();
        }
        return instance;
    }

    public U02SettingsFragment() {
        // Required empty public constructor

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (null != savedInstanceState) {
            people = (MongoPeople) savedInstanceState.getSerializable("people");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_u02_settings, container, false);
        context = getActivity().getApplicationContext();
        requestQueue = RequestQueueManager.INSTANCE.getQueue();

        matchUI(view);
        getUser();
        setJumpListener(view);

        view.findViewById(R.id.quitButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CookieSerializer.INSTANCE.saveCookie("");
                QSModel.INSTANCE.removeUser();
                Toast.makeText(context, "已退出登录", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getActivity(), U06LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
                QSStringRequest stringRequest = new QSStringRequest(Request.Method.POST, QSAppWebAPI.LOGOUT_SERVICE_URL,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                QSModel.INSTANCE.setUser(null);
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("TAG", error.getMessage(), error);
                    }
                }) {
                };
                requestQueue.add(stringRequest);
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void matchUI(View view) {

        tradeRelativeLayout = (RelativeLayout) view.findViewById(R.id.tradelistRelativeLayout);
        addresslistRelativeLayout = (RelativeLayout) view.findViewById(R.id.addresslist_RelativeLayout);

        portraitImageView = (ImageView) view.findViewById(R.id.portraitImageView);
        backgroundImageView = (ImageView) view.findViewById(R.id.backgroundImageView);

        nameEditText = (EditText) view.findViewById(R.id.nameEditText);
        nameEditText.setOnFocusChangeListener(this);

        sexTextView = (TextView) view.findViewById(R.id.sexTextView);

        heightEditText = (EditText) view.findViewById(R.id.heightEditText);
        heightEditText.setOnFocusChangeListener(this);

        weightEditText = (EditText) view.findViewById(R.id.weightEditText);
        weightEditText.setOnFocusChangeListener(this);

        bodyTypeTextView = (TextView) view.findViewById(R.id.bodyTypeTextView);
//        bodyTypeTextView.setOnFocusChangeListener(this);

        dressStyleEditText = (TextView) view.findViewById(R.id.dressStyleEditText);
//        dressStyleEditText.setOnFocusChangeListener(this);

        effectEditText = (TextView) view.findViewById(R.id.effectEditText);
//        effectEditText.setOnFocusChangeListener(this);

        changePwText = (TextView) view.findViewById(R.id.u02_change_pw_text);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (resultCode == Activity.RESULT_OK && null != data) {
                // Get the Image from data

                Uri selectedImage = data.getData();

                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                // Get the cursor
                Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String imgDecodableString = cursor.getString(columnIndex);

                cursor.close();

                if (requestCode == TYPE_PORTRAIT) {
                    uploadImage(imgDecodableString, TYPE_PORTRAIT);
                }

                if (requestCode == TYPE_BACKGROUD) {
                    uploadImage(imgDecodableString, TYPE_BACKGROUD);
                }

            } else {
                Toast.makeText(context, "您未选择图片！",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            //e.printStackTrace();
            Toast.makeText(context, "未知错误，请重试！", Toast.LENGTH_LONG)
                    .show();
        }
    }

    private void uploadImage(final String imgUri, final int type) {

        String api = "";

        if (type == TYPE_PORTRAIT) {
            api = QSAppWebAPI.getUserUpdateportrait();
        } else {
            api = QSAppWebAPI.getUserUpdatebackground();
        }
        String API = api;
        QSMultipartRequest multipartRequest = new QSMultipartRequest(Request.Method.POST,
                API, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                MongoPeople user = UserParser.parseUpdate(response);
                if (user == null) {
                    ErrorHandler.handle(context, MetadataParser.getError(response));
                } else {
                    getUser();
                }
                if (type == TYPE_PORTRAIT) {
                    ImageLoader.getInstance().displayImage(user.getPortrait(), portraitImageView, AppUtil.getPortraitDisplayOptions());
                }
                if (type == TYPE_BACKGROUD) {
                    ImageLoader.getInstance().displayImage(user.getPortrait(), portraitImageView, AppUtil.getModelBackgroundDisplayOptions());
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
// 获取MultipartEntity对象
        QSMultipartEntity multipartEntity = multipartRequest.getMultiPartEntity();
        multipartEntity.addStringPart("content", "hello");
// 文件参数
        File file = new File(imgUri);
        multipartEntity.addFilePart("image", file);
        multipartEntity.addStringPart("filename", file.getName());

// 构建请求队列
// 将请求添加到队列中
        requestQueue.add(multipartRequest);
    }

    //进入页面时，给字段赋值
    private void setData() {
        if (null != people) {

            ImageLoader.getInstance().displayImage(people.portrait, portraitImageView, AppUtil.getPortraitDisplayOptions());
            ImageLoader.getInstance().displayImage(people.background, backgroundImageView, AppUtil.getModelBackgroundDisplayOptions());

            nameEditText.setText(people.name);
            heightEditText.setText(people.height);
            weightEditText.setText(people.weight);
            sexTextView.setText(sexArgs[people.gender]);
            sexTextView.setTag(people.gender);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        getUser();
        outState.putSerializable("people", people);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        commitForm();
        getUser();
        UserCommand.refresh();
    }

    //获得用户信息
    private void getUser() {

        people = QSModel.INSTANCE.getUser();
        if (people != null) setData();
        else UserCommand.refresh(new Callback() {
            @Override
            public void onComplete() {
                super.onComplete();
                setData();
            }

            @Override
            public void onError() {
                super.onError();
                Toast.makeText(U02SettingsFragment.this.getActivity(), "网络请求错误", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void showActionSheet(String type) {

        if (TAG_SEX.equals(type)) {

            ActionSheet.createBuilder(getActivity(), getFragmentManager())
                    .setTag(TAG_SEX)
                    .setCancelButtonTitle("取消")
                    .setOtherButtonTitles(sexArgs)
                    .setCancelableOnTouchOutside(true).setListener(this).show();
        }

        if (TAG_BODYTYPE.equals(type)) {
            ActionSheet.createBuilder(getActivity(), getFragmentManager())
                    .setTag(TAG_BODYTYPE)
                    .setCancelButtonTitle("取消")
                    .setOtherButtonTitles(bodyTypeArgs)
                    .setCancelableOnTouchOutside(true).setListener(this).show();
        }

        if (TAG_DRESSSTYLE.equals(type)) {
            ActionSheet.createBuilder(getActivity(), getFragmentManager())
                    .setTag(TAG_DRESSSTYLE)
                    .setCancelButtonTitle("取消")
                    .setOtherButtonTitles(dressStyles)
                    .setCancelableOnTouchOutside(true).setListener(this).show();
        }

        if (TAG_EXPECTATIONS.equals(type)) {
            ActionSheet.createBuilder(getActivity(), getFragmentManager())
                    .setTag(TAG_EXPECTATIONS)
                    .setCancelButtonTitle("取消")
                    .setOtherButtonTitles(expectations)
                    .setCancelableOnTouchOutside(true).setListener(this).show();
        }

    }

    @Override
    public void onDismiss(ActionSheet actionSheet, boolean isCancel) {

    }

    @Override
    public void onOtherButtonClick(ActionSheet actionSheet, int index) {

        if (TAG_SEX.equals(String.valueOf(actionSheet.getTag()))) {
            sexTextView.setText(sexArgs[index]);
            sexTextView.setTag(index);
        }

        if (TAG_BODYTYPE.equals(String.valueOf(actionSheet.getTag()))) {
            bodyTypeTextView.setText(bodyTypeArgs[index]);
            bodyTypeTextView.setTag(index);
        }

        if (TAG_DRESSSTYLE.equals(String.valueOf(actionSheet.getTag()))) {
            dressStyleEditText.setText(dressStyles[index]);
            dressStyleEditText.setTag(index);
        }

        if (TAG_EXPECTATIONS.equals(String.valueOf(actionSheet.getTag()))) {
            effectEditText.setText(expectations[index]);
            effectEditText.setTag(index);
        }

        commitForm();
    }

    private void commitForm() {
        Map<String, String> params = new HashMap<String, String>();
        if (nameEditText != null && !nameEditText.getText().toString().equals(""))
            params.put("name", nameEditText.getText().toString());
        if (heightEditText != null && !heightEditText.getText().toString().equals(""))
            params.put("height", heightEditText.getText().toString());
        if (weightEditText != null && !weightEditText.getText().toString().equals(""))
            params.put("weight", weightEditText.getText().toString());
        if (null != sexTextView.getTag()) {
            params.put("gender", sexTextView.getTag().toString());
        }

        UserCommand.update(params, new Callback() {
            @Override
            public void onComplete() {
                super.onComplete();
            }

            @Override
            public void onError(int errorCode) {
                super.onError(errorCode);
                ErrorHandler.handle(context, errorCode);
            }

            @Override
            public void onError() {
                super.onError();
                Toast.makeText(context, "请检查网络", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setJumpListener(View view) {
        view.findViewById(R.id.backTextView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                commitForm();
                getActivity().finish();
            }
        });

        personalRelativeLayout = (RelativeLayout) view.findViewById(R.id.personalRelativeLayout);
        personalRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "请选择头像"), TYPE_PORTRAIT);
            }
        });
        backgroundRelativeLayout = (RelativeLayout) view.findViewById(R.id.backgroundRelativeLayout);
        backgroundRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "请选择背景"), TYPE_BACKGROUD);
            }
        });

        sexRelativeLayout = (RelativeLayout) view.findViewById(R.id.sexRelativeLayout);
        sexRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().setTheme(R.style.ActionSheetStyleIOS7);
                showActionSheet(TAG_SEX);
            }
        });

        bodyTypeRelativeLayout = (RelativeLayout) view.findViewById(R.id.bodyTypeRelativeLayout);
        bodyTypeRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().setTheme(R.style.ActionSheetStyleIOS7);
                showActionSheet(TAG_BODYTYPE);
            }
        });

        changePasswordRelativeLayout = (RelativeLayout) view.findViewById(R.id.changePasswordRelativeLayout);
        changePasswordRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                U02ChangePasswordFragment fragment = new U02ChangePasswordFragment();
                getFragmentManager().beginTransaction().setCustomAnimations(R.anim.push_left_in, 0, R.anim.push_left_in, 0).
                        replace(R.id.settingsScrollView, fragment).commit();
            }
        });

        tradeRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), U09TradeListActivity.class);
                startActivity(intent);
            }
        });

        addresslistRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), U10AddressListActivity.class);
                startActivity(intent);
            }
        });

        dressStyleRelativeLayout = (RelativeLayout) view.findViewById(R.id.dressStyleEelativeLayout);
        dressStyleRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().setTheme(R.style.ActionSheetStyleIOS7);
                showActionSheet(TAG_DRESSSTYLE);
            }
        });

        effectRelativeLayout = (RelativeLayout) view.findViewById(R.id.effectEelativeLayout);
        effectRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().setTheme(R.style.ActionSheetStyleIOS7);
                showActionSheet(TAG_EXPECTATIONS);
            }
        });
    }
}
