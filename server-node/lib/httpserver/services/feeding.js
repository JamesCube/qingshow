var mongoose = require('mongoose');
var async = require('async'), _ = require('underscore');
//model
var Show = require('../../model/shows');
var Peoples = require('../../model/peoples');
var RPeopleLikeShow = require('../../model/rPeopleLikeShow');
var Promotion = require('../../model/promotions');
//util
var RequestHelper = require('../helpers/RequestHelper');
var MongoHelper = require('../helpers/MongoHelper');
var ContextHelper = require('../helpers/ContextHelper');
var ServiceHelper = require('../helpers/ServiceHelper');

var ServerError = require('../server-error');

var _feed = function (req, res, querier, aspectInceptions) {
    aspectInceptions = aspectInceptions || {};
    ServiceHelper.queryPaging(req, res, querier, function (models) {
        // responseDataBuilder
        return {
            'shows' : models
        };
    }, {
        'afterParseRequest' : aspectInceptions.afterParseRequest,
        'afterQuery' : function (qsParam, currentPageModels, numTotal, callback) {
            async.series([
                function (callback) {
                    // Populate
                    _.delay(function () {
                        Show.populate(currentPageModels.filter(function(show) {
                            if (show.cover && req.session && req.session.assetsRoot) {
                                show.cover = req.session.assetsRoot + show.cover;
                            }
                            return !!show;
                        }), 'modelRef', callback);
                    }, (res.qsPerformance && res.qsPerformance.d) ? res.qsPerformance.d : 1);
                },
                function (callback) {
                    // Append context
                    ContextHelper.appendShowContext(req.qsCurrentUserId, currentPageModels, callback);
                },
                function (callback) {
                    if (aspectInceptions.afterQuery) {
                        aspectInceptions.afterQuery(qsParam, currentPageModels, numTotal, callback);
                    } else {
                        callback();
                    }
                }], callback);
        },
        'beforeEndResponse' : aspectInceptions.beforeEndResponse
    });
};

var feeding = module.exports;

feeding.recommendation = {
    'method' : 'get',
    'func' : function (req, res) {
        _feed(req, res, function (qsParam, outCallback) {
            async.waterfall([
                function (callback) {
                    var userid = req.qsCurrentUserId;
                    Peoples.findOne({'_id' : userid}, callback);
                }, function (people, callback) {
                    var rate = people.weight / people.height;
                    var type = null;
                    /*
                     * 0.24~0.27属于偏瘦型（A1）
                     0.28~0.31属于标准型（A2）
                     0.32~0.40属于偏胖型（A3）
                     0.41~0.50属于超胖型（A4）
                     * */
                    if (rate < 0.275) {
                        type = 'A1';
                    } else if (rate < 0.315) {
                        type = 'A2';
                    } else if (rate < 0.405) {
                        type = 'A3';
                    } else {
                        type = 'A4';
                    }
                    var criteria = {
                        'recommend.group' : type
                    };
                    MongoHelper.queryPaging(Show.find(criteria).sort({
                        'recommend.date' : -1
                    }), Show.find(criteria), qsParam.pageNo, qsParam.pageSize, outCallback);
                }
            ], outCallback);
        }, {
            afterQuery : function (qsParam, currentPageModels, numTotal, afterQuery_cb) {
                async.series([
                    function(callback) {
                        Show.populate(currentPageModels, {
                            'path' : 'itemRefs',
                            'model' : "items"
                        }, callback);
                    }, 
                    function(callback) {
                        Show.populate(currentPageModels, {
                            'path' : 'promotionRef',
                            'model' : "promotions"
                        }, callback);
                    }], 
                    afterQuery_cb
                );
            }
        });
    }
};

feeding.hot = {
    'method' : 'get',
    'func' : function (req, res) {
        _feed(req, res, function (qsParam, out_callback) {
            var dateNum = Math.ceil(qsParam.pageSize / 2);
            var showId = [];
            async.waterfall([
                function(callback) {
                    var condition = [
                        {
                            '$group' : {
                                '_id': {
                                    'year' : {'$year' : '$recommend.date'},
                                    'month' : {'$month' : '$recommend.date'},
                                    'day' : {'$dayOfMonth' : '$recommend.date'}
                                }
                            }
                        }, {
                            $sort : { _id : -1 }
                        }
                    ];
                    MongoHelper.aggregatePaging(Show.aggregate(condition), qsParam.pageNo, dateNum, callback);
                },
                function(result, count, callback) {
                    var task = result.map(function(element) {
                        return function(in_callback) {
                            var _id = element._id;

                            var criteria = {
                                'recommend.date' : {
                                    '$gte' : new Date(_id.year, _id.month - 1, _id.day),
                                    '$lt' : new Date(_id.year, _id.month - 1, _id.day + 1) 
                                }
                            };

                            Show.find(criteria).sort({'numLike' : -1}).limit(2).exec(function(err, shows) {
                                if(!err) {
                                    shows.forEach(function(show) {
                                        showId.push(show._id);
                                    });
                                    in_callback();
                                } else {
                                    in_callback(err);
                                }
                            });
                        };
                    });
                    async.parallel(task, function(err) {
                        callback();
                    });
                },
                function(callback) {
                    var criteria = {
                        '_id' : { '$in' : showId}
                    };
                    //MongoHelper.queryPaging(Show.find(criteria).sort({
                    //    'recommend.date' : -1,
                    //    'numLike' : -1
                    //}), Show.find(criteria), qsParam.pageNo, qsParam.pageSize, out_callback);
                    Show.find(criteria).sort({
                        'recommend.date' : - 1,
                        'numLike' : -1
                    }).exec(function(err, shows) {
                        if (err) {
                            out_callback(ServerError.fromDescription(err));
                        } else {
                            out_callback(null, shows, shows.length);
                        }
                    });
                }], out_callback
            );
        }, {
            afterQuery : function (qsParam, currentPageModels, numTotal, afterQuery_cb) {
                async.series([
                    function(callback) {
                        Show.populate(currentPageModels, {
                            'path' : 'itemRefs',
                            'model' : "items"
                        }, callback);
                    }, 
                    function(callback) {
                        Show.populate(currentPageModels, {
                            'path' : 'promotionRef',
                            'model' : "promotions"
                        }, callback);
                    }], 
                    afterQuery_cb
                );
            }
        });
    }
};

feeding.like = {
    'method' : 'get',
    'func' : function (req, res) {
        _feed(req, res, function (qsParam, callback) {
            async.waterfall([
                function (callback) {
                    var criteria = {
                        'initiatorRef' : qsParam._id || req.qsCurrentUserId
                    };
                    MongoHelper.queryPaging(RPeopleLikeShow.find(criteria).sort({
                        'create' : -1
                    }).populate('targetRef'), RPeopleLikeShow.find(criteria), qsParam.pageNo, qsParam.pageSize, callback);
                },
                function (relationships, count, callback) {
                    var shows = [];
                    relationships.forEach(function (relationship) {
                        shows.push(relationship.targetRef);
                    });
                    callback(null, shows, count);
                }], callback);
        }, {
            'afterQuery' : function (qsParam, currentPageModels, numTotal, afterQuery_cb) {
                async.series([
                    function(callback) {
                        Show.populate(currentPageModels, {
                            'path' : 'itemRefs',
                            'model' : "items"
                        }, callback);
                    }, 
                    function(callback) {
                        Show.populate(currentPageModels, {
                            'path' : 'promotionRef',
                            'model' : "promotion"
                        }, callback);
                    }], 
                    afterQuery_cb
                );
            },
            'afterParseRequest' : function (raw) {
                return {
                    '_id' : RequestHelper.parseId(raw._id)
                };
            }
        });
    }
};
