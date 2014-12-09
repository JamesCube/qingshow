package com.focosee.qingshow.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.focosee.qingshow.R;
import com.focosee.qingshow.entity.CommentEntity;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

class CommentViewHolder {
    public ImageView commentImage;
    public TextView commentName;
    public TextView commentContent;
    public TextView commentTime;
}

public class S04CommentListAdapter extends BaseAdapter {

    private ArrayList<CommentEntity> data;
    private Context context;
    private ImageLoader imageLoader;

    private DisplayImageOptions imageOptions;
    private AnimateFirstDisplayListener animateFirstListener = new AnimateFirstDisplayListener();

    public S04CommentListAdapter(Context context, ArrayList<CommentEntity> comments, ImageLoader imageLoader){
        this.context = context;
        this.data = comments;
        this.imageLoader = imageLoader;


        imageOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.root_user_background) //设置图片在下载期间显示的图片
                .showImageForEmptyUri(R.drawable.root_user_background)//设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.drawable.root_user_background)  //设置图片加载/解码过程中错误时候显示的图片
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .displayer(new RoundedBitmapDisplayer(20))//是否设置为圆角，弧度为多少
                .displayer(new FadeInBitmapDisplayer(100))//是否图片加载好后渐入的动画时间
                .build();//构建完成
    }

    @Override
    public int getCount() {
        return (null != data) ? data.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CommentViewHolder holder;
        if (convertView == null) {
            LayoutInflater layoutInflator = LayoutInflater.from(parent.getContext());
            convertView = layoutInflator.inflate(R.layout.comment_item_list, null);
            holder = new CommentViewHolder();
            holder.commentImage = (ImageView) convertView.findViewById(R.id.item_comment_user_image);
            holder.commentName = (TextView) convertView.findViewById(R.id.item_comment_user_name);
            holder.commentContent = (TextView) convertView.findViewById(R.id.item_comment_content);
            holder.commentTime = (TextView) convertView.findViewById(R.id.item_comment_time);
            convertView.setTag(holder);
        }
        holder = (CommentViewHolder)convertView.getTag();
        this.imageLoader.displayImage(data.get(position).getAuthorImage(), holder.commentImage, imageOptions, animateFirstListener);
        holder.commentName.setText(data.get(position).getAuthorName());
        holder.commentContent.setText(data.get(position).getCommentContent());
        holder.commentTime.setText(data.get(position).getCommentTime());
        return convertView;
    }

    public void resetData(ArrayList<CommentEntity> commentEntities) {
        this.data = commentEntities;
    }

    public void addDataInTail(ArrayList<CommentEntity> commentEntities) {
        this.data.addAll(this.data.size(), commentEntities);
    }

    // Animation
    private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

        static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            if (loadedImage != null) {
                Log.i("test", "load complete not null");
                ImageView imageView = (ImageView) view;
                boolean firstDisplay = !displayedImages.contains(imageUri);
                if (firstDisplay) {
                    FadeInBitmapDisplayer.animate(imageView, 500);
                    displayedImages.add(imageUri);
                }
            }
        }
    }
}