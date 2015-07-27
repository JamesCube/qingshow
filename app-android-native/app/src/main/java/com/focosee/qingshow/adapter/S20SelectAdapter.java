package com.focosee.qingshow.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import com.focosee.qingshow.R;
import com.focosee.qingshow.model.vo.mongo.MongoItem;
import com.focosee.qingshow.util.ImgUtil;
import com.focosee.qingshow.util.adapter.*;
import com.focosee.qingshow.util.adapter.AbsViewHolder;
import com.focosee.qingshow.widget.radio.RadioLayout;
import com.sina.weibo.sdk.utils.ImageUtils;

import java.util.List;

/**
 * Created by Administrator on 2015/7/1.
 */
public class S20SelectAdapter extends AbsAdapter<MongoItem> {

    private RadioLayout lastChecked = null;
    private int selectPos = -1;

    private OnCheckedChangeListener onCheckedChangeListener = null;

    public interface OnCheckedChangeListener{
        void onCheckedChange(MongoItem datas, int position , RadioLayout view);
    }

    public S20SelectAdapter(@NonNull List<MongoItem> datas, Context context, int... layoutId) {
        super(datas, context, layoutId);
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public void onBindViewHolder(AbsViewHolder holder, int position) {
        holder.setImgeByUrl(R.id.select_view, ImgUtil.getImgSrc( datas.get(position).thumbnail,ImgUtil.Large));
        RadioLayout bgView = holder.getView(R.id.item_bg);
        holder.setText(R.id.price,datas.get(position).getPrice());
        bgView.setTag(new Integer(position));

        if (position != selectPos) {
            bgView.setChecked(false);
        } else {
            bgView.setChecked(true);
            lastChecked = bgView;
        }

        bgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RadioLayout radio = (RadioLayout) view;
                int clickedPos = ((Integer) radio.getTag()).intValue();
                radio.setChecked(true);
                if (lastChecked != null) {
                    if (lastChecked != radio) {
                        lastChecked.setChecked(false);
                    }
                }

                lastChecked = radio;
                selectPos = clickedPos;
                onCheckedChangeListener.onCheckedChange(datas.get(clickedPos),clickedPos,lastChecked);
            }
        });
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener onCheckedChangeListener) {
        this.onCheckedChangeListener = onCheckedChangeListener;
    }

    public void setLastChecked(RadioLayout lastChecked) {
        this.lastChecked = lastChecked;
    }

    public void setSelectPos(int selectPos) {
        this.selectPos = selectPos;
    }

}
