package com.focosee.qingshow.entity;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ShowDetailEntity extends AbsEntity {


    // Cover super property
    public static final String DEBUG_TAG = "ShowDetailEntity";


    // Public interface (used in S03ShowActivity.java)
    public static ShowDetailEntity getShowDetailFromResponse(JSONObject response) {
        try {
            String tempString = response.getJSONObject("data").getJSONArray("shows").get(0).toString();
            Gson gson = new Gson();
            return gson.fromJson(tempString, ShowDetailEntity.class);
        } catch (JSONException e) {
            log(e.toString());
            return null;
        }
    }

    public String getShowVideo() {
        return (null != video) ? video : null;
    }

    public String getModelPhoto() {
        if (null != modelRef && null != modelRef.portrait)
            return modelRef.portrait;
        return null;
    }
    public String getModelName() {
        return modelRef.name;
    }
    public String getModelJob() {
        return modelRef.roles.toString();
    }
    public String getModelWeightHeight() {
        return modelRef.height + "cm/" + modelRef.weight + "kg";
    }
    public String getShowNumLike() {
        return String.valueOf(modelRef.modelInfo.numLikes);
    }
    public String getModelStatus() {
        return modelRef.modelInfo.status;
    }

    public ArrayList<RefItem> getItemsList() {
        ArrayList<RefItem> result = new ArrayList<RefItem>();
        for (RefItem item : itemRefs) {
            result.add(item);
        }
        return result;
    }
    public RefItem getItem(int index) {
        if (index >= itemRefs.length || index < 0) return null;
        return itemRefs[index];
    }
    public String getAllItemDescription() {
        String description = "";
        for (RefItem item : itemRefs)
            description += item.name;
        return description;
    }
    public List<String> getItemDescriptionList() {
        ArrayList<String> itemDescriptionList = new ArrayList<String>();
        for (RefItem item : itemRefs) {
            itemDescriptionList.add(item.name);
        }
        return itemDescriptionList;
    }

    public String[] getPosters() {
        return posters;
    }

//    public String getItem(int index) {
//        return (index < itemRefs.length) ? itemRefs[index] : null;
//    }
//
//    public ArrayList<String> getItemsList() {
//        ArrayList<String> _itemsData = new ArrayList<String>();
//        for (String item : itemRefs)
//            _itemsData.add(item);
//        return _itemsData;
//    }
//

//
//    public String getAge() {
//        return create;
//    }

//
//    public List<String> getItemUrlList() {
//        ArrayList<String> itemUrlList = new ArrayList<String>();
//        for (RefItem item : itemRefs) {
//            itemUrlList.add(item.cover);
//        }
//        return itemUrlList;
//    }



    // Inner data
    public String _id;                      // "5439f64013bf528b45f00f9a"
    public String cover;                    // "url for image source"
    public String video;                    // "/10.mp4.mp4"
    public int numLike;                  // "7777"
    public RefModel modelRef;               // "Model Object"
    public String create;                   // "2014-11-21T15:52:27.740Z"
    public RefItem[] itemRefs;          // "Item Object List"
    public String[] styles;
    public String[] posters;            // "Poster(str) List"
    public MetaDataCover coverMetadata;     // "Cover Object"
    public ShowContext __context;


    // Item object in show
    public static class RefModel extends AbsEntity {
        public String _id;
        public String name;
        public String portrait;
        public String birthtime;
        public float height;
        public float weight;
        public String[] followerRefs;
        public int __v;
        public String update;
        public String create;
        public InfoModel modelInfo;
        public String[] hairTypes;
        public int[] roles;
        public ModelContext __context;

        public static class InfoModel extends AbsEntity {
            public String status;
            public int numLikes;
        }

        public static class ModelContext {
            public Boolean followedByCurrentUser;
        }
    }

    public static class RefItem extends AbsEntity {
        public String _id;
        public int category;
        public String name;
        public String cover;
        public String source;
        public RefBrand brandRef;
        public String create;

        public static class RefBrand extends AbsEntity {
            public String _id;
            public String type;
            public String name;
            public String create;

        }
    }

    public static class MetaDataCover extends AbsEntity {
        public String cover;
        public int width;
        public int height;
    }

    public static class ShowContext {
        public int numComments;
        public int numLike;
        public Boolean likedByCurrentUser;
    }

}