package com.focosee.qingshow.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.focosee.qingshow.R;
import com.focosee.qingshow.activity.S03SHowActivity;
import com.focosee.qingshow.model.vo.mongo.MongoShow;
import com.focosee.qingshow.util.AppUtil;
import com.focosee.qingshow.util.ImgUtil;
import com.focosee.qingshow.widget.MImageView_OriginSize;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.LinkedList;

public class P02ModelItemListAdapter extends BaseAdapter {

    private Context context;
    private LinkedList<MongoShow> itemList;
    private View view_hidden;

    public P02ModelItemListAdapter(Context concreteContext, LinkedList<MongoShow> concreteItemList) {
        context = concreteContext;
        itemList = concreteItemList;
    }

    @Override
    public Object getItem(int position) {
        return (null != itemList) ? itemList.get(position + 1) : null;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == 0) ? 0 : 1;
    }

    @Override
    public int getCount() {
        return (null == itemList) ? 0 : itemList.size() + 1;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(position == 0){
            RelativeLayout headRelativeLayout;
            if(convertView == null) {
                LayoutInflater layoutInflater = LayoutInflater.from(context);
                convertView = layoutInflater.inflate(R.layout.activity_p02_model, null);
                headRelativeLayout = (RelativeLayout)convertView.findViewById(R.id.P02_head_relative);
                headRelativeLayout.removeAllViews();
                convertView.setTag(headRelativeLayout);
            }
            headRelativeLayout = (RelativeLayout) convertView.getTag();
            return convertView;
        }

        ItemViewHolder viewHolder;
        position--;

        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(R.layout.item_p02_show_list, null);

            viewHolder = new ItemViewHolder();
            viewHolder.image = (MImageView_OriginSize) convertView.findViewById(R.id.item_p02_item_image);
            viewHolder.detailButton = (ImageButton) convertView.findViewById(R.id.item_p02_item_detail_button);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ItemViewHolder) convertView.getTag();
        }

        viewHolder.image.setOriginWidth(itemList.get(position).getHorizontalCoverWidth());
        viewHolder.image.setOriginHeight(itemList.get(position).getHorizontalCoverHeight());
        ImageLoader.getInstance().displayImage(ImgUtil.imgTo2x(itemList.get(position).getHorizontalCover()), viewHolder.image, AppUtil.getShowDisplayOptions());
        viewHolder.detailButton.setTag(position);
        final String id = itemList.get(position).get_id();
        viewHolder.detailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, S03SHowActivity.class);
                intent.putExtra(S03SHowActivity.INPUT_SHOW_ENTITY_ID, id);
                context.startActivity(intent);
            }
        });

        return convertView;
    }

    public void resetData(LinkedList<MongoShow> newData) {
        this.itemList = newData;
    }

    public void addData(LinkedList<MongoShow> moreData) {
        this.itemList.addAll(this.itemList.size(), moreData);
    }

    class ItemViewHolder {
        public MImageView_OriginSize image;
        public ImageButton detailButton;
    }

}