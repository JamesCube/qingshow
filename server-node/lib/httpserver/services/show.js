var mongoose = require('mongoose');
var async = require('async');
//model
var Show = require('../../model/shows');
var ShowComment = require('../../model/showComments');
var RPeopleLikeShow = require('../../model/rPeopleLikeShow');
//util
var MongoHelper = require('../helpers/MongoHelper');
var ContextHelper = require('../helpers/ContextHelper');
var RelationshipHelper = require('../helpers/RelationshipHelper');
var ResponseHelper = require('../helpers/ResponseHelper');
var RequestHelper = require('../helpers/RequestHelper');

var ServerError = require('../server-error');

var show = module.exports;

show.query = {
    'method' : 'get',
    'func' : function(req, res) {
        var _ids;
        async.waterfall([
        function(callback) {
            // Parser req
            try {
                _ids = RequestHelper.parseIds(req.queryString._ids);
                callback(null);
            } catch (err) {
                callback(ServerError.fromError(err));
            }
        },
        function(callback) {
            // Query & populate
            Show.find({
                '_id' : {
                    '$in' : _ids
                }
            }).populate('modelRef').populate('itemRefs').exec(callback);
        },
        function(shows, callback) {
            // Populate nested references
            Show.populate(shows, {
                'path' : 'itemRefs.brandRef',
                'model' : 'brands'
            }, callback);
        },
        function(shows, callback) {
            // Append followed by current user
            ContextHelper.appendShowContext(req.qsCurrentUserId, shows, callback);
        }], function(err, shows) {
            ResponseHelper.response(res, err, {
                'shows' : shows
            });
        });
    }
};

show.like = {
    'method' : 'post',
    'permissionValidators' : ['loginValidator'],
    'func' : function(req, res) {
        var targetRef, initiatorRef;
        async.waterfall([
        function(callback) {
            try {
                var param = req.body;
                targetRef = RequestHelper.parseId(param._id);
                initiatorRef = req.qsCurrentUserId;
            } catch (err) {
                callback(err);
            }
            callback();
        },
        function(callback) {
            // Like
            RelationshipHelper.create(RPeopleLikeShow, initiatorRef, targetRef, function(err, relationship) {
                callback(err);
            });
        },
        function(callback) {
            // Count
            Show.update({
                '_id' : targetRef
            }, {
                '$inc' : {
                    'numLike' : 1
                }
            }, function(err, numUpdated) {
                callback(err);
            });
        }], function(err) {
            ResponseHelper.response(res, err);
        });

    }
};

show.unlike = {
    'method' : 'post',
    'permissionValidators' : ['loginValidator'],
    'func' : function(req, res) {
        try {
            var param = req.body;
            var targetRef = mongoose.mongo.BSONPure.ObjectID(param._id);
            var initiatorRef = mongoose.mongo.BSONPure.ObjectID(req.qsCurrentUserId);
        } catch (e) {
            ResponseHelper.response(res, ServerError.RequestValidationFail);
            return;
        }

        RelationshipHelper.remove(RPeopleLikeShow, initiatorRef, targetRef, function(err) {
            ResponseHelper.response(res, err);
        });
    }
};

show.queryComments = {
    'method' : 'get',
    'func' : function(req, res) {
        var pageNo, pageSize, numTotal;
        var _id;
        async.waterfall([
        function(callback) {
            // Parse request
            try {
                var param = req.queryString;
                pageNo = parseInt(param.pageNo || 1), pageSize = parseInt(param.pageSize || 20);
                _id = mongoose.mongo.BSONPure.ObjectID(param._id);
                callback(null);
            } catch(err) {
                callback(ServerError.fromError(err));
            }
        },
        function(callback) {
            // Query
            var criteria = {
                'targetRef' : _id,
                'delete' : null
            };
            MongoHelper.queryPaging(ShowComment.find(criteria).sort({
                'create' : -1
            }).populate('authorRef').populate('atRef'), ShowComment.find(criteria), pageNo, pageSize, function(err, count, showComments) {
                numTotal = count;
                callback(err, showComments);
            });
        }], function(err, showComments) {
            // Response
            ResponseHelper.responseAsPaging(res, err, {
                'showComments' : showComments
            }, pageSize, numTotal);
        });
    }
};

show.comment = {
    'method' : 'post',
    'permissionValidators' : ['loginValidator'],
    'func' : function(req, res) {
        try {
            var param = req.body;
            var targetRef = mongoose.mongo.BSONPure.ObjectID(param._id);
            var atRef = mongoose.mongo.BSONPure.ObjectID(param._atId);
            var comment = param.comment;
        } catch (err) {
            ResponseHelper.response(res, err);
            return;
        }
        async.waterfall([
        function(callback) {
            var showComment = new ShowComment({
                'targetRef' : targetRef,
                'atRef' : atRef,
                'authorRef' : req.qsCurrentUserId,
                'comment' : comment
            });
            showComment.save(function(err) {
                callback();
            });
        }], function(err) {
            ResponseHelper.response(res, err);
        });
    }
};

show.deleteComment = {
    'method' : 'post',
    'permissionValidators' : ['loginValidator'],
    'func' : function(req, res) {
        try {
            var param = req.body;
            var _id = mongoose.mongo.BSONPure.ObjectID(param._id);
        } catch (err) {
            ResponseHelper.response(res, err);
            return;
        }
        async.waterfall([
        function(callback) {
            ShowComment.findOne({
                '_id' : _id,
                'authorRef' : req.qsCurrentUserId,
                'delete' : null
            }, callback);
        },
        function(comment, callback) {
            if (comment) {
                comment.set('delete', new Date());
                comment.save(function(err) {
                    callback();
                });
            } else {
                callback();
            }
        }], function(err) {
            ResponseHelper.response(res, err);
        });
    }
};
