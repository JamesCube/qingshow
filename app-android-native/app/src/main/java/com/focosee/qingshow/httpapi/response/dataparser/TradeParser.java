package com.focosee.qingshow.httpapi.response.dataparser;

import com.focosee.qingshow.httpapi.gson.QSGsonFactory;
import com.focosee.qingshow.model.vo.mongo.MongoTrade;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.LinkedList;

/**
 * Created by Administrator on 2015/3/13.
 */
public class TradeParser {
    public static LinkedList<MongoTrade> parseQuery(JSONObject response) {
        try {
<<<<<<< Updated upstream
            String trades = response.getJSONObject("data").getJSONArray("trades").toString();
            Gson gson = QSGsonFactory.create();
            return gson.fromJson(trades, new TypeToken<LinkedList<MongoTrade>>() {
            }.getType());
        } catch (JSONException e) {
            return null;
        }
    }

    public static MongoTrade parse(JSONObject response) {
        try {
            String trade = response.getJSONObject("data").getJSONObject("trade").toString();
=======
            String shows = response.getJSONObject("data").getJSONArray("trades").toString();
>>>>>>> Stashed changes
            Gson gson = QSGsonFactory.create();
            return gson.fromJson(trade, new TypeToken<MongoTrade>() {
            }.getType());
        } catch (JSONException e) {
            return null;
        }
    }
}
