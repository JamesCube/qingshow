var mongoose = require('mongoose');
var async = require('async');
// Models
var Shows = require('../dbmodels').Show;
var ShowComments = require('../dbmodels').ShowComment;
var RPeopleLikeShow = require('../dbmodels').RPeopleLikeShow;
var RPeopleShareShow = require('../dbmodels').RPeopleShareShow;
var RPeopleFollowPeople = require('../dbmodels').RPeopleFollowPeople;
var People = require('../dbmodels').People;
var Item = require('../dbmodels').Item;

/**
 * ContextHelper
 *
 * Input models then output models with context
 */
var ContextHelper = module.exports;

ContextHelper.appendPeopleContext = function(qsCurrentUserId, peoples, callback) {
    peoples = _prepare(peoples);

    // __context.followedByCurrentUser
    var followedByCurrentUser = function(callback) {
        _rInitiator(RPeopleFollowPeople, qsCurrentUserId, peoples, 'followedByCurrentUser', callback);
    };

    // __context.numFollowPeoples
    var numFollowPeoples = function(callback) {
        _numAssociated(peoples, RPeopleFollowPeople, 'initiatorRef', 'numFollowPeoples', callback);
    };
    // __context.numFollowers
    var numFollowers = function(callback) {
        _numAssociated(peoples, RPeopleFollowPeople, 'targetRef', 'numFollowers', callback);
    };
    // __context.numCreateShows, __context.numLikeToCreateShows
    var numCreateShows = function(callback) {
        var peopleMap = {};
        Shows.aggregate([{
            '$match' : {
                '$or' : peoples.map(function(people) {
                    peopleMap[people._id.toString()] = people;
                    return {
                        'ownerRef' : people._id
                    };
                })
            }
        }, {
            '$group' : {
                '_id' : '$ownerRef',
                'numCreateShows' : {
                    '$sum' : 1
                },
                'numLikeToCreateShows' : {
                    '$sum' : '$numLike'
                }
            }
        }], function(err, results) {
            if (!err) {
                results.forEach(function(result) {
                    var people = peopleMap[result._id];
                    people.__context.numCreateShows = result.numCreateShows;
                    people.__context.numLikeToCreateShows = result.numLikeToCreateShows;
                });
            }
            callback(null, peoples);
        });
    };

    async.parallel([followedByCurrentUser, numFollowPeoples, numFollowers, numCreateShows], function(err) {
        callback(null, peoples);
    });
};

ContextHelper.appendShowContext = function(qsCurrentUserId, shows, callback) {
    shows = _prepare(shows);
    // __context.numComments
    var numComments = function(callback) {
        _numAssociated(shows, ShowComments, 'targetRef', 'numComments', callback);
    };
    // __context.likedByCurrentUser
    var likedByCurrentUser = function(callback) {
        _rInitiator(RPeopleLikeShow, qsCurrentUserId, shows, 'likedByCurrentUser', callback);
    };
    // __context.sharedByCurrentUser
    var sharedByCurrentUser = function(callback) {
        _rInitiator(RPeopleShareShow, qsCurrentUserId, shows, 'sharedByCurrentUser', callback);
    };

    // modedRef.__context.followedByCurrentUser
    async.parallel([numComments, likedByCurrentUser, sharedByCurrentUser], function(err) {
        callback(null, shows);
    });
};

ContextHelper.appendTradeContext = function(qsCurrentUserId, trades, callback) {
    trades = _prepare(trades);

    async.parallel([], function(err) {
        callback(null, trades);
    });
};

ContextHelper.appendMatchCompositionContext = function(items, callback){
    items = _prepare(items);
    var tasks = items.map(function(item){
        return function(cb){
            var config = global.qsMatcherConfig;
            var layout = {};
            if (item.matchComposition && item.matchComposition.layout && config[item.matchComposition.layout]) {      
                layout = config[item.matchComposition.layout];
            }else {
                layout = config.default;
            }

            item.__context = require('./ConfigHelper').format(layout);
            cb();
        }
    })
    async.parallel(tasks, function(err) {
        callback(null, items);
    });
}

var _prepare = function(models) {
    return models.filter(function(model) {
        return model;
    }).map(function(model) {
        model.__context = model.__context || {};
        return model;
    });
};

var _numAssociated = function(models, RModel, associateField, contextField, callback) {
    var tasks = models.map(function(model) {
        return function(callback) {
            var criteria = {
                'delete' : null
            };
            criteria[associateField] = model._id;
            RModel.count(criteria, function(err, count) {
                model.__context[contextField] = count || 0;
                callback(null);
            });
        };
    });
    async.parallel(tasks, function(err) {
        callback(null, models);
    });
};

var _rInitiator = function(RModel, initiatorRef, models, contextField, callback) {
    var tasks = models.map(function(model) {
        return function(callback) {
            if (initiatorRef) {
                RModel.findOne({
                    'initiatorRef' : initiatorRef,
                    'targetRef' : model._id
                }, function(err, relationship) {
                    model.__context[contextField] = Boolean(!err && relationship);
                    callback();
                });
            } else {
                model.__context[contextField] = false;
                callback();
            }
        };
    });
    async.parallel(tasks, function(err) {
        callback(null, models);
    });
};

var _rCreateDate = function(RModel, initiatorRef, models, contextField, callback) {
    var tasks = models.map(function(model) {
        return function(callback) {
            if (initiatorRef) {
                RModel.findOne({
                    'initiatorRef' : initiatorRef,
                    'targetRef' : model._id
                }, function(err, relationship) {
                    if (Boolean(!err && relationship)) {
                        model.__context[contextField] = relationship.create;
                    }
                    callback();
                });
            } else {
                callback();
            }
        };
    });
    async.parallel(tasks, function(err) {
        callback(null, models);
    });
};

var _initiator = function(RModel, InitiatorModel, models, contextField, callback) {
    var tasks = models.map(function(model) {
        return function(callback) {
            model.__context[contextField] = {};

            RModel.findOne({
                'targetRef' : model._id
            }, function(err, relationship) {
                if (!err && relationship) {
                    InitiatorModel.findOne({
                        '_id' : relationship.initiatorRef
                    }, function(err, people) {
                        model.__context[contextField] = Boolean(!err && people) ? people : {};
                        callback();
                    });
                } else {
                    callback();
                }
            });
        };
    });

    async.parallel(tasks, function(err) {
        callback(null, models);
    });
};