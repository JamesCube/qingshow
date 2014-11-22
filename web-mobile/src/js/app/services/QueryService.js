// @formatter:off
define([
    'app/services/DataService'
], function(DataService) {
// @formatter:on
    /**
     * The top level dom element, which will fit to screen
     */
    var QueryService = {};

    QueryService.show = function(_id, callback) {
        _shows([_id], callback);
    };

    QueryService.shows = function(_ids, callback) {
        _shows(_ids, callback);
    };

    QueryService.PITEM_PAGE_SIZE = 20;

    QueryService.pItemsByCategories = function(categories, pageNo, callback) {
        DataService.request('GET', '/query/pItemsByCategories', {
            'categories' : categories,
            'pageNo' : pageNo,
            'pageSize' : QueryService.PITEM_PAGE_SIZE
        }, callback);
    };

    QueryService.pShowsByModel = function(_id, callback) {
        DataService.request('GET', '/query/pShowsByModel', {
            '_id' : _id
        }, callback);
    };

    var _shows = function(_ids, callback) {
        DataService.request('GET', '/query/shows', {
            '_ids' : _ids
        }, DataService.injectBeforeCallback(callback, function(metadata, data, model) {
            data.shows.forEach(function(show, index) {
                model.cacheShow(show._id, show);
            });
        }));
    };
    return QueryService;
});
