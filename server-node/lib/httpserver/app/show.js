var mongoose = require('mongoose');
var async = require('async');
//model
var Show = require('../../models').Show;
var Item = require('../../models').Item;
var ShowComment = require('../../models').ShowComment;
var RPeopleLikeShow = require('../../models').RPeopleLikeShow;
var RPeopleShareShow = require('../../models').RPeopleShareShow;
var People = require('../../models').People;
var jPushAudiences = require('../../models').JPushAudience;

//util
var MongoHelper = require('../../helpers/MongoHelper');
var ContextHelper = require('../../helpers/ContextHelper');
var RelationshipHelper = require('../../helpers/RelationshipHelper');
var ResponseHelper = require('../../helpers/ResponseHelper');
var RequestHelper = require('../../helpers/RequestHelper');
var PushNotificationHelper = require('../../helpers/PushNotificationHelper');

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
            }).populate('ownerRef').populate('itemRefs').exec(callback);
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
            var targetRef = RequestHelper.parseId(param._id);
            var initiatorRef = req.qsCurrentUserId;
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
        ServiceHelper.queryPaging(req, res, function(qsParam, callback) {
            // querier
            var criteria = {
                'targetRef' : qsParam._id,
                'delete' : null
            };
            MongoHelper.queryPaging(ShowComment.find(criteria).sort({
                'create' : -1
            }).populate('authorRef').populate('atRef'), ShowComment.find(criteria), qsParam.pageNo, qsParam.pageSize, callback);
        }, function(models) {
            // responseDataBuilder
            return {
                'showComments' : models
            };
        }, {
            'afterParseRequest' : function(raw) {
                return {
                    '_id' : mongoose.Types.ObjectId(raw._id)
                };
            }
        });

    }
};

show.comment = {
    'method' : 'post',
    'permissionValidators' : ['loginValidator'],
    'func' : function(req, res) {
        try {
            var param = req.body;
            var targetRef = mongoose.Types.ObjectId(param._id);
            var atRef = mongoose.Types.ObjectId(param._atId);
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
        }, 
        function(callback) {
            Show.findOne({
                '_id' : targetRef
            }).populate('ownerRef').exec(function(err, show) {
                if (show && show.ownerRef) {
                    if (show.ownerRef._id.toString() != req.qsCurrentUserId.toString()) {
                        jPushAudiences.find({
                            'peopleRef' : show.ownerRef
                        }).exec(function(err, infos) {
                            if (infos.length > 0) {
                                var targets = [];
                                infos.forEach(function(element) {
                                    if (element.registrationId && element.registrationId.length > 0) {
                                        targets.push(element.registrationId);
                                    }
                                });

                                PushNotificationHelper.push(targets, PushNotificationHelper.MessageNewShowComment, {
                                    'id' : param._id,
                                    'command' : PushNotificationHelper.CommandNewShowComments
                                }, null);
                            }
                        });
                    }
                }
            }); 
            callback();
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
            var _id = mongoose.Types.ObjectId(param._id);
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

show.share= {
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
            RelationshipHelper.create(RPeopleShareShow, initiatorRef, targetRef, function(err, relationship) {
                callback(err);
            });
        }], function(err) {
            ResponseHelper.response(res, err);
        });

    }
};
