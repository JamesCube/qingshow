package com.focosee.qingshow.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.focosee.qingshow.R;
import com.focosee.qingshow.entity.ShowDetailEntity;
import com.focosee.qingshow.util.AppUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import java.util.ArrayList;

public class P04BrandItemListAdapter extends BaseAdapter {

    private static String TAG = "P04BrandItemListAdapter";
    private Context context;
    private ArrayList<ShowDetailEntity.RefItem> itemList;
    private ItemViewHolder viewHolder;
    private int itemHeight;

    public P04BrandItemListAdapter(Context concreteContext, int screenHeight,
                                   ArrayList<ShowDetailEntity.RefItem> concreteItemList) {
        context = concreteContext;
        itemList = concreteItemList;
        this.itemHeight = screenHeight / 2;
    }

    @Override
    public int getCount() {
        return (null != itemList) ? itemList.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return (null != itemList) ? itemList.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(R.layout.item_p04_item_list, null);

            viewHolder = new ItemViewHolder();
            //viewHolder.image = (ViewPager)convertView.findViewById(R.id.item_p04_item_image);
            viewHolder.viewPager = (ViewPager) convertView.findViewById(R.id.item_p04_item_viewPager);
            viewHolder.viewGroup = (LinearLayout) convertView.findViewById(R.id.item_p04_item_viewGroup);
            viewHolder.discount = (TextView) convertView.findViewById(R.id.item_p04_item_discount);
            viewHolder.descirption = (TextView) convertView.findViewById(R.id.item_p04_item_description);
            viewHolder.price = (TextView) convertView.findViewById(R.id.item_p04_item_price);
            viewHolder.detailButton = (ImageButton) convertView.findViewById(R.id.item_p04_item_detail_button);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ItemViewHolder)convertView.getTag();
        }

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                itemHeight);

        viewHolder.viewPager.setLayoutParams(params);
        //ImageLoader.getInstance().displayImage(itemList.get(position).getSource(), viewHolder.image);
        //viewHolder.discount.setText(itemList.get(position).getPrice());
        viewHolder.price.setText(itemList.get(position).getPrice());
        viewHolder.detailButton.setTag(position);
        viewHolder.detailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = Integer.getInteger(((ImageButton)v).getTag().toString()).intValue();
                Toast.makeText(context, "click item " + String.valueOf(position) + " button", Toast.LENGTH_SHORT).show();
            }
        });

        //if(null != itemList.get(position).images) {
        P04ViewPagerAdapter mViewPagerAdapter = new P04ViewPagerAdapter(itemList.get(position).images);

        viewHolder.viewPager.setAdapter(mViewPagerAdapter);
        viewHolder.viewPager.setOnPageChangeListener(mViewPagerAdapter);
        viewHolder.viewPager.setCurrentItem(itemList.get(position).images.size());
        //}

        return convertView;
    }


    public void resetData(ArrayList<ShowDetailEntity.RefItem> newData) {
        this.itemList = newData;
    }

    public void addData(ArrayList<ShowDetailEntity.RefItem> moreData) {
        this.itemList.addAll(this.itemList.size(), moreData);
    }

    class ItemViewHolder {
        public ViewPager viewPager;
        public TextView discount;
        public TextView descirption;
        public TextView price;
        public ImageButton detailButton;
        public LinearLayout viewGroup;
    }


    class P04ViewPagerAdapter extends PagerAdapter implements ViewPager.OnPageChangeListener{

        private ArrayList<ShowDetailEntity.ImageInfo> images;
        private ImageView[] _mImgViewS;
        private int imgSize;
        private LinearLayout _mViewGroup;
        /**
         * 装点点的ImageView数组
         */
        private ImageView[] tips;

        public P04ViewPagerAdapter(ArrayList<ShowDetailEntity.ImageInfo> images){

            _mViewGroup = viewHolder.viewGroup;
            this.images = images;
            this.imgSize = images.size() == 0 ? 1 : images.size();

            initImageViewList();
            initTips();
        }

        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(_mImgViewS[position % this.imgSize]);
        }

        /**
         * 载入图片进去，用当前的position 除以 图片数组长度取余数是关键
         */
        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            ImageView imageView = _mImgViewS[position % this.imgSize];
            ShowDetailEntity.ImageInfo imgInfo = (ShowDetailEntity.ImageInfo)imageView.getTag();

            ImageLoader.getInstance().displayImage(imgInfo.url, imageView, AppUtil.getShowDisplayOptions());
            container.addView(imageView, 0);
            Log.d(TAG, imgInfo.url);

            return imageView;
        }

        private void initTips(){
            tips = new ImageView[this.imgSize];
            _mViewGroup.removeAllViews();
            for (int i = 0; i < tips.length; i++) {
                ImageView imageView_tips = new ImageView(context);
                imageView_tips.setLayoutParams(new LinearLayout.LayoutParams(10, 10));
                tips[i] = imageView_tips;
                if (i == 0) {
                    tips[i].setBackgroundResource(R.drawable.image_indicator_focus);
                } else {
                    tips[i].setBackgroundResource(R.drawable.image_indicator);
                }

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                layoutParams.leftMargin = 5;
                layoutParams.rightMargin = 5;
                _mViewGroup.addView(imageView_tips, layoutParams);
            }
        }

        private void initImageViewList(){
            _mImgViewS = new ImageView[this.imgSize];
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                    , ViewGroup.LayoutParams.MATCH_PARENT);

            int index = 0;

            for(ShowDetailEntity.ImageInfo imgInfo : images){

                ImageView imageView = new ImageView(context);
                imageView.setLayoutParams(params);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setTag(imgInfo);
                _mImgViewS[index] = imageView;
                index++;
            }


        }

        /**
         * 设置选中的tip的背景
         *
         * @param selectItems
         */
        private void setImageBackground(int selectItems) {


            for (int i = 0; i < tips.length; i++) {
                if (i == (selectItems % this.imgSize)) {
                    tips[i].setBackgroundResource(R.drawable.image_indicator_focus);
                } else {
                    tips[i].setBackgroundResource(R.drawable.image_indicator);
                }
            }
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            setImageBackground(position);
            viewHolder.detailButton.setTag(position % this.imgSize);
            viewHolder.descirption.setText(((ShowDetailEntity.ImageInfo)_mImgViewS[position % this.imgSize].getTag()).description);
            Log.d(TAG, "description" + ((ShowDetailEntity.ImageInfo)_mImgViewS[position % this.imgSize].getTag()).description);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }
}
