package com.focosee.qingshow.constants.config;

/**
 * Created by jackyu on 11/22/14.
 */
public class QSAppWebAPI {
    public static final String USER_DEFAULT_PORTRAIT = "http://trial01.focosee.com/img//user/portrait/1.png";//识别头像是否是用倾秀上传
//    public static final String HOST_ADDRESS = "http://chinshow.com";
    public static final String HOST_ADDRESS = "http://192.168.1.110:30001";

    private static final String HOST_NAME = HOST_ADDRESS + "/services";

    public static final String LOGIN_SERVICE_URL = HOST_NAME + "/user/login";
    public static final String REGISTER_SERVICE_URL = HOST_NAME + "/user/register";
    public static final String UPDATE_SERVICE_URL = HOST_NAME + "/user/update";
    public static final String GET_SERVICE_URL = HOST_NAME + "/user/get";
    private static final String USER_SAVE_RECEIVER_API = HOST_NAME + "/user/saveReceiver";
    private static final String USER_REMOVE_RECEIVER_API = HOST_NAME + "/user/removeReceiver";
    private static final String USER_LOGIN_WX_API = HOST_NAME + "/user/loginViaWeixin";
    public static final String USER_LOGOUT = HOST_NAME + "/user/logout";

    private static final String USER_LOGIN_WB_API = HOST_NAME + "/user/loginViaWeibo";
    private static final String FEEDING_LIKE = HOST_NAME + "/feeding/like";
    private static final String SHOW_DETAIL_API = HOST_NAME + "/show/query";

    private static final String SHOW_COMMENTS_LIST_API = HOST_NAME + "/show/queryComments";
    private static final String COMMENT_POST_API = HOST_NAME + "/show/comment";
    private static final String COMMENT_DELETE_API = HOST_NAME + "/show/deleteComment";

    private static final String PEOPLE_FOLLOW_API = HOST_NAME + "/people/follow";
    private static final String PEOPLE_UNFOLLOW_API = HOST_NAME + "/people/unfollow";

    private static final String SHOW_LIKE_API = HOST_NAME + "/show/like";
    private static final String SHOW_UNLIKE_API = HOST_NAME + "/show/unlike";

    private static final String QUERY_PEOPLE_FOLLOWER_API = HOST_NAME + "/people/queryFollowers";

    private static final String PREVIEW_QUERYCOMMENTS_API = HOST_NAME + "/preview/queryComments";
    private static final String PREVIEW_COMMENT_POST_API = HOST_NAME + "/preview/comment";
    private static final String PREVIEW_COMMENT_DEL_API = HOST_NAME + "/preview/deleteComment";

    private static final String USER_UPDATEPORTRAIT = HOST_NAME + "/user/updatePortrait";
    private static final String USER_UPDATEBACKGROUND = HOST_NAME + "/user/updateBackground";

    private static final String TRADE_CREATE_API = HOST_NAME + "/trade/create";
    private static final String TRADE_QUERY_API = HOST_NAME + "/trade/queryCreatedBy";
    private static final String TRADE_QUERY = HOST_NAME + "/trade/query";
    private static final String TRADE_STATUSTO_API = HOST_NAME + "/trade/statusTo";
    private static final String TRADE_REFRESH = HOST_NAME + "/trade/refreshPaymentStatus";
    private static final String TOP_API = HOST_NAME + "/feeding/hot";
    private static final String BYDATE_API = HOST_NAME + "/feeding/byRecommendDate";

    private static final String SPREAD_FIRSTLANUCH_API = HOST_NAME + "/spread/firstLaunch";


    private static final String MATCH_HOT_API = HOST_NAME + "/feeding/matchHot";
    private static final String MATCH_NEW_API = HOST_NAME + "/feeding/matchNew";

    private static final String QUERY_CATEGORIES = HOST_NAME + "/matcher/queryCategories";

    private static final String QUERY_ITEMS = HOST_NAME + "/matcher/queryItems";
    private static final String MATCH_SAVE = HOST_NAME + "/matcher/save";
    private static final String UPDATE_COVER = HOST_NAME + "/matcher/updateCover";
    private static final String MATCH_HIDE_API = HOST_NAME + "/matcher/hide";

    private static final String MATCH_CREATEDBY_API = HOST_NAME + "/feeding/matchCreatedBy";

    private static final String PEOPLE_QUERY_FOLLOW_PEOPLES_API = HOST_NAME + "/people/queryFollowingPeoples";
    private static final String PEOPLE_QUERY_API = HOST_NAME + "/people/query";
    private static final String PAY_API = HOST_NAME + "/trade/prepay";
    private static final String TRADE_SHARE_API = HOST_NAME + "/trade/share";

    private static final String FEEDING_RECOMMENDATION_API = HOST_NAME + "/feeding/recommendation";

    private static final String TRADE_QUERYBY_PHASE_API = HOST_NAME + "/trade/queryByPhase";
    private static final String ITEM_SYNC_API = HOST_NAME + "/item/sync";

    public static String getTradeQuerybyPhaseApi(String phases, int pageNo, int pageSize) {
        return TRADE_QUERYBY_PHASE_API + "?phases=" + phases + "&pageNo=" + pageNo + "&pageSize=" + pageSize;
    }

    public static String getItemSyncApi() {
        return ITEM_SYNC_API;
    }

    public static String getMatchHideApi() {
        return MATCH_HIDE_API;
    }

    public static String getPayApi() {
        return PAY_API;
    }

    public static String getTradeShareApi() {
        return TRADE_SHARE_API;
    }

    public static String getPeopleQueryApi(String _ids) {
        return PEOPLE_QUERY_API + "?_ids=" + _ids;
    }

    public static String getMatchSaveApi() {
        return MATCH_SAVE;
    }

    public static String getUpdateMatchCoverApi() {
        return UPDATE_COVER;
    }

    public static String getUserLoginWbApi() {
        return USER_LOGIN_WB_API;
    }

    public static String getUserLoginWxApi() {
        return USER_LOGIN_WX_API;
    }

    public static String getQueryItems(int pageNo, int pageSize, String categoryRef) {
        return QUERY_ITEMS + "?pageNo=" + pageNo + "&pageSize=" + pageSize + "&categoryRef=" + categoryRef;
    }

    public static String getPeopleQueryFollowPeoplesApi(String _id, int pageNo, int pageSize) {
        return PEOPLE_QUERY_FOLLOW_PEOPLES_API + "?_id=" + _id + "&pageNo=" + pageNo + "&pageSize=" + pageSize;
    }

    public static String getMatchCreatedbyApi(String _id, int pageNo, int pageSize) {
        return MATCH_CREATEDBY_API + "?_id=" + _id + "&pageNo=" + pageNo + "&pageSize=" + pageSize;
    }

    public static String getMatchHotApi(int pageNo, int pageSize) {
        return MATCH_HOT_API + "?pageNo=" + pageNo + "&pageSize=" + pageSize;
    }

    public static String getMatchNewApi(int pageNo, int pageSize) {
        return MATCH_NEW_API + "?pageNo=" + pageNo + "&pageSize=" + pageSize;
    }

    public static String getQueryCategories() {
        return QUERY_CATEGORIES;
    }


    public static String getSpreadFirstlanuchApi() {
        return SPREAD_FIRSTLANUCH_API;
    }


    public static String getBydateApi(String date) {
        return BYDATE_API + "?date=" + date;
    }

    public static String getTopApi() {
        return TOP_API;
    }

    public static String getFeedingRecommendationApi() {
        return FEEDING_RECOMMENDATION_API;
    }

    public static String getTradeRefreshApi() {
        return TRADE_REFRESH;
    }

    public static String getUserRemoveReceiverApi() {
        return USER_REMOVE_RECEIVER_API;
    }

    public static String getTradeCreateApi() {
        return TRADE_CREATE_API;
    }

    public static String getUserSaveReceiverApi() {
        return USER_SAVE_RECEIVER_API;
    }

    public static String getTradeStatustoApi() {
        return TRADE_STATUSTO_API;
    }

    public static String getTradeQueryApi(String _id, int pageNo, int pageSize, boolean inProgress) {
        return TRADE_QUERY_API + "?_id=" + _id + "&inProgress=" + inProgress + "&pageNo=" + pageNo + "&pageSize=" + pageSize;
    }

    public static String getTradeApi(String _id){
        return TRADE_QUERY + "?_ids=" + _id;
    }

    public static String getUserApi() {
        return GET_SERVICE_URL;
    }

    public static String getPreviewQuerycommentsApi(String _id, int pageIndex, int pageSize) {
        return PREVIEW_QUERYCOMMENTS_API + "?_id=" + _id + "&pageNo=" + pageIndex + "&pageSize=" + pageSize;
    }

    public static String getFeedingLikeApi(String _id, int pageNo, int pageSize) {
        return FEEDING_LIKE + "?_id=" + _id + "&pageNo=" + pageNo + "&pageSize=" + pageSize;
    }

    public static String getShowDetailApi(String showId) {
        return SHOW_DETAIL_API + "?_ids=" + showId;
    }

    public static String getShowCommentsListApi(String showId, int pageIndex, int pageSize) {
        return SHOW_COMMENTS_LIST_API + "?_id=" + showId + "&pageNo=" + pageIndex + "pageSize=" + pageSize;
    }

    public static String getCommentPostApi(int API_TYPE) {
        if (API_TYPE == 1) {//preview
            return PREVIEW_COMMENT_POST_API;
        }
        return COMMENT_POST_API;
    }

    public static String getCommentDeleteApi(int API_TYPE) {
        if (API_TYPE == 1) {
            return PREVIEW_COMMENT_DEL_API;
        }
        return COMMENT_DELETE_API;
    }

    public static String getPeopleFollowApi() {
        return PEOPLE_FOLLOW_API;
    }

    public static String getPeopleUnfollowApi() {
        return PEOPLE_UNFOLLOW_API;
    }

    public static String getShowLikeApi() {
        return SHOW_LIKE_API;
    }

    public static String getShowUnlikeApi() {
        return SHOW_UNLIKE_API;
    }

    public static String getQueryPeopleFollowerApi(String _id, int pageNo, int pageSize) {
        return QUERY_PEOPLE_FOLLOWER_API + "?_id=" + _id + "&pageNo=" + pageNo + "&paegSize=" + pageSize;
    }

    public static String getUserUpdatebackground() {
        return USER_UPDATEBACKGROUND;
    }

    public static String getUserUpdateportrait() {
        return USER_UPDATEPORTRAIT;
    }
}
