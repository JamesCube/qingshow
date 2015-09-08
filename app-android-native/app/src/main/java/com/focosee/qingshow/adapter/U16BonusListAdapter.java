package com.focosee.qingshow.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;

import com.focosee.qingshow.R;
import com.focosee.qingshow.activity.S10ItemDetailActivity;
import com.focosee.qingshow.model.vo.mongo.MongoPeople;
import com.focosee.qingshow.util.ImgUtil;
import com.focosee.qingshow.util.TimeUtil;
import com.focosee.qingshow.util.adapter.AbsAdapter;
import com.focosee.qingshow.util.adapter.AbsViewHolder;
import com.focosee.qingshow.util.people.PeopleHelper;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by Administrator on 2015/9/1.
 */
public class U16BonusListAdapter extends AbsAdapter<MongoPeople.Bonuses> {

    /**
     * viewType的顺序的layoutId的顺序一致
     *
     * @param datas
     * @param context
     * @param layoutId
     */
    public U16BonusListAdapter(@NonNull List<MongoPeople.Bonuses> datas, Context context, int... layoutId) {
        super(datas, context, layoutId);
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public void onBindViewHolder(AbsViewHolder holder, int position) {
        final MongoPeople.Bonuses bonuses = datas.get(position);
        if (null == bonuses) return;
        holder.setText(R.id.item_u16_description, bonuses.notes);
        holder.setText(R.id.item_u16_time, TimeUtil.formatDateTime(bonuses.create
                , new SimpleDateFormat("yyyy.MM.dd HH:mm:ss")));
        holder.setText(R.id.item_u16_money, PeopleHelper.getBonusesMoneySign(bonuses));

        holder.setImgeByController(R.id.item_u16_portrait, ImgUtil.getImgSrc(bonuses.icon, ImgUtil.Meduim), 1f);

        holder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, S10ItemDetailActivity.class);

                if (null != bonuses.trigger) {
                    if (!TextUtils.isEmpty(bonuses.trigger.itemRef)) {
                        intent.putExtra(S10ItemDetailActivity.BONUSES_ITEMID, bonuses.trigger.itemRef);
                    }
                }
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return null == datas ? 0 : datas.size();
    }
}
