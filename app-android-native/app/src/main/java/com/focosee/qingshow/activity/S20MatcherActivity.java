package com.focosee.qingshow.activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.android.volley.Response;
import com.focosee.qingshow.QSApplication;
import com.focosee.qingshow.R;
import com.focosee.qingshow.adapter.S20SelectAdapter;
import com.focosee.qingshow.constants.config.QSAppWebAPI;
import com.focosee.qingshow.httpapi.request.QSJsonObjectRequest;
import com.focosee.qingshow.httpapi.request.RequestQueueManager;
import com.focosee.qingshow.httpapi.response.MetadataParser;
import com.focosee.qingshow.httpapi.response.dataparser.CategoryParser;
import com.focosee.qingshow.httpapi.response.dataparser.ItemFeedingParser;
import com.focosee.qingshow.httpapi.response.error.ErrorHandler;
import com.focosee.qingshow.model.QSModel;
import com.focosee.qingshow.model.S20Bitmap;
import com.focosee.qingshow.model.vo.mongo.MongoCategories;
import com.focosee.qingshow.model.vo.mongo.MongoItem;
import com.focosee.qingshow.model.vo.mongo.MongoPeople;
import com.focosee.qingshow.receiver.PushGuideEvent;
import com.focosee.qingshow.util.AppUtil;
import com.focosee.qingshow.util.ToastUtil;
import com.focosee.qingshow.util.user.UnreadHelper;
import com.focosee.qingshow.widget.ConfirmDialog;
import com.focosee.qingshow.widget.MenuView;
import com.focosee.qingshow.widget.QSCanvasView;
import com.focosee.qingshow.widget.QSImageView;
import com.focosee.qingshow.widget.radio.RadioLayout;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

/**
 * Created by Administrator on 2015/7/1.
 */
public class S20MatcherActivity extends BaseActivity {

    @InjectView(R.id.canvas)
    QSCanvasView canvas;
    @InjectView(R.id.selectRV)
    RecyclerView selectRV;

    public static final String S20_ITEMREFS = "S20_ITEMREFS";
    public static final String S20_ITEMRECTS = "S20_ITEMRECTS";
    public static final String S20_SELECT_CATEGORYREFS = "S20_SELECT_CATEGORYREFS";
    @InjectView(R.id.container)
    FrameLayout container;
    @InjectView(R.id.s20_guide_imageview)
    ImageView s20GuideImageview;
    @InjectView(R.id.menu)
    ImageButton menu;

    private S20SelectAdapter adapter;
    private List<MongoItem> datas;
    private List<MongoCategories> categories;

    private Map<String, Select> allSelect;
    private List<String> categoryRefs;
    private ArrayList<String> lastCategoryRefs;
    private Map<String, MongoCategories.Context> contexts;

    private String mCategoryRef = "";
    private int pagaSize = 10;
    private int rows[] = new int[]{1, 2};
    private boolean hasDraw = false;

    private MenuView menuView;

    @Override
    public void reconn() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_s20_matcher);
        ButterKnife.inject(this);
        EventBus.getDefault().register(this);
        allSelect = new HashMap<>();
        categoryRefs = new ArrayList<>();
        lastCategoryRefs = new ArrayList<>();
        contexts = new HashMap<>();

        if (!QSModel.INSTANCE.isFinished(MongoPeople.MATCH_FINISHED)) {
            menu.setVisibility(View.GONE);
            s20GuideImageview.setVisibility(View.VISIBLE);
            s20GuideImageview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    s20GuideImageview.setVisibility(View.GONE);
                }
            });
        }

        initSelectRV();
        initCanvas();

        initCategoryRef();
    }


    private void initCategoryRef() {
        getCategoryFromNet();
    }

    private void addItemsToCanvas(final String categoryRef, String url) {
        final Select select;

        final QSImageView itemView = new QSImageView(this);

        if (allSelect.containsKey(categoryRef)) {
            select = allSelect.get(categoryRef);
        } else {
            select = new Select();
        }

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
        itemView.setLayoutParams(layoutParams);
        Rect frame = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int barHeight = frame.top;
        itemView.setBarHeight(barHeight);
        ImageLoader.getInstance().displayImage(url, itemView.getImageView(), AppUtil.getShowDisplayOptions(), new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                super.onLoadingComplete(imageUri, view, loadedImage);
                if (contexts.containsKey(categoryRef)) {
                    formatPlace(itemView, categoryRef, true);
                }
                ObjectAnimator animator = ObjectAnimator.ofFloat(view, "alpha", 0, 1.0f);
                animator.setDuration(500);
                animator.start();
                canvas.notifyCheckedChange();
                select.loadDone = true;
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                select.loadDone = true;
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                select.loadDone = true;
            }

            @Override
            public void onLoadingStarted(String imageUri, View view) {
                select.loadDone = false;
            }
        });


        itemView.setCategoryId(categoryRef);
        itemView.setOnDelClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ConfirmDialog dialog = new ConfirmDialog(S20MatcherActivity.this);
                dialog.setTitle(getResources().getString(R.string.s20_dialog));
                dialog.setConfirm(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        canvas.detach(itemView);
                        allSelect.remove(categoryRef);
                        categoryRefs.remove(categoryRef);
                        dialog.dismiss();
                    }
                });
                dialog.setCancel(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();

            }
        });

        canvas.attach(itemView);
        itemView.bringToFront();
        select.setView(itemView).setPageNo(1);
        allSelect.put(categoryRef, select);
    }


    private void formatPlace(final QSImageView view, final String categoryRef, boolean moveToFrame) {

        int width = view.getImageView().getDrawable().getIntrinsicWidth();
        int height = view.getImageView().getDrawable().getIntrinsicHeight();
        float ratio =(float) width / height;

        float maxWidth = contexts.get(categoryRef).maxWidth * canvas.getWidth() / 100f;
        float maxHeight = contexts.get(categoryRef).maxHeight * canvas.getHeight() / 100f;

        float maxRatio = maxWidth / maxHeight;

        if (ratio < maxRatio) {
            width = (int) (maxHeight * ratio);
            height = (int) maxHeight;
        } else {
            height = (int) (maxWidth / ratio);
            width = (int) maxWidth;
        }
        view.setLayoutParams(new FrameLayout.LayoutParams(width, height));
        view.setX(contexts.get(categoryRef).x * canvas.getWidth() / 100);
        view.setY(contexts.get(categoryRef).y * canvas.getHeight() / 100);
        System.out.println("left:" + view.getLeft());
        System.out.println("right:" + view.getRight());
        view.callOnClick();
    }


    public void checkBorder(QSImageView view, View prent) {
        float scaleFactor = view.getLastScaleFactor();
        float dx = view.getImageView().getDrawable().getIntrinsicWidth() * (scaleFactor - 1) / 2;
        float dy = view.getImageView().getDrawable().getIntrinsicHeight() * (scaleFactor - 1) / 2;
        float actrulLeft = view.getLeft() - dx;
        float actrulRight = view.getRight() + dx;
        float actrulTop = view.getTop() - dy;
        float actrulBottom = view.getBottom() + dy;

        if (actrulLeft < 0) {
//            moveView(view,view.getX(),view.getY(),);
        }


        if (actrulRight > prent.getWidth()) {

        }

        if (actrulTop < 0) {
        }

        if (actrulBottom > prent.getHeight()) {
        }
    }


    private void initCanvas() {
        canvas.setOnCheckedChangeListener(new QSCanvasView.OnCheckedChangeListener() {
            @Override
            public void checkedChanged(QSImageView view) {
                if (mCategoryRef != view.getCategoryId()) {
                    mCategoryRef = view.getCategoryId();
                    if (allSelect.containsKey(mCategoryRef)) {
                        allSelect.get(mCategoryRef).view.showDelBtn();
                        allSelect.get(mCategoryRef).view.bringToFront();
                        List<MongoItem> mongoItems = allSelect.get(mCategoryRef).data;
                        adapter.setSelectPos(allSelect.get(mCategoryRef).selectPos);
                        adapter.setLastChecked(allSelect.get(mCategoryRef).lastChecked);
                        adapter.addDataAtTop(mongoItems);
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }


    private void initSelectRV() {
        datas = new LinkedList<>();
        adapter = new S20SelectAdapter(datas, this, R.layout.item_s20_select);
        allSelect.containsKey(mCategoryRef);
        adapter.setOnCheckedChangeListener(new S20SelectAdapter.OnCheckedChangeListener() {
            @Override
            public void onCheckedChange(MongoItem datas, int position, RadioLayout view) {
                if (!allSelect.containsKey(mCategoryRef)) {
                    return;
                }
                ImageLoader.getInstance().displayImage(datas.thumbnail, allSelect.get(mCategoryRef).view.getImageView(), new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        super.onLoadingComplete(imageUri, view, loadedImage);
                        if (allSelect.containsKey(mCategoryRef) && contexts.containsKey(mCategoryRef)) {
                            formatPlace(allSelect.get(mCategoryRef).view, mCategoryRef, false);
                        } else {
                            return;
                        }
                    }
                });
                allSelect.put(mCategoryRef, allSelect.get(mCategoryRef).setLastChecked(view).setSelectPos(position).setItem(datas));

            }
        });
        selectRV.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL));
        selectRV.addOnScrollListener(new RecyclerView.OnScrollListener() {

            private int[] lastPositions;
            private int lastVisibleItemPosition;
            private int currentScrollState = 0;

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                StaggeredGridLayoutManager layoutManager = (StaggeredGridLayoutManager) recyclerView.getLayoutManager();
                if (lastPositions == null) {
                    lastPositions = new int[layoutManager.getSpanCount()];
                }
                layoutManager.findLastVisibleItemPositions(lastPositions);
                lastVisibleItemPosition = findMax(lastPositions);
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                currentScrollState = newState;
                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                if ((visibleItemCount > 0 && currentScrollState == RecyclerView.SCROLL_STATE_IDLE &&
                        (lastVisibleItemPosition) >= totalItemCount - 1)) {
                    onBottom();
                }
            }

            public void onBottom() {
                if (allSelect.containsKey(mCategoryRef)) {
                    int pagaNo = allSelect.get(mCategoryRef).pageNo;
                    ++pagaNo;
                    getDataFromNet(pagaNo, pagaSize, mCategoryRef);
                    allSelect.put(mCategoryRef, allSelect.get(mCategoryRef).setPageNo(pagaNo));
                }
            }

            private int findMax(int[] lastPositions) {
                int max = lastPositions[0];
                for (int value : lastPositions) {
                    if (value > max) {
                        max = value;
                    }
                }
                return max;
            }
        });
        selectRV.setAdapter(adapter);

    }


    private void getDataFromNet(final String categoryRef) {
        QSJsonObjectRequest jsonObjectRequest = new QSJsonObjectRequest(QSJsonObjectRequest.Method.GET, QSAppWebAPI.getQueryItems(1, 20, categoryRef), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(S20MatcherActivity.class.getSimpleName(), "response:" + response);
                if (MetadataParser.hasError(response)) {
                    ErrorHandler.handle(S20MatcherActivity.this, MetadataParser.getError(response));
                    return;
                }
                datas = ItemFeedingParser.parse(response);
                if (allSelect.containsKey(categoryRef)) {
                    allSelect.put(categoryRef, allSelect.get(categoryRef).setData(datas).setItem(datas.get(0)));
                } else {
                    allSelect.put(categoryRef, new Select().setData(datas).setItem(datas.get(0)));
                }
                if (!categoryRefs.contains(categoryRef)) {
                    categoryRefs.add(categoryRef);
                }
                addItemsToCanvas(categoryRef, datas.get(0).thumbnail);
            }
        }, null);
        RequestQueueManager.INSTANCE.getQueue().add(jsonObjectRequest);
    }

    private void getDataFromNet(int pageNo, int pageSize, final String categoryRef) {
        QSJsonObjectRequest jsonObjectRequest = new QSJsonObjectRequest(QSJsonObjectRequest.Method.GET, QSAppWebAPI.getQueryItems(pageNo, pageSize, categoryRef), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(S20MatcherActivity.class.getSimpleName(), "items-response:" + response);
                if (MetadataParser.hasError(response)) {
                    ErrorHandler.handle(S20MatcherActivity.this, MetadataParser.getError(response));
                    return;
                }

                datas = ItemFeedingParser.parse(response);

                adapter.addDataAtLast(datas);

                if (allSelect.containsKey(categoryRef)) {
                    List<MongoItem> mongoItems = allSelect.get(categoryRef).data;
                    mongoItems.addAll(datas);
                    allSelect.put(categoryRef, allSelect.get(categoryRef).setData(mongoItems));
                } else {
                    allSelect.put(categoryRef, new Select().setData(datas));
                }
                adapter.notifyDataSetChanged();
            }
        }, null);
        RequestQueueManager.INSTANCE.getQueue().add(jsonObjectRequest);
    }

    private void getCategoryFromNet() {
        QSJsonObjectRequest jsonObjectRequest = new QSJsonObjectRequest(QSJsonObjectRequest.Method.GET, QSAppWebAPI.getQueryCategories(), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(S20MatcherActivity.class.getSimpleName(), "cate_response:" + response);
                if (MetadataParser.hasError(response)) {
                    ErrorHandler.handle(S20MatcherActivity.this, MetadataParser.getError(response));
                    return;
                }
                categories = CategoryParser.parseQuery(response);

                for (MongoCategories category : categories) {

                    if (null != category.__context) {
                        contexts.put(category._id, category.__context);
                        if (!categoryRefs.contains(category._id)) {
                            categoryRefs.add(category._id);
                        }
                        getDataFromNet(category._id);
                    }
                }
            }
        }, null);
        RequestQueueManager.INSTANCE.getQueue().add(jsonObjectRequest);
    }

    private void moveView(View view, float startX, float startY, float nextX, float nextY) {
        ObjectAnimator x = ObjectAnimator.ofFloat(view, "x", startX, nextX);
        ObjectAnimator y = ObjectAnimator.ofFloat(view, "y", startY, nextY);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(x, y);
        animatorSet.setDuration(0);
        animatorSet.start();
        ((QSImageView) view).setLastCentroid(new Point(view.getLeft() + ((QSImageView) view).getImageView().getDrawable().getIntrinsicWidth() / 2, view.getTop() + ((QSImageView) view).getImageView().getDrawable().getIntrinsicHeight() / 2));
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    public void onEventMainThread(S21CategoryEvent event) {
        categoryRefs = event.getCategories();

        for (String ref : lastCategoryRefs) {
            if (!categoryRefs.contains(ref)) {
                if (allSelect.containsKey(ref)) {
                    canvas.detach(allSelect.get(ref).view);
                    allSelect.remove(ref);
                }
            }
        }

        for (String ref : categoryRefs) {
            if (!lastCategoryRefs.contains(ref)) {
                Random random = new Random();
                int row = random.nextInt(3);
                int cloum = random.nextInt(2);
                if (row > rows[cloum]) {
                    rows[cloum] = row;
                }
                getDataFromNet(ref);
            }
        }
    }

    public void onEventMainThread(PushGuideEvent event) {
        if (event.unread) {
            ((ImageButton) findViewById(R.id.menu)).setImageResource(R.drawable.nav_btn_menu_n_dot);
        } else {
            if (!UnreadHelper.hasUnread())
                ((ImageButton) findViewById(R.id.menu)).setImageResource(R.drawable.nav_btn_menu_n);
        }
    }

    @OnClick(R.id.menu)
    public void menuOpen() {
        ((ImageButton) findViewById(R.id.menu)).setImageResource(R.drawable.nav_btn_menu_n);
        menuView = new MenuView();
        menuView.show(getSupportFragmentManager(), S01MatchShowsActivity.class.getSimpleName(), container);
    }

    @OnClick(R.id.selectBtn)
    public void select() {
        Intent intent = new Intent(S20MatcherActivity.this, S21CategoryActivity.class);
        lastCategoryRefs.clear();
        lastCategoryRefs.addAll(categoryRefs);
        intent.putStringArrayListExtra(S20_SELECT_CATEGORYREFS, lastCategoryRefs);
        startActivity(intent);
    }

    private boolean onSubmit = false;

    @OnClick(R.id.submitBtn)
    public void submit() {
        if (onSubmit) {
            return;
        } else {
            onSubmit = true;
        }
        if (allSelect.size() < 4) {
            ToastUtil.showShortToast(getApplicationContext(), getResources().getString(R.string.s20_item_less));
            onSubmit = false;
            return;
        }
        for (String key : allSelect.keySet()) {
            if (!allSelect.get(key).loadDone) {
                ToastUtil.showShortToast(getApplicationContext(), getResources().getString(R.string.s20_notify_load_image_unfinished));
                onSubmit = false;
                return;
            }
        }


        if (!checkOverlap(0.7f)) {
            ToastUtil.showShortToast(getApplicationContext(), getResources().getString(R.string.s20_notify_more));
            onSubmit = false;
            return;
        }

        if (hasDraw) {
            canvas.destroyDrawingCache();
        }
        canvas.notifyChildrenUnClick();
        canvas.setDrawingCacheEnabled(true);
        canvas.buildDrawingCache();
        Bitmap bitmap = canvas.getDrawingCache();
        S20Bitmap.INSTANCE.setBitmap(bitmap);
        hasDraw = true;

        Intent intent = new Intent(S20MatcherActivity.this, S22MatchPreviewActivity.class);
        ArrayList<String> itemRefs = new ArrayList<>();
        for (String s : allSelect.keySet()) {
            itemRefs.add(allSelect.get(s).item._id);
        }
        intent.putStringArrayListExtra(S20_ITEMREFS, itemRefs);

        ArrayList<Rect> itemRects = new ArrayList<>();
        for (int i = 0; i < canvas.getChildCount(); i++) {
            Rect rect = new Rect();
            if(canvas.getChildVisibleRect(canvas.getChildAt(i), rect, null)){
                itemRects.add(rect);
            }
        }

        intent.putParcelableArrayListExtra(S20_ITEMRECTS, itemRects);

        startActivity(intent);
        onSubmit = false;
    }

    private boolean checkOverlap(float limit) {
        for (String s : allSelect.keySet()) {
            QSImageView view = allSelect.get(s).view;
            if (canvas.calcUnOverlapArea(view) / view.getArea() < limit) {
                return false;
            }
        }
        return true;
    }

    private class Select {
        public RadioLayout lastChecked;
        public int selectPos;
        public QSImageView view;
        public int pageNo;
        public List<MongoItem> data;
        public MongoItem item;
        public int row;
        public int column;
        public boolean loadDone = false;

        public Select setRow(int row) {
            this.row = row;
            return this;
        }

        public Select setColumn(int column) {
            this.column = column;
            return this;
        }

        public Select setData(List<MongoItem> data) {
            this.data = data;
            return this;
        }

        public Select setPageNo(int pageNo) {
            this.pageNo = pageNo;
            return this;
        }

        public Select setView(QSImageView view) {
            this.view = view;
            return this;
        }

        public Select setSelectPos(int selectPos) {
            this.selectPos = selectPos;
            return this;
        }

        public Select setLastChecked(RadioLayout lastChecked) {
            this.lastChecked = lastChecked;
            return this;
        }

        public Select setItem(MongoItem item) {
            this.item = item;
            return this;
        }

        public Select setLoadDone(boolean loadDone) {
            this.loadDone = loadDone;
            return this;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent home = new Intent(Intent.ACTION_MAIN);
            home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            home.addCategory(Intent.CATEGORY_HOME);
            startActivity(home);
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (canvas.views.size() != 0) {
            canvas.reselectView();
        } else {
            adapter.clearData();
            adapter.notifyDataSetChanged();
        }

        MobclickAgent.onPageStart("S20MatcherActivity");
        MobclickAgent.onResume(this);

        if (UnreadHelper.hasUnread()) {
            ((ImageButton) findViewById(R.id.menu)).setImageResource(R.drawable.nav_btn_menu_n_dot);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("S20MatcherActivity");
        MobclickAgent.onPause(this);
    }
}
