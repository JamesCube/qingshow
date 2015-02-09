package com.focosee.qingshow.activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.allthelucky.common.view.ImageIndicatorView;
import com.allthelucky.common.view.network.NetworkImageIndicatorView;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.focosee.qingshow.R;
import com.focosee.qingshow.app.QSApplication;
import com.focosee.qingshow.config.QSAppWebAPI;
import com.focosee.qingshow.config.ShareConfig;
import com.focosee.qingshow.entity.mongo.MongoItem;
import com.focosee.qingshow.entity.mongo.MongoShow;
import com.focosee.qingshow.httpapi.response.MetadataParser;
import com.focosee.qingshow.httpapi.response.dataparser.ShowParser;
import com.focosee.qingshow.request.QSJsonObjectRequest;
import com.focosee.qingshow.share.SinaAccessTokenKeeper;
import com.focosee.qingshow.util.AppUtil;
import com.focosee.qingshow.util.BitMapUtil;
import com.focosee.qingshow.widget.MRoundImageView;
import com.focosee.qingshow.widget.SharePopupWindow;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.constant.WBConstants;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.utils.Utility;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class S03SHowActivity extends BaseActivity implements IWXAPIEventHandler ,IWeiboHandler.Response {

    // Input data
    public static final String INPUT_SHOW_ENTITY_ID = "S03SHowActivity_input_show_entity_id";
    public static final String INPUT_SHOW_LIST_ENTITY = "S03SHowActivity_input_show_list_entity";
    public static final String ACTION_MESSAGE = "S03SHowActivity_like";
    public final String TAG = "S03SHowActivity";
    private int position;

    private String showId;
    private MongoShow showListEntity;
    private MongoShow showDetailEntity;// TODO remove the duplicated one
    private ArrayList<MongoItem> itemsData;
    private String videoUriString;
    private Uri videoUri = null;

    // Component declaration
    private RelativeLayout mRelativeLayout;
    private NetworkImageIndicatorView imageIndicatorView;
    private VideoView videoView;
//    private SurfaceView surfaceView;
//    private MediaPlayer mediaPlayer;


    private MRoundImageView modelImage;
    private TextView modelInformation;
    private TextView modelAgeHeight;
    private TextView modelSignature;
    private TextView commentTextView;
    private TextView likeTextView;
    private TextView itemTextView;
    private SharePopupWindow sharePopupWindow;

    private IWXAPI wxApi;

    private IWeiboShareAPI mWeiboShareAPI;


    // like image button
    private ImageButton likedImageButton;

    private LinearLayout buttomLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_s03_show);


        likedImageButton = (ImageButton) findViewById(R.id.S03_like_btn);

        wxApi = WXAPIFactory.createWXAPI(this, ShareConfig.WX_APP_KEY, true);
        wxApi.registerApp(ShareConfig.WX_APP_KEY);


       // mSsoHandler = new SsoHandler(this, mAuthInfo);
        mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(this, ShareConfig.SINA_APP_KEY);
        mWeiboShareAPI.registerApp();

        findViewById(R.id.S03_back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                S03SHowActivity.this.finish();
            }
        });

        Intent intent = getIntent();
        if(null != intent.getSerializableExtra(S03SHowActivity.INPUT_SHOW_LIST_ENTITY)){
            showListEntity = (MongoShow) intent.getSerializableExtra(S03SHowActivity.INPUT_SHOW_LIST_ENTITY);
            position = intent.getIntExtra("position", 0);
        }
        showId = intent.getStringExtra(S03SHowActivity.INPUT_SHOW_ENTITY_ID);
        //if(null == intent.getSerializableExtra(S03SHowActivity.INPUT_SHOW_ENTITY)){
        getShowDetailFromNet();
        //}else {
        //    showDetailEntity = (MongoShowD) intent.getSerializableExtra(S03SHowActivity.INPUT_SHOW_ENTITY);
        //}

        matchUI();

    }

    private void getShowDetailFromNet() {
        final QSJsonObjectRequest jsonObjectRequest = new QSJsonObjectRequest(QSAppWebAPI.getShowDetailApi(showId), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i("S03ShowActivity", response.toString());
                showDetailEntity = ShowParser.parseQuery(response).get(0);
                showData();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("S03ShowActivity", error.toString());
            }
        });
        QSApplication.get().QSRequestQueue().add(jsonObjectRequest);
    }

    private void clickLikeShowButton() {
        likedImageButton.setClickable(false);
        Map<String, String> likeData = new HashMap<String, String>();
        likeData.put("_id", showDetailEntity.get_id());
        JSONObject jsonObject = new JSONObject(likeData);

        String requestApi = (showDetailEntity.likedByCurrentUser()) ? QSAppWebAPI.getShowUnlikeApi() : QSAppWebAPI.getShowLikeApi();

        QSJsonObjectRequest jsonObjectRequest = new QSJsonObjectRequest(Request.Method.POST, requestApi, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (!MetadataParser.hasError(response)) {
                        showMessage(S03SHowActivity.this, showDetailEntity.likedByCurrentUser() ? "取消点赞成功" : "点赞成功");
                        showDetailEntity.setLikedByCurrentUser(!showDetailEntity.likedByCurrentUser());
                        if(showDetailEntity.likedByCurrentUser()) {//发送广播，更新首页的numLike
                            Intent intent = new Intent(ACTION_MESSAGE);
                            intent.putExtra("position", position);
                            sendBroadcast(intent);
                        }
                        setLikedImageButtonBackgroundImage();
                        likedImageButton.setClickable(true);
                        showListEntity.numLike = showDetailEntity.numLike;
                        QSApplication.get().refreshPeople(S03SHowActivity.this);
                    } else {
                        handleResponseError(response);
//                        showMessage(S03SHowActivity.this, showDetailEntity.likedByCurrentUser() ? "取消点赞失败" : "点赞失败");
                    }
                } catch (Exception e) {
                    showMessage(S03SHowActivity.this, e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showMessage(S03SHowActivity.this, error.toString());
            }
        });

        QSApplication.get().QSRequestQueue().add(jsonObjectRequest);
    }

    private void setLikedImageButtonBackgroundImage() {
        if (null == showDetailEntity) {
            return;
        }
        if (showDetailEntity.likedByCurrentUser()) {
            likedImageButton.setBackgroundResource(R.drawable.s03_like_btn_hover);
        } else {
            likedImageButton.setBackgroundResource(R.drawable.s03_like_btn);
        }
        likeTextView.setText(showDetailEntity.getShowLikeNumber());
    }

    private void handleResponseError(JSONObject response) {
        try {
            int errorCode = MetadataParser.getError(response);
            String errorMessage = showDetailEntity.likedByCurrentUser() ? "取消点赞失败" : "点赞失败";
            switch (errorCode) {
                case 1012:
                    errorMessage = "请先登录！";
                    break;
                case 1000:
                    errorMessage = "服务器错误，请稍微重试！";
                    break;
                default:
                    errorMessage = String.valueOf(errorCode) + response.toString();
                    break;
            }
            showMessage(S03SHowActivity.this, errorMessage);
        } catch (Exception e) {
            showMessage(S03SHowActivity.this, e.toString() + response.toString());
        }
    }

    private void showMessage(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        Log.i(context.getPackageName(), message);
    }

    private void matchUI() {
        this.mRelativeLayout = (RelativeLayout) findViewById(R.id.S03_relative_layout);
        this.imageIndicatorView = (NetworkImageIndicatorView) findViewById(R.id.S03_image_indicator);
        this.videoView = (VideoView) findViewById(R.id.S03_video_view);

        modelImage = (MRoundImageView) findViewById(R.id.S03_model_portrait);
        modelInformation = (TextView) findViewById(R.id.S03_model_name);
        modelAgeHeight = (TextView) findViewById(R.id.S03_model_age_height);

        commentTextView = (TextView) findViewById(R.id.S03_comment_text_view);
        likeTextView = (TextView) findViewById(R.id.S03_like_text_view);
        itemTextView = (TextView) findViewById(R.id.S03_item_text_view);

        buttomLayout = (LinearLayout) findViewById(R.id.S03_model_LinearLayout);
    }

    private void showData() {
        if (null == showDetailEntity)
            return;

        itemsData = showDetailEntity.getItemsList();

        videoUriString = showDetailEntity.getShowVideo();

        ImageLoader.getInstance().displayImage(showDetailEntity.getModelPhoto(), modelImage, AppUtil.getPortraitDisplayOptions());

        modelInformation.setText(showDetailEntity.getModelName());

        modelAgeHeight.setText(showDetailEntity.getModelWeightHeight());

        commentTextView.setText(showDetailEntity.getShowCommentNumber());

        likeTextView.setText(showDetailEntity.getShowLikeNumber());

        itemTextView.setText(showDetailEntity.getItemsCount());

        this.initPosterView(showDetailEntity.getPosters());

        findViewById(R.id.S03_item_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (S07CollectActivity.isOpened) return;
                S07CollectActivity.isOpened = true;
                Intent intent = new Intent(S03SHowActivity.this, S07CollectActivity.class);
                intent.putExtra(S07CollectActivity.INPUT_BACK_IMAGE, showDetailEntity.getCover());
//                intent.putExtra(S07CollectActivity.INPUT_BRAND_TEXT, showDetailEntity.getBrandNameText());
                Bundle bundle = new Bundle();
                bundle.putSerializable(S07CollectActivity.INPUT_ITEMS, showDetailEntity.getItemsList());
                //bundle.putSerializable(S07CollectActivity.INPUT_BRAND_ENTITY, showDetailEntity.get());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        findViewById(R.id.S03_comment_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != showDetailEntity && null != showDetailEntity._id) {
                    if(S04CommentActivity.isOpened) return;
                    S04CommentActivity.isOpened = true;
                    Intent intent = new Intent(S03SHowActivity.this, S04CommentActivity.class);
                    intent.putExtra(S04CommentActivity.INPUT_SHOW_ID, showDetailEntity._id);
                    startActivity(intent);
                } else {
                    Toast.makeText(S03SHowActivity.this, "Plese NPC!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        likedImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickLikeShowButton();
            }
        });

        findViewById(R.id.S03_share_btn).setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {

                sharePopupWindow=new SharePopupWindow(S03SHowActivity.this,new ShareClickListener());
                sharePopupWindow.showAtLocation(S03SHowActivity.this.findViewById(R.id.S03_share_btn), Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);

            }
        });

        findViewById(R.id.S03_video_start_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startVideo();
            }
        });

        this.buttomLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(S03SHowActivity.this, P02ModelActivity.class);
//                intent.putExtra(P02ModelActivity.INPUT_MODEL, showDetailEntity);
//                startActivity(intent);
            }
        });

        this.imageIndicatorView.setOnItemChangeListener(new ImageIndicatorView.OnItemChangeListener() {
            @Override
            public void onPosition(int position, int totalCount) {
                Log.d(TAG, "videoView.visible: " + videoView.getVisibility());
                if(videoView.getVisibility() == View.GONE) return;
                Log.d(TAG, "position: " + position % totalCount);
                findViewById(R.id.S03_before_video_view).setVisibility(View.VISIBLE);
                if(position % totalCount == 0)
                    findViewById(R.id.S03_before_video_without_back).setVisibility(View.VISIBLE);
                else
                    findViewById(R.id.S03_before_video_without_back).setVisibility(View.GONE);
            }
        });

        setLikedImageButtonBackgroundImage();
    }

    private String arrayToString(String[] input) {
        String result = "";
        for (String str : input)
            result += str + " ";
        return result;
    }

    private void initPosterView(String[] urlList) {
        this.imageIndicatorView.setupLayoutByImageUrl(Arrays.asList(urlList), ImageLoader.getInstance(), AppUtil.getShowDisplayOptions());
        this.imageIndicatorView.show();
        this.imageIndicatorView.getViewPager().setCurrentItem(urlList.length * 100, true);
    }

    private void configVideo() {
        videoView.setDrawingCacheEnabled(true);
        videoView.setVideoPath(videoUriString);
        videoView.requestFocus();
        videoView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                pauseVideo();
                return false;
            }
        });
    }

    private boolean isFirstStart = true;

    private void startVideo() {
        if (isFirstStart) {
            configVideo();
            isFirstStart = false;
        }
        findViewById(R.id.S03_before_video_view).setVisibility(View.GONE);
//        imageIndicatorView.setVisibility(View.GONE);
        videoView.setVisibility(View.VISIBLE);
        videoView.start();
    }

    private void pauseVideo() {

//        MediaMetadataRetriever rev = new MediaMetadataRetriever();
//        rev.setDataSource(this, Uri.parse(videoUriString));
//        Bitmap bitmap = rev.getFrameAtTime(videoView.getCurrentPosition() * 1000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);

        videoView.pause();

//        View view = findViewById(R.id.S03_relative_layout).getRootView();
//        view.setDrawingCacheEnabled(true);
//        view.buildDrawingCache();
//        Bitmap bitmap = view.getDrawingCache();

//        videoView.buildDrawingCache();
        Bitmap bitmapInput = videoView.getDrawingCache();
//        Bitmap bitmapInput = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
        Bitmap bitmap = Bitmap.createBitmap(bitmapInput);
//        Bitmap bitmap = Surface.screenshot((int) dims[0], (int) dims[1]);
//
//        Canvas canvas = new Canvas(bitmapInput);
//        canvas.drawBitmap(bitmap, 0, 0, null);

//        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
//        savePic(bitmapInput, "test.png");


        this.imageIndicatorView.addBitmapAtFirst(bitmap, ImageLoader.getInstance(), AppUtil.getShowDisplayOptions());
        this.imageIndicatorView.show();

        findViewById(R.id.S03_before_video_view).setVisibility(View.VISIBLE);
    }

    // 保存到sdcard
    private void savePic(Bitmap b, String strFileName) {
        File f = new File("/sdcard/Note/" + strFileName + ".jpg");
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
            Log.i("test", e.toString());
        }
        b.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
            Log.i("test", e.toString());
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
            Log.i("test", e.toString());
        }
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        setIntent(intent);
        wxApi.handleIntent(intent, this);
        mWeiboShareAPI.handleWeiboResponse(intent,this);
    }

    private void shareToSina(){
        WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
        WebpageObject mediaObject = new WebpageObject();
        mediaObject.identify = Utility.generateGUID();
        mediaObject.title =ShareConfig.SHARE_TITLE;
        mediaObject.description =ShareConfig.SHARE_DESCRIPTION;
        mediaObject.setThumbImage(BitmapFactory.decodeResource(getResources(), ShareConfig.IMG));
        mediaObject.actionUrl =  ShareConfig.SHARE_SHOW_URL +showDetailEntity.get_id();
        mediaObject.defaultText = ShareConfig.SHARE_DESCRIPTION;

        weiboMessage.mediaObject = mediaObject;

        SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
        request.transaction = String.valueOf(System.currentTimeMillis());
        request.multiMessage = weiboMessage;
        AuthInfo authInfo = new AuthInfo(this, ShareConfig.SINA_APP_KEY, ShareConfig.SINA_REDIRECT_URL, ShareConfig.SCOPE);
        Oauth2AccessToken accessToken = SinaAccessTokenKeeper.readAccessToken(getApplicationContext());
        String token = "";
        if (accessToken != null) {
            token = accessToken.getToken();
        }
        mWeiboShareAPI.sendRequest(this, request, authInfo, token, new WeiboAuthListener() {

            @Override
            public void onWeiboException( WeiboException arg0 ) {
            }

            @Override
            public void onComplete( Bundle bundle ) {
                // TODO Auto-generated method stub
                Oauth2AccessToken newToken = Oauth2AccessToken.parseAccessToken(bundle);
                SinaAccessTokenKeeper.writeAccessToken(getApplicationContext(), newToken);
            }

            @Override
            public void onCancel() {
            }
        });

    }

    @Override
    public void onResponse(BaseResponse baseResponse) {
        switch (baseResponse.errCode) {
            case WBConstants.ErrorCode.ERR_OK:
                Log.i("tag", "ERR_OK");
                break;
            case WBConstants.ErrorCode.ERR_CANCEL:
                Log.i("tag", "ERR_CANCEL");
                break;
            case WBConstants.ErrorCode.ERR_FAIL:
                Log.i("tag", "ERR_FAIL");
                break;
        }
    }



    private void shareToWX(boolean isTimelineCb){

        WXWebpageObject webpage = new WXWebpageObject();
        WXMediaMessage msg;
        webpage.webpageUrl = ShareConfig.SHARE_SHOW_URL +showDetailEntity.get_id();

        msg = new WXMediaMessage();
        msg.mediaObject = webpage;
        Bitmap thumb = BitmapFactory.decodeResource(getResources(), ShareConfig.IMG);
        msg.thumbData = BitMapUtil.bmpToByteArray(thumb, false);
        msg.setThumbImage(thumb);
        msg.title = ShareConfig.SHARE_TITLE;
        msg.description = ShareConfig.SHARE_DESCRIPTION;


        final SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = msg;
        req.scene = isTimelineCb ? SendMessageToWX.Req.WXSceneTimeline : SendMessageToWX.Req.WXSceneSession;
        wxApi.sendReq(req);
    }


    @Override
    public void onReq(BaseReq req) {
        switch (req.getType()) {
            case ConstantsAPI.COMMAND_GETMESSAGE_FROM_WX:
                Log.i("tag", "COMMAND_GETMESSAGE_FROM_WX");
                break;
            case ConstantsAPI.COMMAND_SHOWMESSAGE_FROM_WX:
                Log.i("tag", "COMMAND_SHOWMESSAGE_FROM_WX");
                break;
            default:
                break;
        }
    }

    @Override
    public void onResp(BaseResp resp) {


        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                Log.i("tag", "ERR_OK");
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                Log.i("tag", "ERR_USER_CANCEL");
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                Log.i("tag", "ERR_AUTH_DENIED");
                break;
            default:
                Log.i("tag", "ERR_OK");
                break;
        }

    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (mSsoHandler != null) {
//            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
//        }
//    }


    class ShareClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.share_wechat:
                    shareToWX(false);
                    break;
                case R.id.share_wx_timeline:
                    shareToWX(true);
                    break;
                case R.id.share_sina:
                    shareToSina();
                    break;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("S03Show"); //统计页面
        MobclickAgent.onResume(this);          //统计时长
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("S03Show"); // 保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息
        MobclickAgent.onPause(this);
    }
}
