var mongoose = require('mongoose');
var async = require('async');
//model
var People = require('../../model/peoples');
var RPeopleFollowPeople = require('../../model/rPeopleFollowPeople');
//util
var ServiceHelper = require('../helpers/ServiceHelper');
var MongoHelper = require('../helpers/MongoHelper');
var ContextHelper = require('../helpers/ContextHelper');
var RelationshipHelper = require('../helpers/RelationshipHelper');
var ResponseHelper = require('../helpers/ResponseHelper');
var RequestHelper = require('../helpers/RequestHelper');

var ServerError = require('../server-error');

var _queryModels = function(req, res) {
    var pageNo, pageSize, numTotal;
    async.waterfall([
    function(callback) {
        // Parse request
        try {
            var param = req.queryString;
            pageNo = parseInt(param.pageNo || 1), pageSize = parseInt(param.pageSize || 10);
            callback(null);
        } catch(err) {
            callback(ServerError.fromError(err));
        }
    },
    function(callback) {
        // Query
        var criteria = {
            'roles' : 1
        };
        MongoHelper.queryPaging(People.find(criteria), People.find(criteria), pageNo, pageSize, function(err, count, peoples) {
            numTotal = count;
            callback(err, peoples);
        });
    },
    function(peoples, callback) {
        ContextHelper.appendPeopleContext(req.qsCurrentUserId, peoples, callback);
    }], function(err, peoples) {
        // Response
        ResponseHelper.responseAsPaging(res, err, {
            'peoples' : peoples
        }, pageSize, numTotal);
    });
};

var _queryFollowers = function(req, res) {
    ServiceHelper.queryRelatedPeoples(req, res, RPeopleFollowPeople, 'targetRef', 'initiatorRef');
};

var _queryFollowed = function(req, res) {
    ServiceHelper.queryRelatedPeoples(req, res, RPeopleFollowPeople, 'initiatorRef', 'targetRef');
};

var _follow = function(req, res) {
    try {
        var param = req.body;
        var targetRef = mongoose.mongo.BSONPure.ObjectID(param._id);
        var initiatorRef = mongoose.mongo.BSONPure.ObjectID(req.qsCurrentUserId);
    } catch (e) {
        ResponseHelper.response(res, ServerError.PeopleNotExist);
        return;
    }

    RelationshipHelper.create(RPeopleFollowPeople, initiatorRef, targetRef, function(err) {
        ResponseHelper.response(res, err);
    });
};

var _unfollow = function(req, res) {
    try {
        var param = req.body;
        var targetRef = mongoose.mongo.BSONPure.ObjectID(param._id);
        var initiatorRef = mongoose.mongo.BSONPure.ObjectID(req.qsCurrentUserId);
    } catch (e) {
        ResponseHelper.response(res, ServerError.PeopleNotExist);
        return;
    }

    RelationshipHelper.remove(RPeopleFollowPeople, initiatorRef, targetRef, function(err) {
        ResponseHelper.response(res, err);
    });
};

module.exports = {
    'queryModels' : {
        method : 'get',
        func : _queryModels
    },
    'queryFollowers' : {
        method : 'get',
        func : _queryFollowers
    },
    'queryFollowed' : {
        method : 'get',
        func : _queryFollowed
    },
    'follow' : {
        method : 'post',
        func : _follow,
        permissionValidators : ['loginValidator']
    },
    'unfollow' : {
        method : 'post',
        func : _unfollow,
        permissionValidators : ['loginValidator']
    }
};