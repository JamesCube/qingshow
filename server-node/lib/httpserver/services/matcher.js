var mongoose = require('mongoose');
var async = require('async');
var path = require('path');
var _ = require('underscore');

// model
var Category = require('../../model/categories');
var Item = require('../../model/items');
var Show = require('../../model/shows');

// util
var ResponseHelper = require('../helpers/ResponseHelper');
var RequestHelper = require('../helpers/RequestHelper');
var ServiceHelper = require('../helpers/ServiceHelper');
var MongoHelper = require('../helpers/MongoHelper.js');
var RelationshipHelper = require('../helpers/RelationshipHelper');

var ServerError = require('../server-error');

var matcher = module.exports;

matcher.queryCategories = {
    'method' : 'get',
    'func' : function(req, res) {
        Category.find({}).exec(function(err, categories) {
            ResponseHelper.response(res, err, {
                'categories' : categories
            });
        });
    }
};

matcher.queryItems = {
    'method' : 'get',
    'func' : function(req, res) {
        var qsParam = req.body;

        ServiceHelper.queryPaging(req, res, function(qsParam, callback) {
            var criteria = {
                'categoryRef' : RequestHelper.parseId(qsParam.categoryRef),
                '$or' : [{'delist' : {'$exists' : false}}, {'delist' : null}]
            };
            MongoHelper.queryRandom(Item.find(criteria), Item.find(criteria), qsParam.pageSize, callback);
        }, function(items) {
            return {
                'items' : items
            };
        }, {});
    }
};

var _matchers = {};

matcher.save = {
    'method' : 'post',
    'permissionValidators' : ['loginValidator'],
    'func' : function(req, res) {
        if (!req.body.itemRefs || !req.body.itemRefs.length) {
            ResponseHelper.response(res, ServerError.NotEnoughParam);
            return;
        }
        var itemRefs = RequestHelper.parseIds(req.body.itemRefs);

        var coverUrl = global.qsConfig.show.coverForeground.template;
        coverUrl = coverUrl.replace(/\{0\}/g, _.random(1, global.qsConfig.show.coverForeground.max));
        var show = new Show({
            'itemRefs' : itemRefs, 
            'ownerRef' : req.qsCurrentUserId,
            'coverForeground' : coverUrl
        });

        var uuid = require('node-uuid').v1();
        _matchers[uuid] = show;
        
        ResponseHelper.response(res, null, {
            'uuid' : uuid
        });
    }
};

matcher.updateCover = {
    'method' : 'post',
    'permissionValidators' : ['loginValidator'],
    'func' : function(req, res) {
        RequestHelper.parseFile(req, global.qsConfig.uploads.show.cover.ftpPath, [
            {'suffix' : '_s', 'rate' : 0.5},
            {'suffix' : '_xs', 'rate' : 0.25}
        ], function (err, fields, file) {
            if (err) {
                ResponseHelper.response(res, err);
                return;
            }
            if (!fields.uuid || !fields.uuid.length) {
                ResponseHelper.response(res, ServerError.NotEnoughParam);
                return;
            }
            if (!file) {
                ResponseHelper.response(res, ServerError.NotEnoughParam);
                return;
            }
            var show = _matchers[fields.uuid];
            if (!show) {
                ResponseHelper.response(res, ServerError.NotEnoughParam);
            }
            show.set('cover', global.qsConfig.uploads.show.cover.exposeToUrl + '/' + path.relative(global.qsConfig.uploads.show.cover.ftpPath, file.path));
            show.save(function(err, show) {
                ResponseHelper.response(res, err, {
                    'show' : show
                });
            });
            delete _matchers[fields.uuid];
        });
    }
};

matcher.hide = {
    'method' : 'post',
    'permissionValidators' : ['loginValidator'],
    'func' : function(req, res) {
        async.waterfall([
        function(callback) {
            Show.findOne({
                '_id' : RequestHelper.parseId(req.body._id),
                'ownerRef' : req.qsCurrentUserId
            }, function(err, show) {
                callback(err, show);
            });
        },
        function(show, callback) {
            if (show == null) {
                callback(ServerError.ShowNotExist);
                return;
            }
            show.hideAgainstOwner = true;
            show.save(function(err, show) {
                callback(err, show);
            });
        }],
        function(err, results) {
            if (!results) {
                ResponseHelper.response(res, ServerError.ShowNotExist);
            } else {
                ResponseHelper.response(res, err);
            }
        });
    }
};