package com.focosee.qingshow.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.focosee.qingshow.R;
import com.focosee.qingshow.activity.S13TopicActivity;
import com.focosee.qingshow.model.vo.mongo.MongoTopic;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONObject;

import java.util.LinkedList;

/**
 * Created by Administrator on 2015/3/31.
 */
public class S12Adapter extends AbsWaterfallAdapter<MongoTopic> {


    public S12Adapter(Context context) {
        this(context,0, null);
    }

    public S12Adapter(Context context, int resourceId, ImageLoader mImageFetcher) {
        super(context, resourceId, mImageFetcher);
    }

    @Override
    public void refreshDate(JSONObject response) {

    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;
        final MongoTopic item = _data.get(position);

        if (null == convertView) {
            convertView = LayoutInflater.from(_context).inflate(R.layout.item_s12_topic, null);
            viewHolder = new ViewHolder();
            viewHolder.image = (SimpleDraweeView) convertView.findViewById(R.id.s12_image);
            viewHolder.title = (TextView) convertView.findViewById(R.id.s12_title);
            viewHolder.subtitle = (TextView) convertView.findViewById(R.id.s12_subtitle);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.image.setAspectRatio(1.37f);

        viewHolder.image.setImageURI(Uri.parse(item.cover));
        viewHolder.title.setText(item.title);
        viewHolder.subtitle.setText(item.subtitle);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(_context, S13TopicActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(S13TopicActivity.KEY, item);
                intent.putExtras(bundle);
                _context.startActivity(intent);
            }
        });

        return convertView;
    }

    class ViewHolder extends AbsViewHolder {
        public SimpleDraweeView image;
        public TextView title;
        public TextView subtitle;
    }
}
