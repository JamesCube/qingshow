package com.focosee.qingshow.httpapi.response;

import com.focosee.qingshow.entity.metadata.PagingMetadata;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;

/**
 * Created by i068020 on 2/8/15.
 */
public class MetadataParser {
    public static boolean hasError(JSONObject response) {
        try {
            return response.getJSONObject("metadata").has("error");
        } catch (JSONException e) {
            return false;
        }
    }

    public static int getError(String response) {
        try {
            return getError(new JSONObject(response));
        } catch (JSONException e) {
        }
        return -1;
    }

    public static int getError(JSONObject response) {
        try {
            if (response.getJSONObject("metadata").has("error")) {
                return response.getJSONObject("metadata").getInt("error");
            } else {
                return -1;
            }
        } catch (JSONException e) {
            return -1;
        }
    }

    public static PagingMetadata parsePaging(JSONObject response) {
        try {
            String metadataString = response.getJSONObject("metadata").toString();
            Type metadataType = new TypeToken<PagingMetadata>() {
            }.getType();
            Gson gson = new Gson();
            return gson.fromJson(metadataString, metadataType);
        } catch (JSONException e) {
            return null;
        }
    }

    public static String getNumTotal(JSONObject response) {
        PagingMetadata pagingMetadata = parsePaging(response);
        if (pagingMetadata == null) {
            return "0";
        } else {
            return pagingMetadata.numTotal + "";
        }
    }

    public static String getRefreshTime(JSONObject response) {
        try {
            return ((JSONObject) response.get("metadata")).get("refreshTime").toString();
        } catch (JSONException e) {
            return "";
        }
    }
}
