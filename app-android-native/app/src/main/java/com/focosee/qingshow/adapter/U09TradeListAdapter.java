package com.focosee.qingshow.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.Response;
import com.focosee.qingshow.QSApplication;
import com.focosee.qingshow.R;
import com.focosee.qingshow.activity.S17PayActivity;
import com.focosee.qingshow.activity.U09TradeListActivity;
import com.focosee.qingshow.activity.U12ReturnActivity;
import com.focosee.qingshow.constants.code.StatusCode;
import com.focosee.qingshow.constants.config.QSAppWebAPI;
import com.focosee.qingshow.httpapi.request.QSJsonObjectRequest;
import com.focosee.qingshow.httpapi.request.RequestQueueManager;
import com.focosee.qingshow.httpapi.response.MetadataParser;
import com.focosee.qingshow.httpapi.response.dataparser.TradeParser;
import com.focosee.qingshow.httpapi.response.error.ErrorHandler;
import com.focosee.qingshow.model.vo.mongo.MongoTrade;
import com.focosee.qingshow.util.ShareUtil;
import com.focosee.qingshow.util.StringUtil;
import com.focosee.qingshow.util.TimeUtil;
import com.focosee.qingshow.util.adapter.*;
import com.focosee.qingshow.util.adapter.AbsViewHolder;
import com.focosee.qingshow.widget.ConfirmDialog;
import com.focosee.qingshow.widget.QSTextView;

import org.json.JSONObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 * Created by Administrator on 2015/3/16.
 */
public class U09TradeListAdapter extends AbsAdapter<MongoTrade> implements View.OnClickListener{

    public static String transaction = "";
    private final int CANCEL = 0;
    private final int COMFIRM_PAY = 1;
    private final int COMFIRM_RECEIPT = 2;
    private final int RETURN = 3;

    /**
     * viewType的顺序的layoutId的顺序一致
     *
     * @param datas
     * @param context
     * @param layoutId
     */
    public U09TradeListAdapter(@NonNull List<MongoTrade> datas, Context context, int... layoutId) {
        super(datas, context, layoutId);
    }

    @Override
    public void onBindViewHolder(AbsViewHolder holder, final int position) {
        if(position == 0){
            holder.getView(R.id.U09_head_layout).setVisibility(View.INVISIBLE);
            return;
        }
        if(null == getItemData(position))return;
        final MongoTrade trade = getItemData(position);
        if(null == trade)return;
        String dateStr = "付款日期：" + TimeUtil.formatDateTime(trade.update);
        if(trade.status == 0 || trade.status == 1){
            dateStr = "申请日期：" + TimeUtil.formatDateTime(trade.update);
        }
        holder.setText(R.id.item_tradelist_payTime, dateStr);
        final QSTextView btn1 = holder.getView(R.id.item_tradelist_btn1);
        QSTextView btn2 = holder.getView(R.id.item_tradelist_btn2);
        QSTextView statusTV = holder.getView(R.id.item_tradelist_status);
        btn1.setVisibility(View.GONE);
        btn2.setVisibility(View.GONE);
        statusTV.setVisibility(View.GONE);
        holder.getView(R.id.item_tradelist_sale_img).setVisibility(View.GONE);

//        holder.setText(R.id.item_tradelist_status, StatusCode.getStatusText(trade.status));


        holder.setText(R.id.item_tradelist_skuProperties, StringUtil.formatSKUProperties(trade.orders.get(0).selectedSkuProperties));
//        holder.setText(R.id.item_tradelist_measurement, SkuUtil.getPropValue(skus, SkuUtil.KEY.SIZE_1.id, SkuUtil.KEY.SIZE_2.id, SkuUtil.KEY.SIZE_3.id));
        holder.setText(R.id.item_tradelist_quantity, String.valueOf(trade.orders.get(0).quantity));
        holder.setText(R.id.item_tradelist_price, StringUtil.FormatPrice(String.valueOf(trade.orders.get(0).actualPrice)));
        holder.setImgeByUrl(R.id.item_tradelist_image, trade.orders.get(0).itemSnapshot.thumbnail);
        holder.setText(R.id.item_tradelist_description, trade.orders.get(0).itemSnapshot.name);

        //0-折扣申请中
        if(trade.status == 0){
            statusTV.setVisibility(View.VISIBLE);
            statusTV.setText(StatusCode.getStatusText(trade.status));
            return;
        }
        //1-等待付款
        if(trade.status == 1){
            btn1.setVisibility(View.VISIBLE);
            holder.getView(R.id.item_tradelist_sale_img).setVisibility(View.VISIBLE);
            if(null != trade.__context) {
                if(trade.__context.sharedByCurrentUser) {
                    btn1.setTag("立即付款");
                    btn1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent();
                            Bundle bundle = new Bundle();
                            bundle.putSerializable(S17PayActivity.INPUT_ITEM_ENTITY, trade);
                            intent.putExtras(bundle);
                            context.startActivity(intent);
                        }
                    });
                }else {
                    holder.getView(R.id.item_tradelist_btn1_topImg).setVisibility(View.VISIBLE);
                    btn1.setText("分享后付款");
                    btn1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            transaction = String.valueOf(System.currentTimeMillis());
                            if (QSApplication.instance().getWxApi().isWXAppInstalled()) {
                                EventBus.getDefault().post(trade);
                                ShareUtil.shareTradeToWX(trade._id, trade.peopleSnapshot._id, transaction, context, true);
                            }else
                                Toast.makeText(context, "请先安装微信，然后才能分享", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }
        //2-已付款
        if(trade.status == 2){
            btn1.setVisibility(View.VISIBLE);
            btn1.setText("取消订单");
            btn1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickCancelTrade(trade, 17, CANCEL, position);
                }
            });
        }
        //3-已发货
        if(trade.status == 3){
            btn1.setVisibility(View.VISIBLE);
            btn2.setVisibility(View.VISIBLE);
            btn1.setText("申请退货");
            btn2.setText("物流信息");
            btn2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                String msg = "暂无物流信息";
                if(null != trade.logistic) {
                    msg = "物流公司：" + trade.logistic.company + "\n物流单号：" + trade.logistic.trackingID == null ? "" : trade.logistic.trackingID;
                }
                final ConfirmDialog dialog = new ConfirmDialog();
                dialog.setTitle(msg);
                dialog.show(((U09TradeListActivity) context).getSupportFragmentManager());
                }
            });
            btn1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, U12ReturnActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(U12ReturnActivity.TRADE_ENTITY, trade);
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                }
            });
        }

    }

    private void onClickCancelTrade(final MongoTrade trade, final int status, final int type, final int position){
        final ConfirmDialog dialog = new ConfirmDialog();
        dialog.setTitle("确定要取消订单？");
        dialog.setConfirm(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                statusTo(trade, status, type, position);
                dialog.dismiss();
            }
        });
        dialog.show(((U09TradeListActivity)context).getSupportFragmentManager());
    }

    @Override
    public MongoTrade getItemData(int position) {
        return 0 == position ? null : datas.get(position - 1);
    }

    @Override
    public int getItemViewType(int position) {
        return 0 == position ? 0 : 1;
    }

    //获取数据的数量
    @Override
    public int getItemCount() {
        return null == datas ? 1 : datas.size() + 1;
    }

    public void statusTo(MongoTrade trade, int status, final int type, final int position){

        JSONObject jsonObject = getStatusJSONObjcet(trade, status);

        QSJsonObjectRequest jor = new QSJsonObjectRequest(Request.Method.POST, QSAppWebAPI.getTradeStatustoApi(), jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                System.out.println("response:" + response);
                if(MetadataParser.hasError(response)){
                    ErrorHandler.handle(context, MetadataParser.getError(response));
                    return;
                }
                responseToStatusToSuccessed(type, position, TradeParser.parse(response));
            }
        });

        RequestQueueManager.INSTANCE.getQueue().add(jor);
    }


    private JSONObject getStatusJSONObjcet(MongoTrade trade, int status){

        Map params = new HashMap();
        Map orders = new HashMap();
        Map taobaoInfo = new HashMap();
        Map logistic = new HashMap();
        Map returnLogistic = new HashMap();
        params.put("_id", trade._id);
        params.put("status", status);
        params.put("comment", (trade.statusLogs.get(0)).comment);

        switch (status){
            case 1:
                taobaoInfo.put("tradeID", trade.taobaoInfo.tradeID);
                taobaoInfo.put("userNick", trade.taobaoInfo.userNick);
                params.put("orders", orders);
                break;
            case 3:
                logistic.put("company", trade.logistic.company);
                logistic.put("trackingID", trade.logistic.trackingID);
                params.put("logistic", logistic);
                break;
            case 7:
                logistic.put("company", trade.returnlogistic.company);
                logistic.put("trackingID", trade.returnlogistic.trackingID);
                params.put("returnLogistic", returnLogistic);
                break;
        }

        return new JSONObject(params);

    }

    private void responseToStatusToSuccessed(int type, int position, MongoTrade parse){
        EventBus.getDefault().post(U09TradeListActivity.responseToStatusToSuccessed);
        datas.set(position - 1, parse);
        notifyDataSetChanged();
        switch (type){
            case CANCEL://取消订单
                Toast.makeText(context, "已取消订单", Toast.LENGTH_SHORT).show();
                break;
            case COMFIRM_RECEIPT:
                Toast.makeText(context, "已确认收货", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void onClick(View v) {

    }

    public interface OnViewHolderListener {
        void onRequestedLastItem();
    }

}
